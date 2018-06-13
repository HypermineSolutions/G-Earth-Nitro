package main.ui.scheduler;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.protocol.HMessage;
import main.protocol.HPacket;
import main.ui.SubForm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jonas on 06/04/18.
 */
public class Scheduler extends SubForm {

    private static final Interval defaultInterval = new Interval(0, 500);
    private static final HPacket defaultPacket = new HPacket(0);

    public VBox schedulecontainer;
    public GridPane header;
    public ScrollPane scrollpane;

    public Button btn_addoredit;

    public TextField txt_delay;
    public ToggleGroup scheduler_dest;
    public TextField txt_packet;
    public RadioButton rb_incoming;
    public RadioButton rb_outgoing;

    public Button btn_clear;
    public Button btn_save;
    public Button btn_load;

    private ScheduleItem isBeingEdited = null;

    private List<ScheduleItem> scheduleItemList = new ArrayList<>();


    public void initialize() {
        scrollpane.widthProperty().addListener(observable -> header.setPrefWidth(scrollpane.getWidth()));
        scheduler_dest.selectToggle(scheduler_dest.getToggles().get(0));

        txt_packet.textProperty().addListener(event -> Platform.runLater(this::updateUI));
        txt_delay.textProperty().addListener(event -> Platform.runLater(this::updateUI));

        btn_clear.setTooltip(new Tooltip("Clear all items"));
        btn_save.setTooltip(new Tooltip("Save to file"));
        btn_load.setTooltip(new Tooltip("Load from file"));

        updateUI();

        new Thread(() -> {
            long t = System.currentTimeMillis();
            long changed = 1;

            Set<ScheduleItem> set = new HashSet<>();

            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                set.clear();
                for (int i = scheduleItemList.size() - 1; i >= 0; i--) {
                    set.add(scheduleItemList.get(i));
                }

                for (ScheduleItem item : set) {
                    if (!item.getPausedProperty().get()) {
                        Interval cur = item.getDelayProperty().get();
                        for (int i = 0; i < changed; i++) {
                            if ((t - i) % cur.getDelay() == cur.getOffset()) {
                                if (item.getDestinationProperty().get() == HMessage.Side.TOSERVER) {
                                    getHConnection().sendToServerAsync(item.getPacketProperty().get());
                                }
                                else {
                                    getHConnection().sendToClient(item.getPacketProperty().get());
                                }
                            }
                        }
                    }

                }

                long newT = System.currentTimeMillis();
                changed = newT - t;
                t = newT;
            }
        }).start();

    }

    public static boolean stringIsNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private void updateUI() {
        btn_addoredit.setDisable(!Interval.isValid(txt_delay.getText()) || new HPacket(txt_packet.getText()).isCorrupted());
    }

    public void scheduleBtnClicked(ActionEvent actionEvent) {
        if (isBeingEdited == null) {
            HPacket packet = new HPacket(txt_packet.getText());
            if (packet.isCorrupted()) return;

            ScheduleItem newItem = new ScheduleItem(
                    scheduleItemList.size(),
                    false,
                    new Interval(txt_delay.getText()),
                    new HPacket(txt_packet.getText()),
                    rb_incoming.isSelected() ? HMessage.Side.TOCLIENT : HMessage.Side.TOSERVER);

            addItem(newItem);
        }
        else {

            isBeingEdited.getPacketProperty().set(new HPacket(txt_packet.getText()));
            isBeingEdited.getDelayProperty().set(new Interval(txt_delay.getText()));
            isBeingEdited.getDestinationProperty().set(rb_incoming.isSelected() ? HMessage.Side.TOCLIENT : HMessage.Side.TOSERVER);
            isBeingEdited.isUpdatedTrigger();

            isBeingEdited = null;
            setInputDefault();
        }

    }

    private void addItem(ScheduleItem newItem) {
        new ScheduleItemContainer(newItem, schedulecontainer, scrollpane);
        scheduleItemList.add(newItem);


        newItem.onDelete(observable -> {
            if (isBeingEdited == newItem) {
                setInputDefault();
                isBeingEdited = null;
            }
            scheduleItemList.remove(newItem);
            for (int i = 0; i < scheduleItemList.size(); i++) {
                scheduleItemList.get(i).getIndexProperty().set(i);
            }
        });
        newItem.onEdit(observable -> {
            if (isBeingEdited != null) {
                isBeingEdited.isUpdatedTrigger();
            }

            if (isBeingEdited != newItem) {
                txt_packet.setText(newItem.getPacketProperty().get().toString());
                txt_delay.setText(newItem.getDelayProperty().get().toString());
                rb_incoming.setSelected(newItem.getDestinationProperty().get() == HMessage.Side.TOCLIENT);
                rb_outgoing.setSelected(newItem.getDestinationProperty().get() == HMessage.Side.TOSERVER);

                isBeingEdited = newItem;
                btn_addoredit.setText("Edit schedule item"); //Add to scheduler
                updateUI();
                newItem.onIsBeingUpdatedTrigger();
            }
            else {
                setInputDefault();
                isBeingEdited.isUpdatedTrigger();
                isBeingEdited = null;
            }
        });
    }

    private void setInputDefault() {
        txt_delay.setText(defaultInterval.toString());
        txt_packet.setText(defaultPacket.toString());
        rb_incoming.setSelected(true);
        rb_outgoing.setSelected(false);

        btn_addoredit.setText("Add to scheduler");
        updateUI();
    }


    private void clear() {
        for (int i = scheduleItemList.size() - 1; i >= 0; i--) {
            scheduleItemList.get(i).delete();
        }
    }
    private void load(List<ScheduleItem> list) {
        clear();

        for (ScheduleItem item : list) {
            addItem(item);
        }
    }


    public void clearBtnClicked(ActionEvent actionEvent) {
        clear();
    }

    public void saveBtnClicked(ActionEvent actionEvent) {
    }

    public void loadBtnClicked(ActionEvent actionEvent) {
    }
}
