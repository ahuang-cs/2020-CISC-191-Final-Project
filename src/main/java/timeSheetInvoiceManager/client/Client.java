package timeSheetInvoiceManager.client;

import timeSheetInvoiceManager.project.Project;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Client {
    public static final Client NONE = new Client("NONE client", -1.0, "NONE address");

    @Id
    private String id;

    private String name;
    private String address;

    private Double rate;
    private boolean isActive;

    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @MapKey(name = "name")
    private Map<String, Project> projects = new HashMap<>();


    protected Client() {
    }

    public Client(String name, Double hourlyRate, String address) {
        setName(name);
        this.address = address;
        this.rate = hourlyRate;
        isActive = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.id = name;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if(!isActive) {
            projects.forEach((name, project) -> project.setActive(false));
        }
    }

    public void addProject(Project project) {
        projects.put(project.getName(), project);
    }

    public void replaceProject(Project project) {
        projects.replace(project.getName(), project);
    }

    public void removeProject(Project project) {
        projects.remove(project.getName());
    }

    public Map<String, Project> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, Project> projects) {
        this.projects = projects;
    }

    public Project getProject(String name) {
        return projects.getOrDefault(name, Project.NONE);
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
        this.name = id;
    }

    @Override
    public String toString() {
        return "Client {" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rate='" + rate + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
