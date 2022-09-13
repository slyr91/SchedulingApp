package calendar.wrappers;

import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Month {

    private LocalDate monthOfDate;
    private GridPane monthNode;
    private int year;
    private List<Day> days;

    public LocalDate getMonthOfDate() {
        return monthOfDate;
    }

    public void setMonthOfDate(LocalDate monthOfDate) {
        this.monthOfDate = monthOfDate;
    }

    public GridPane getMonthNode() {
        return monthNode;
    }

    public void setMonthNode(GridPane monthNode) {
        this.monthNode = monthNode;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
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
        Month month = (Month) o;
        return monthOfDate.equals(month.monthOfDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthOfDate);
    }
}
