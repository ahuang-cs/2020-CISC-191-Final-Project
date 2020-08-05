/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timeSheetInvoiceManager.controller;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import timeSheetInvoiceManager.client.Client;
import timeSheetInvoiceManager.client.ClientRepository;
import timeSheetInvoiceManager.project.ProjectRepository;

import java.util.Optional;

import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import timeSheetInvoiceManager.project.Project;
import timeSheetInvoiceManager.services.MainServiceCoordinator;
import timeSheetInvoiceManager.timesheet.TimeSheet;

/**
 * @author xaboo
 */
@Component
@FxmlView("clients.fxml")
public class ClientsController implements Initializable {

    private ClientRepository clientRepository;
    private Client client;
    private ProjectRepository projectRepository;
    
    @FXML
    private Button btnSave;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtProject;
    @FXML
    private TextArea txtAddress;
    @FXML
    private TextField txtRate;
    @FXML
    private ListView<String> listContacts;
    private ObservableList<String> clientList = FXCollections.observableArrayList();


    /**
     * @param clientRepository
     */
    @Autowired
    public ClientsController(ClientRepository clientRepository, ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository  = projectRepository;
        MainServiceCoordinator.getInstance().setClientsController(this);
    }

    /**
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Clients Controller initialized");

        reloadClientListView();
        resetInputFields();
    }

    @FXML
    public void clientListClicked(MouseEvent mouseEvent) {
        System.out.println("Client list Clicked");
        Client client = getClientFromListView();

        if (client != Client.NONE) {
            Map<String, Project> thisProject = client.getProjects();
            thisProject.forEach((key, value) -> {
                //System.out.println("Key = " + key + ", Value = " + value);
                this.txtProject.setText(value.getName());
            });
            
            this.txtName.setText(client.getName());
            this.txtRate.setText(client.getRate().toString());
            this.txtAddress.setText(client.getAddress());
        }
        else {
            resetInputFields();
        }
    }

    /**
     * @param actionEvent
     */
    @FXML
    public void btnSaveClicked(ActionEvent actionEvent) {
        Client client = getClientFromListView();
        if (client != Client.NONE) {
            Integer previousID = client.getId();
            try {
                client.setName(this.txtName.getText());
                //TODO: make sure that the app doesn't crash if we can't parse this double
                client.setRate(Double.parseDouble(txtRate.getText()));
                client.setAddress(this.txtAddress.getText());
                //Add project if name is different
                if(client.getProject(txtProject.getText()).getName() != txtProject.getText()) {
                    Project project = new Project(txtProject.getText(), Double.parseDouble(txtRate.getText()), client);
                    //System.out.println(project);

                    //Create time sheet for this project
                    TimeSheet timeSheetNew = new TimeSheet(project, LocalDate.now(), LocalDate.now().plusMonths(1));
                    //System.out.println("timeSheetNew = " + timeSheetNew);

                    project.addTimeSheet(timeSheetNew);
                    client.addProject(project);                
                }
                
                clientRepository.save(client);
                // Changing the name changes the ID, so we have to delete the extraneous client if the names are different
                if (!client.getName().equals(previousID)) {
                    clientRepository.deleteById(previousID);
                }
            } catch (NumberFormatException e) {
                System.out.println(e);
                System.out.println("Rate is either empty or not a number");
            }

            //Update the projects controller whenever we save the client here
            reloadClientListView();
            updateProjectTabClientListView();
        }
    }

    @FXML
    public void btnRemoveClicked(ActionEvent actionEvent) {
        Client client = getClientFromListView();
        if (client != Client.NONE) {
            clientRepository.delete(client);
            System.out.println("Client Removed: " + client);
        }

        reloadClientListView();
        resetInputFields();
        updateProjectTabClientListView();
    }

    @FXML
    public void btnAddClicked(ActionEvent actionEvent) {
        try {
            Client newClient = new Client(txtName.getText(), Double.parseDouble(txtRate.getText()), txtAddress.getText());
            Project project = new Project(txtProject.getText(), Double.parseDouble(txtRate.getText()), newClient);
            //System.out.println(project);

            //Create time sheet for this project
            TimeSheet timeSheetNew = new TimeSheet(project, LocalDate.now(), LocalDate.now().plusMonths(1));
            //System.out.println("timeSheetNew = " + timeSheetNew);

            project.addTimeSheet(timeSheetNew);
            newClient.addProject(project);

            clientRepository.save(newClient);
            System.out.println("Client saved");

        } catch (NumberFormatException e) {
            System.out.println("hourly rate is not a double");
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println("Rate is empty");
            System.out.println(e);
        }

        //Show names to Contact list
        reloadClientListView();
    }

    public void btnActiveClicked(ActionEvent actionEvent) {
        System.out.println("Client Status changed");
    }

    private Client getClientFromListView() {

        String clientName = listContacts.getSelectionModel().getSelectedItem();
        System.out.println(clientName);
        if (clientName != null) {
            Optional<Client> selectedClient = clientRepository.findByName(clientName);
            if (!selectedClient.isPresent()) {
                throw new NullPointerException("client with name: " + clientName + " doesn't exist!");
            } else {
                return selectedClient.get();
            }
        } else {
            return Client.NONE;
        }

    }

    private void reloadClientListView() {
        listContacts.getItems().clear();
        listContacts.setItems(clientList);
        clientRepository.findAll().forEach((client) -> {
            System.out.println("Adding client to list: " + client.toString());
            clientList.add(client.getName());
        });
    }

    private void updateProjectTabClientListView() {
        ProjectsController projectsControllerInstance = MainServiceCoordinator.getInstance().getProjectsController();
        if (projectsControllerInstance != null) {
            projectsControllerInstance.reloadClientList();
        }
    }

    private void resetInputFields() {
        txtName.setText("");
        txtName.setPromptText("Name");

        txtRate.setText("");
        txtRate.setPromptText("Hourly Rate");

        txtProject.setText("");
        txtProject.setPromptText("Project Name");

        txtAddress.setText("");
        txtAddress.setPromptText("");
    }

}
