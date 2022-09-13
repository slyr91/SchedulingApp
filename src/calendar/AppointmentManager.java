package calendar;

import application.DatabaseConnectionManager;
import application.Main;
import calendar.nodefactory.LabelFactory;
import calendar.wrappers.Day;
import calendar.wrappers.Month;
import calendar.wrappers.Week;
import controllers.AppointmentEditorController;
import controllers.CalendarFrameController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logging.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class AppointmentManager {

    private final CallBackController controller;
    private Week weekOf = null;
    private Month monthOf = null;
    private List<Appointment> todaysAppointments = null;
    private ResourceBundle resources;
    private final double weekLabelMaxWidth = Integer.MAX_VALUE;

    public AppointmentManager(ResourceBundle resources, CallBackController controller) {
        this.resources = resources;
        this.controller = controller;
        todaysAppointments = loadTodaysAppointments();
    }

    private static ArrayList<Appointment> loadTodaysAppointments() {

        ArrayList<Appointment> appointments = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet resultSet = st.executeQuery("SELECT * FROM appointment WHERE userId=" + Main.getUser().getUserID() +
                    " AND DATE(start)='" + LocalDate.now() + "'");

            while (resultSet.next()) {
                Appointment appointment = new Appointment(resultSet.getInt("appointmentId"), resultSet.getInt("customerId"),
                        resultSet.getInt("userId"), resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getString("location"), resultSet.getString("contact"), resultSet.getString("type"),
                        resultSet.getTimestamp("start").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                        resultSet.getTimestamp("end").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                        resultSet.getTimestamp("createDate").toLocalDateTime().toLocalDate(), resultSet.getString("createdBy"),
                        resultSet.getTimestamp("lastUpdate").toLocalDateTime(), resultSet.getString("lastUpdateBy"));

                appointments.add(appointment);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return appointments;
    }

    public Week getWeekOf(LocalDate date) {
        int dayOfWeekValue = date.getDayOfWeek().getValue();
        LocalDate weekStart = date.minusDays(dayOfWeekValue % 7); //A new week starts on a Sunday.
        LocalDate weekEnd = date.plusDays(6 - dayOfWeekValue % 7); //A week ends on a Saturday.

        LabelFactory labelFactory = new LabelFactory();

        Week week = new Week();
        week.setWeekOfDate(date);
        week.setWeekOf(weekStart.format(DateTimeFormatter.ofPattern("MMMM d")) + " - " + weekEnd.format(DateTimeFormatter.ofPattern("MMMM d")));

        if (week.equals(weekOf)) {
            return weekOf;
        }

        GridPane weekNode = new GridPane();

        LocalDate dayOfWeekDate = weekStart;
        for(int i = 0; i < 7; i++, dayOfWeekDate = dayOfWeekDate.plusDays(1)) {
            Label dayOfWeekLabel = labelFactory.setTextWrapEnabled(true).getLabel(dayOfWeekDate.format(DateTimeFormatter.ofPattern("E", Locale.getDefault())), Pos.CENTER);
            weekNode.add(dayOfWeekLabel, i, 0);
        }

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth((double) (100/7));
        for(int i = 0; i < 7; i++) {
            weekNode.getColumnConstraints().add(col);
        }

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(5);
        weekNode.getRowConstraints().add(row1);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(95);
        weekNode.getRowConstraints().add(row2);

        weekNode.getStylesheets().add("calendar/weekStyle.css");

        try (Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
             Statement st = conn.createStatement()) {

            ResultSet result = st.executeQuery("SELECT * FROM appointment WHERE userId=" + Main.getUser().getUserID() + " AND start BETWEEN '" +
                    weekStart.toString() + "' AND '" + weekEnd.toString() + "'");

            VBox[] daysOfWeek = new VBox[7];

            for(int i = 0; i < daysOfWeek.length; i++) {
                daysOfWeek[i] = new VBox();
                daysOfWeek[i].getStyleClass().add("vbox");
            }

            if (result.next()) {

                List<Appointment> appointments = new ArrayList<>();

                do {
                    appointments.add(new Appointment(result.getInt("appointmentId"), result.getInt("customerId"), result.getInt("userId"),
                            result.getString("title"), result.getString("description"), result.getString("location"), result.getString("contact"),
                            result.getString("type"), result.getTimestamp("start").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                            result.getTimestamp("end").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                            result.getDate("createDate").toLocalDate(), result.getString("createdBy"), result.getTimestamp("lastUpdate").toLocalDateTime(),
                            result.getString("lastUpdateBy")));
                } while (result.next());

                appointments.stream().forEach(a -> {
                    Label label = labelFactory.setMaxWidth(weekLabelMaxWidth).setTextWrapEnabled(true).getLabel(a.getStart().toLocalTime() +
                            " to " + a.getEnd().toLocalTime() + " - " + a.getTitle(), "labelAppointment");

                    ContextMenu contextMenu = getAppointmentContextMenu(a);
                    label.setContextMenu(contextMenu);

                    if (a.getStart().toLocalDate().isEqual(weekStart)) {
                        daysOfWeek[0].getChildren().add(label);
                    } else {
                        daysOfWeek[a.getStart().toLocalDate().getDayOfWeek().getValue()]
                                .getChildren().add(label);
                    }
                });

            }

            LocalDate current = weekStart;
            int columnNum = 0;
            List<Day> days = new ArrayList<>();
            for (VBox dayNode : daysOfWeek) {

                Day day = new Day();
                day.setDate(current);

                dayNode.getStyleClass().add("weekday");

                Label label = labelFactory.setTextWrapEnabled(false)
                        .getLabel(current.format(DateTimeFormatter.ofPattern("d")), Pos.CENTER, "labelDate");

                dayNode.getChildren().add(0, label);

                if(current.getDayOfWeek().getValue() == 6 || current.getDayOfWeek().getValue() == 7) {
                    dayNode.getStyleClass().add("weekendDay");
                    label.getStyleClass().add("weekendDateLabel");
                }

                if(current.isEqual(LocalDate.now())) {
                    label.getStyleClass().add("today");
                    dayNode.getStyleClass().add("todaysDay");
                }

                if (dayNode.getChildren().size() == 1) {
                    Label noAppLabel = labelFactory.setMaxWidth(weekLabelMaxWidth).setTextWrapEnabled(true)
                            .getLabel(resources.getString("appointmentsNoneYet"), "labelAppointment");
                    dayNode.getChildren().add(noAppLabel);
                }

                weekNode.add(dayNode, columnNum++, 1);

                day.setDayNode(dayNode);
                days.add(day);

                current = current.plusDays(1);
            }

            week.setDays(days);


        } catch (SQLException throwables) {
            Logger.getInstance().error("Something went wrong with the MySQL Connection while generating a week.");
        }

        week.setWeekNode(weekNode);

        weekOf = week;
        return weekOf;
    }

    private ContextMenu getAppointmentContextMenu(Appointment appointment) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem viewMenuItem = new MenuItem(resources.getString("view"));
        viewMenuItem.setOnAction(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/AppointmentEditor.fxml"), resources);
                Stage viewStage = new Stage();
                Parent root = loader.load();
                AppointmentEditorController appointmentEditorController = loader.getController();

                appointmentEditorController.setAppointment(appointment);
                appointmentEditorController.enableEditMode();
                appointmentEditorController.onReturnAction(controller, (c) -> {
                    if(c instanceof CalendarFrameController) {
                        invalidateCache();
                        ((CalendarFrameController) c).refreshAppointments();
                    }
                });
                viewStage.setScene(new Scene(root));
                viewStage.show();

            } catch (IOException e) {
                Logger.getInstance().error("There was an error loading the AppointmentEditor from the view context menu.");
            }

        });

        MenuItem deleteMenuItem = new MenuItem(resources.getString("delete"));
        deleteMenuItem.setOnAction(event -> {
            Appointment.deleteAppointment(appointment);
            if(controller instanceof CalendarFrameController) {
                invalidateCache();
                ((CalendarFrameController) controller).refreshAppointments();
            }
        });

        contextMenu.getItems().addAll(viewMenuItem, deleteMenuItem);
        return contextMenu;
    }

    public Month getMonthOf(LocalDate date) {
        LocalDate firstOfTheMonth = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endOfTheMonth = LocalDate.of(date.plusMonths(1).getYear(), date.plusMonths(1).getMonth(), 1).minusDays(1);

        LabelFactory labelFactory = new LabelFactory();

        Month month = new Month();
        month.setMonthOfDate(date);
        month.setYear(date.getYear());

        if(month.equals(monthOf)) {
            return monthOf;
        }

        GridPane monthGrid = new GridPane();

        monthGrid.getStylesheets().add("calendar/monthStyle.css");

        LocalDate firstDayOfWeek = firstOfTheMonth.minusDays(firstOfTheMonth.getDayOfWeek().getValue());
        for (int i = 0; i < 7; i++, firstDayOfWeek = firstDayOfWeek.plusDays(1)) {
            Label dayOfWeekLabel = labelFactory.setTextWrapEnabled(true).getLabel(firstDayOfWeek.format(DateTimeFormatter.ofPattern("E", Locale.getDefault())), Pos.CENTER);
            monthGrid.add(dayOfWeekLabel, i, 0);
        }

        //Setup column constraints.
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth((double) (100/7));

        //Setup row constraints.
        RowConstraints weekDayRow = new RowConstraints();
        weekDayRow.setPercentHeight(5);
        monthGrid.getRowConstraints().add(weekDayRow);

        RowConstraints weekRows = new RowConstraints();
        weekRows.setPercentHeight((double) (95/5));

        for(int i = 0; i < 7; i++) {
            monthGrid.getColumnConstraints().add(col);
        }

        for(int i = 0; i < 5; i++) {
            monthGrid.getRowConstraints().add(weekRows);
        }

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()) {

            ResultSet result = st.executeQuery("SELECT * FROM appointment WHERE userId =" + Main.getUser().getUserID() + " AND start BETWEEN '" +
                    firstOfTheMonth.toString() + "' AND '" + endOfTheMonth.toString() + "'");

            int numOfFillerDaysBeforeFirst = firstOfTheMonth.getDayOfWeek().getValue() % 7;
            List<Day> days = new ArrayList<>();

            //First let's get the appointments into a list.
            List<Appointment> appointments = new ArrayList<>();

            if(result.next()) {

                do {
                    appointments.add(new Appointment(result.getInt("appointmentId"), result.getInt("customerId"), result.getInt("userId"),
                            result.getString("title"), result.getString("description"), result.getString("location"), result.getString("contact"),
                            result.getString("type"), result.getTimestamp("start").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                            result.getTimestamp("end").toLocalDateTime().plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds()),
                            result.getDate("createDate").toLocalDate(), result.getString("createdBy"), result.getTimestamp("lastUpdate").toLocalDateTime(),
                            result.getString("lastUpdateBy")));
                } while (result.next());

            }
            //Next lets get all the appoints lined up in the appropriate day.

            for(int i = 1; i <= date.getMonth().length(date.isLeapYear()); i++) {
                Day day = new Day();
                VBox dayNode = new VBox();

                dayNode.getStyleClass().add("vbox");

                LocalDate currentDate;

                try {
                    currentDate = LocalDate.of(date.getYear(), date.getMonth(), i);
                } catch (DateTimeException e) {
                    break;
                }

                day.setDate(currentDate);

                Label dateLabel = labelFactory.setTextWrapEnabled(true).getLabel(currentDate.format(DateTimeFormatter.ofPattern("d")), Pos.CENTER, "labelDate");

                dayNode.getChildren().add(dateLabel);

                int localDayOfMonth = i;
                appointments.stream().filter(a -> a.getStart().toLocalDate().getDayOfMonth() == localDayOfMonth)
                        .forEach(a -> {
                            Label label = labelFactory.setTextWrapEnabled(true).setMaxWidth(weekLabelMaxWidth)
                                    .getLabel(a.getStart().toLocalTime() + " to " + a.getEnd().toLocalTime() + " - " + a.getTitle(), "labelAppointment");

                            ContextMenu contextMenu = getAppointmentContextMenu(a);
                            label.setContextMenu(contextMenu);

                            dayNode.getChildren().add(label);
                        });

                //test for weekend.
                if(currentDate.getDayOfWeek().getValue() == 6 || currentDate.getDayOfWeek().getValue() == 7) {
                    dateLabel.getStyleClass().add("weekendDateLabel");
                    dayNode.getStyleClass().add("weekendDay");
                } else {
                    dayNode.getStyleClass().add("weekday");
                }

                //test for today.
                if(currentDate.equals(LocalDate.now())) {
                    dateLabel.getStyleClass().add("today");
                    dayNode.getStyleClass().add("todaysDay");
                }

                day.setDayNode(dayNode);
                days.add(day);
            }

            //Now we can add the days to the month.
            //Add the first week to monthGrid
            LocalDate currentDay = firstOfTheMonth;
            for(int i = 0; i < 7; i++) {
                currentDay = firstOfTheMonth.minusDays(numOfFillerDaysBeforeFirst - i);
                if(currentDay.isBefore(firstOfTheMonth)) {

                    VBox invalidDay = new VBox();
                    invalidDay.getStyleClass().add("vbox");
                    invalidDay.getStyleClass().add("invalidDay");

                    Label invalidLabel = labelFactory.setTextWrapEnabled(true).getLabel(currentDay.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())), Pos.CENTER, "labelDate", "invalidDateLabel");

                    invalidDay.getChildren().add(invalidLabel);

                    monthGrid.add(invalidDay, i, 1);
                } else {
                    int k = i;
                    LocalDate lamdaDay = currentDay;
                    GridPane lamdaGrid = monthGrid;
                    days.stream().filter(d -> d.getDate().isEqual(lamdaDay)).forEach(d -> lamdaGrid.add(d.getDayNode(), k, 1));
                }
            }

            //Add the rest of the month, plus filler days at the end
            currentDay = currentDay.plusDays(1);
            for(int i = 2; i < 6; i++) {

                for(int j = 0; j < 7; j++, currentDay = currentDay.plusDays(1)) {
                    if(!currentDay.isAfter(endOfTheMonth) || currentDay.isEqual(endOfTheMonth)){
                        LocalDate lamdaDay = currentDay;
                        int k = j;
                        int l = i;
                        GridPane lamdaGrid = monthGrid;
                        days.stream().filter(d -> d.getDate().isEqual(lamdaDay)).forEach(d -> lamdaGrid.add(d.getDayNode(), k, l));
                    } else {
                        VBox invalidDay = new VBox();
                        invalidDay.getStyleClass().add("vbox");
                        invalidDay.getStyleClass().add("invalidDay");

                        Label invalidLabel = labelFactory.setTextWrapEnabled(true).getLabel(currentDay.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())), Pos.CENTER, "labelDate", "invalidDateLabel");
                        invalidDay.getChildren().add(invalidLabel);

                        monthGrid.add(invalidDay, j, i);
                    }

                }
            }
            if(currentDay.isBefore(endOfTheMonth)) {

                GridPane extraWeekGrid = new GridPane();
                extraWeekGrid.getStylesheets().add("calendar/monthStyle.css");

                //Setup column constraints.
                ColumnConstraints col2 = new ColumnConstraints();
                col2.setPercentWidth((double) (100/7));

                //Setup row constraints.
                RowConstraints weekDayRow2 = new RowConstraints();
                weekDayRow2.setPercentHeight(5);
                extraWeekGrid.getRowConstraints().add(weekDayRow2);

                RowConstraints weekRows2 = new RowConstraints();
                weekRows2.setPercentHeight((double) (95/6));

                for(int i = 0; i < 7; i++) {
                    extraWeekGrid.getColumnConstraints().add(col2);
                }

                for(int i = 0; i < 6; i++) {
                    extraWeekGrid.getRowConstraints().add(weekRows2);
                }

                extraWeekGrid.getChildren().addAll(monthGrid.getChildren());

                for(int i = 0; i < 7; i++, currentDay = currentDay.plusDays(1)) {
                    if(!currentDay.isAfter(endOfTheMonth) || currentDay.isEqual(endOfTheMonth)){
                        LocalDate lamdaDay = currentDay;
                        int k = i;
                        days.stream().filter(d -> d.getDate().isEqual(lamdaDay)).forEach(d -> extraWeekGrid.add(d.getDayNode(), k, 6));
                    } else {
                        VBox invalidDay = new VBox();
                        invalidDay.getStyleClass().add("vbox");
                        invalidDay.getStyleClass().add("invalidDay");

                        Label invalidLabel = labelFactory.setTextWrapEnabled(true).getLabel(currentDay.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())), Pos.CENTER, "labelDate", "invalidDateLabel");
                        invalidDay.getChildren().add(invalidLabel);

                        extraWeekGrid.add(invalidDay, i, 6);
                    }
                }
                monthGrid = extraWeekGrid;
            }

            month.setDays(days);


        } catch (SQLException throwables) {
            Logger.getInstance().error("Something went wrong with the MySQL Connection while generating a month.");
        }

        month.setMonthNode(monthGrid);

        monthOf = month;
        return month;
    }


    public void invalidateCache() {
        weekOf = null;
        monthOf = null;
    }

    public boolean isAppointmentScheduledSoon() {
        boolean appointmentSoon = false;

        long numOfAppointmentsSoon = todaysAppointments.stream().filter(a -> a.getStart().isBefore(LocalDateTime.now().plusMinutes(15)) && a.getStart().isAfter(LocalDateTime.now())).count();

        if(numOfAppointmentsSoon > 0) {
            appointmentSoon = true;
        }

        return appointmentSoon;
    }
}
