package calendar.wrappers;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Week {
    private String weekOf;
    private LocalDate weekOfDate;
    private List<Day> days;
    private Node weekNode;

    public String getWeekOf() {
        return weekOf;
    }

    public void setWeekOf(String weekOf) {
        this.weekOf = weekOf;
    }

    public LocalDate getWeekOfDate() {
        return weekOfDate;
    }

    public void setWeekOfDate(LocalDate weekOfDate) {
        this.weekOfDate = weekOfDate;
    }

    public Node getWeekNode() {
        return weekNode;
    }

    public void setWeekNode(Node weekNode) {
        this.weekNode = weekNode;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        return weekOf.equals(week.weekOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekOf);
    }
}
