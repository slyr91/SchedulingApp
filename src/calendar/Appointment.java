package calendar;

import application.DatabaseConnectionManager;
import application.Main;
import exceptions.AppointmentOutsideBusinessHours;
import exceptions.AppointmentOverlapException;
import identity.User;

import java.sql.*;
import java.time.*;

public class Appointment {

    private Integer appointmentID;
    private Integer customerID;
    private Integer userID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDate createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    /**
     * This constructor is used when creating appointment objects from the database.
     *
     * @param appointmentID
     * @param customerID
     * @param userID
     * @param title
     * @param description
     * @param location
     * @param contact
     * @param type
     * @param start
     * @param end
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdateBy
     */
    public Appointment(Integer appointmentID, Integer customerID, Integer userID, String title, String description, String location,
                String contact, String type, LocalDateTime start, LocalDateTime end, LocalDate createDate,
                String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.appointmentID = appointmentID;
        this.customerID = customerID;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     * This constructor is used to create new appointments and is accessible program wide. It will generate default
     * values for some fields that should be generated automatically.
     *
     * @param customerID
     * @param user
     * @param title
     * @param description
     * @param location
     * @param contact
     * @param type
     * @param start
     * @param end
     */
    public Appointment(Integer customerID, User user, String title, String description, String location, String contact,
                       String type, LocalDateTime start, LocalDateTime end) {

        this(null, customerID, user.getUserID(), title, description, location, contact, type, start, end,
                LocalDate.now(), user.getUsername(), null, user.getUsername());
    }

    public static boolean createNewAppointment(Integer customerID, String title, String description,
                                               String location, String contact, String type, LocalDateTime start,
                                               LocalDateTime end) throws AppointmentOverlapException, AppointmentOutsideBusinessHours {
        boolean success = false;

        Appointment appointment = new Appointment(customerID, Main.getUser(), title, description, location, contact, type,
                start, end);

        if(!isTimeTaken(appointment.startAdjustedToServer(), appointment.endAdjustedToServer())) {

            if(isBusinessHours(appointment.getStart(), appointment.getEnd())) {
                try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                    Statement st = conn.createStatement()) {

                    int result = st.executeUpdate("INSERT INTO appointment(customerId,userId,title,description,location," +
                            "contact,type,url,start,end,createDate,createdBy,lastUpdateBy) Values(" + appointment.getCustomerID() + "," +
                            appointment.getUserID() + ",'" + appointment.getTitle() + "','" + appointment.getDescription() + "','" +
                            appointment.getLocation() + "','" + appointment.getContact() + "','" + appointment.getType() + "','" +
                            "None','" + appointment.startAdjustedToServer() + "','" + appointment.endAdjustedToServer() + "','" + appointment.getCreateDate() + "','" +
                            appointment.getCreatedBy() + "','" + appointment.getLastUpdateBy() + "')");

                    if(result > 0) {
                        success = true;
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                throw new AppointmentOutsideBusinessHours();
            }

        } else {
            throw new AppointmentOverlapException();
        }


        return success;
    }

    public LocalDateTime startAdjustedToServer() {
        return start.minusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds());
    }

    public LocalDateTime endAdjustedToServer() {
        return end.minusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds());
    }

    public static boolean updateExistingAppointment(Appointment appointment, Integer customerId, String title, String description,
                                                 String location, String contact, String type, LocalDateTime start, LocalDateTime end) throws AppointmentOverlapException, AppointmentOutsideBusinessHours {

        boolean success = false;

        Appointment newAppointment = new Appointment(customerId, Main.getUser(), title, description, location, contact, type,
                start, end);

        if(!isTimeTaken(appointment, newAppointment.startAdjustedToServer(), newAppointment.endAdjustedToServer())) {

            if(isBusinessHours(newAppointment.getStart(), newAppointment.getEnd())) {
                try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                    Statement st = conn.createStatement()) {

                    int result = st.executeUpdate("UPDATE appointment SET customerId=" + newAppointment.getCustomerID() + ", userId=" + newAppointment.getUserID() + ", title='" +
                            newAppointment.getTitle() + "', description='" + newAppointment.getDescription() + "', location='" + newAppointment.getLocation() +
                            "', contact='" + newAppointment.getContact() + "', type='" + newAppointment.getType() + "', start='" + newAppointment.startAdjustedToServer() +
                            "', end='" + newAppointment.endAdjustedToServer() + "' WHERE appointmentId=" + appointment.getAppointmentID());

                    if(result > 0) {
                        success = true;
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                throw new AppointmentOutsideBusinessHours();
            }

        } else {
            throw new AppointmentOverlapException();
        }


        return success;

    }

    public static boolean isBusinessHours(LocalDateTime start, LocalDateTime end) {
        boolean businessHours = false;

        if(!(start.getDayOfWeek().equals(DayOfWeek.SATURDAY) || start.getDayOfWeek().equals(DayOfWeek.SUNDAY)) ||
                !(end.getDayOfWeek().equals(DayOfWeek.SATURDAY) || end.getDayOfWeek().equals(DayOfWeek.SUNDAY))) {

            if(start.toLocalTime().isAfter(LocalTime.of(8,0,0)) && end.toLocalTime().isBefore(LocalTime.of(17, 0 , 0))) {
                businessHours = true;
            }
        }

        return businessHours;
    }

    public static boolean isTimeTaken(LocalDateTime start, LocalDateTime end) {
        return isTimeTaken(null, start, end);
    }

    public static boolean isTimeTaken(Appointment ignoredAppointment, LocalDateTime start, LocalDateTime end) {
        boolean timeIsTaken = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
                Statement st = conn.createStatement()){

            ResultSet resultSet;
            if(ignoredAppointment != null) {
                resultSet = st.executeQuery("SELECT * FROM appointment WHERE appointmentId<>" + ignoredAppointment.getAppointmentID() + " AND (start BETWEEN '" + start +
                        "' AND '" + end + "' OR end BETWEEN '" + start + "' AND '" + end + "')");
            } else {
                resultSet = st.executeQuery("SELECT * FROM appointment WHERE start BETWEEN '" + start +
                        "' AND '" + end + "' OR end BETWEEN '" + start + "' AND '" + end + "'");
            }

            if(resultSet.next()) {
                timeIsTaken = true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  timeIsTaken;
    }



    public static boolean deleteAppointment(Appointment appointment) {
        boolean success = false;

        try(Connection conn = DriverManager.getConnection(DatabaseConnectionManager.getURL(), DatabaseConnectionManager.getUsername(), DatabaseConnectionManager.getPassword());
            Statement st = conn.createStatement()){

            int result = st.executeUpdate("DELETE FROM appointment WHERE appointmentId=" + appointment.getAppointmentID());

            if(result > 0) {
                success = true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return success;
    }

    public Integer getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(Integer appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
}
