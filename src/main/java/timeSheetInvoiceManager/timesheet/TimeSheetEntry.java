package timeSheetInvoiceManager.timesheet;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Entity
public class TimeSheetEntry {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    private LocalDate date;
    private String employeeName;
    private String description;
    private Integer projectId;
    private final String mapId;

    /**
     * How many hours this entry
     */
    private Double hours;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "timeSheet_id"
    )
    private TimeSheet timeSheet;

    protected TimeSheetEntry() {
        mapId = "";
    }

    public TimeSheetEntry(LocalDate date, String employeeName, String description, Double hours, TimeSheet timeSheet, Integer projectId) {
        this.date = date;
        this.employeeName = employeeName;
        this.description = description;
        this.hours = hours;
        this.timeSheet = timeSheet;
        this.projectId = projectId;
        mapId = date.toString() + employeeName;
        //this.id = getEntryID(projectId, employeeName, date);
    }
/*
    public TimeSheetEntry(LocalDate now, String string, String string0, long round, TimeSheet timeSheet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public double getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public TimeSheet getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheet timeSheet) {
        this.timeSheet = timeSheet;
    }

    public String getMapId() {
        return mapId;
    }

    @Override
    public String toString() {
        return "TimeSheetEntry{" +
                "id=" + id +
                ", date=" + date +
                ", employeeName='" + employeeName + '\'' +
                ", description='" + description + '\'' +
                ", hours=" + hours +
                ", projectId=" + projectId +
                ", timeSheetId=" + timeSheet +
                '}';
    }

    public Integer getEntryID() {
        return id;
        //return date.toString() + employeeName + "_" + projectId;
    }
}
