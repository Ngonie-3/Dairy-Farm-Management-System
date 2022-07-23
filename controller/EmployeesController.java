/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.DatabaseConnection;
import model.EmployeeSearchModel;
import org.controlsfx.control.Notifications;

/**
 *
 * @author ngoni
 */
public class EmployeesController implements Initializable{
    @FXML
    private RadioButton amtRadioBtn, femaleRadioBtn, maleRadioBtn, percentageRadioBtn;
    @FXML
    private TextField contactAddress, contactNumber, jobTitle, salary, empID, empName, nameTextField,listEmpSalary,listEmpName,listEmpID;
    @FXML
    private DatePicker dateHired;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TableView<EmployeeSearchModel> employeeTableView;
    @FXML
    private TableColumn<EmployeeSearchModel, Integer> employeeIDColumn;
    @FXML
    private TableColumn<EmployeeSearchModel, String> employeeNameColumn;
    @FXML
    private TableColumn<EmployeeSearchModel, String> employeeSalaryColumn;
    @FXML
    private Button refreshButton;
    ObservableList<EmployeeSearchModel> employeeSearchModelObservableList = FXCollections.observableArrayList();
    private final ToggleGroup buttons = new ToggleGroup();
    @FXML
    private ComboBox<String> employeeComboBox;
    ObservableList<String> employees = FXCollections.observableArrayList("User Type", "Administrator", "Employee");
    ObservableList<String> empList = FXCollections.observableArrayList(employees.subList(1,3));
    @FXML
    void employeesShown() {
        employeeComboBox.setItems(employees);
        employeeComboBox.getSelectionModel().select(0);
    }
    @FXML
    void listDeleteEmployee() {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setHeaderText("Do you wish to continue?");
        deleteAlert.setTitle("Confirm Deletion");
        deleteAlert.show();
        if(deleteAlert.getResult() == ButtonType.OK){
            //TODO
            Notifications deleteEmployee = Notifications.create()
                    .text("Record successfully deleted")
                    .position(Pos.TOP_RIGHT)
                    .hideCloseButton()
                    .hideAfter(Duration.seconds(3));
            deleteEmployee.darkStyle();
            deleteEmployee.showInformation();
        }
    }
    @FXML
    void listUpdateEmployee() {
        //TODO
        Notifications update = Notifications.create()
                .text("Details successfully updated")
                .position(Pos.TOP_RIGHT)
                .hideCloseButton()
                .hideAfter(Duration.seconds(3));
        update.darkStyle();
        update.showInformation();
    }
    @FXML
    void updateEmployee() {
        //TODO
        Notifications updateEmployee = Notifications.create()
                .text("Details successfully updated")
                .position(Pos.TOP_RIGHT)
                .hideCloseButton()
                .hideAfter(Duration.seconds(3));
        updateEmployee.darkStyle();
        updateEmployee.showInformation();
    }
    @FXML
    void updateSalary() {
        //TODO
        Notifications updateSalary = Notifications.create()
                .text("Details successfully updated")
                .position(Pos.TOP_RIGHT)
                .hideCloseButton()
                .hideAfter(Duration.seconds(3));
        updateSalary.darkStyle();
        updateSalary.showInformation();
    }
    @FXML
    void addEmployee() {
        add_Employee();
        if(!(employeeTableView.getItems().isEmpty())){
            add_Last_Row();
        }
        Notifications add = Notifications.create()
                .text("Details successfully saved")
                .position(Pos.TOP_RIGHT)
                .hideCloseButton()
                .hideAfter(Duration.seconds(3));
        add.darkStyle();
        add.showInformation();
        empID.clear();
        empName.clear();
        dateOfBirth.setValue(null);
        if(maleRadioBtn.isSelected()){
            maleRadioBtn.setSelected(false);
        }else if(femaleRadioBtn.isSelected()){
            femaleRadioBtn.setSelected(false);
        }
        contactNumber.clear();
        contactAddress.clear();
        employeeComboBox.getSelectionModel().select(0);
        dateHired.setValue(null);
        salary.clear();
        jobTitle.clear();
        loginPassword.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setValueToTextField();
        employeeComboBox.setItems(empList);
        buttons.getToggles().addAll(maleRadioBtn, femaleRadioBtn, percentageRadioBtn, amtRadioBtn);
        buttons.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null){
                newValue = oldValue;
            }else{
                if(newValue.equals(maleRadioBtn)){
                    femaleRadioBtn.setSelected(false);
                }else if(newValue.equals(femaleRadioBtn)){
                    maleRadioBtn.setSelected(false);
                }else if(newValue.equals(percentageRadioBtn)){
                    amtRadioBtn.setSelected(false);
                }else if(newValue.equals(amtRadioBtn)){
                    percentageRadioBtn.setSelected(false);
                }
            }
        });
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        String employeeViewQuery = "SELECT employeeID, employeeName, salary FROM dairy_farm.employees";
        refreshButton.setOnAction(actionEvent -> {
            try{
                Statement statement = connection.createStatement();
                ResultSet queryOutput = statement.executeQuery(employeeViewQuery);
                while(queryOutput.next()){
                    int employeeID = queryOutput.getInt("employeeID");
                    String employeeName = queryOutput.getString("employeeName");
                    String employeeSalary = queryOutput.getString("salary");
                    employeeSearchModelObservableList.add(new EmployeeSearchModel(employeeID, employeeName, employeeSalary));
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM dairy_farm.employees WHERE employeeID > ?");
                    preparedStatement.setInt(1, 0);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    int value = 0;
                    if(resultSet.next()){
                        value= resultSet.getInt(1);
                    }
                    if(value>0){
                        if(!(employeeTableView.getColumns().isEmpty())){
                            refreshButton.setOnAction(actionEvent1 -> {
                                Notifications info = Notifications.create()
                                        .text("Data is up to date")
                                        .position(Pos.TOP_RIGHT)
                                        .hideCloseButton()
                                        .hideAfter(Duration.seconds(3));
                                info.darkStyle();
                                info.showInformation();
                            });
                        }
                    }
                }
                search();
            }catch (SQLException e){
                Logger.getLogger(EmployeesController.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
        });
    }

    public void add_Employee(){
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connectToDB = dbConnection.getConnection();
        int ID = Integer.parseInt(empID.getText());
        String name = empName.getText();
        LocalDate dob = dateOfBirth.getValue();
        String gender="";
        if(maleRadioBtn.isSelected()){
            gender = maleRadioBtn.getText();
        }else if(femaleRadioBtn.isSelected()){
            gender = femaleRadioBtn.getText();
        }
        String phoneNumber = contactNumber.getText();
        String address = contactAddress.getText();
        String type = employeeComboBox.getSelectionModel().getSelectedItem();
        LocalDate hiringDate = dateHired.getValue();
        String employeeSalary = salary.getText();
        String title = jobTitle.getText();
        String password = loginPassword.getText();
        String insertToDB = "INSERT INTO dairy_farm.employees(employeeID, employeeName, dateOfBirth, gender," +
                "contactNumber, address, designation, dateHired, salary, jobTitle, loginPassword) VALUES ('";
        String insertDBValues = ID +"', '"+name+"', '"+dob+"', '"+gender+"', '"+phoneNumber+"', '"+address+"', '"+type+"', '"+hiringDate+"', '"+employeeSalary+"', '"+title+"', '"+password+"')";
        String DBValues = insertToDB + insertDBValues;
        try {
            Statement statement = connectToDB.createStatement();
            statement.executeUpdate(DBValues);
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private void add_Last_Row(){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        String recordQuery = "SELECT employeeID, employeeName, salary FROM dairy_farm.employees ORDER BY employeeID DESC LIMIT 1 OFFSET 0";
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet result = statement.executeQuery(recordQuery);
            result.first();
            int employeeID = result.getInt("employeeID");
            String employeeName = result.getString("employeeName");
            String salary = result.getString("salary");
            employeeSearchModelObservableList.add(new EmployeeSearchModel(employeeID, employeeName, salary));
            search();
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    private void setValueToTextField(){
        employeeTableView.setOnMouseClicked(mouseEvent -> {
            EmployeeSearchModel employeeSearchModel = employeeTableView.getItems().get(employeeTableView.getSelectionModel().getSelectedIndex());
            listEmpID.setText(String.valueOf(employeeSearchModel.getEmployeeID()));
            listEmpName.setText(employeeSearchModel.getEmployeeName());
            listEmpSalary.setText(employeeSearchModel.getEmployeeSalary());
        });
    }

    private void search(){
        employeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        employeeSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("employeeSalary"));
        employeeTableView.setItems(employeeSearchModelObservableList);
        FilteredList<EmployeeSearchModel> filteredData = new FilteredList<>(employeeSearchModelObservableList, b -> true);
        nameTextField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredData.setPredicate(employeeSearchModel -> {
            if(newValue.isEmpty()){
                return true;
            }
            String search = newValue.toLowerCase();
            if(employeeSearchModel.getEmployeeName().toLowerCase().contains(search)){
                return true;
            }
            int num = employeeSearchModel.getEmployeeID();
            return String.valueOf(num).toLowerCase().contains(search);
        }));
        SortedList<EmployeeSearchModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeeTableView.comparatorProperty());
        employeeTableView.setItems(sortedData);
    }
}
