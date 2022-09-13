package calendar.wrappers;

import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class Day {

    private LocalDate date;
    private VBox dayNode;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public VBox getDayNode() {
        return dayNode;
    }

    public void setDayNode(VBox dayNode) {
        this.dayNode = dayNode;
    }
}
