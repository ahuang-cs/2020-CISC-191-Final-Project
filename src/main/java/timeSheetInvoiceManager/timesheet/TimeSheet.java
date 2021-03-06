package timeSheetInvoiceManager.timesheet;

import timeSheetInvoiceManager.project.Project;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
public class TimeSheet {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    private LocalDate beginDate;
    private LocalDate endDate;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "timeSheet",
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @MapKey(name = "mapId")
    private final Map<String, TimeSheetEntry> entries = new HashMap<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    public TimeSheet(Project project, LocalDate beginDate, LocalDate endDate) {
        this.project = project;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    protected TimeSheet() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public TimeSheetEntry getEntry(TimeSheetEntry entry) {
        return entries.get(entry.getMapId());
    }

    //TODO: maybe check that entry is in the time sheet's month
    public void addEntry(TimeSheetEntry entry) {
        System.out.println(entry.getMapId());
        entries.put(entry.getMapId(), entry);
    }

    public Map<String, TimeSheetEntry> getEntries() {
        return entries;
    }

    public void removeEntry(TimeSheetEntry entry) {
        entries.remove(entry.getMapId());
    }

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TimeSheet{" +
                "id=" + id +
                '}';
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
