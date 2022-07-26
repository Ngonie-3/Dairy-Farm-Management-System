/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import model.AnimalRecordsSearchModel;
import model.DatabaseConnection;
import org.controlsfx.control.Notifications;

/**
 *
 * @author ngoni
 */
public class AnimalRecordsController implements Initializable {

    @FXML
    private ComboBox<String> addAnimalComboBox;
    ObservableList<String> addCombo = FXCollections.observableArrayList( "Select Animal", "Cow", "Bull", "Heifer", "Calf");
    ObservableList<String> comboList = FXCollections.observableArrayList(addCombo.subList(1,5));

    @FXML
    private ComboBox<String> animalComboBox;
    ObservableList<String> animalCombo = FXCollections.observableArrayList( "Select Animal", "Cow", "Bull", "Heifer", "Calf");
    ObservableList<String> animalList = FXCollections.observableArrayList(animalCombo.subList(1,5));
    ObservableList<AnimalRecordsSearchModel> animalRecordsSearchModelObservableList = FXCollections.observableArrayList();

    @FXML
    private TextField addAgeAtFirstService, addAnimalID, addAnimalName, addBreed, addColor, addDamID, addEarTag, addPasture;
    @FXML
    private DatePicker addBirthDate, profileBirthDate;
    @FXML
    private TextField addWeightAtBirth, addWeightAtFirstService, profileAgeAtFirstService, profileAnimalType, profileBreed;
    @FXML
    private TextField profileCurrentAge, profileDamID, profileEarTag, profileID, profileName, profilePasture, profileSireID;
    @FXML
    private TextField profileWightAtFirstService, profileWeightAtBirth, profileColor, addSireID, nameSearchField;
    @FXML
    private TableView<AnimalRecordsSearchModel> animalRecordsTable;
    @FXML
    private TableColumn<AnimalRecordsSearchModel, Integer> animalIDColumn;
    @FXML
    private TableColumn<AnimalRecordsSearchModel, String> animalNameColumn;
    @FXML
    private TableColumn<AnimalRecordsSearchModel, String> animalTypeColumn;
    @FXML
    private Button loadData, deleteAnimal, editDetails, save;
    @FXML
    private Label numOfAnimals;
    private final String identity = " Cow Found";
    private final String numOfCows = " Cows Found";
    private final String bullIdentity = " Bull Found";
    private final String numOfBulls = " Bulls Found";
    private final String heiferIdentity = " Heifer Found";
    private final String numOfHeifers = " Heifers Found";
    private final String calfIdentity = " Calf Found";
    private final String numOfCalves = " Calves Found";
    @FXML
    void deleteButtonPressed(){
       Alert newAlert = new Alert(Alert.AlertType.CONFIRMATION);
        newAlert.setHeaderText("Do you wish to continue?");
        newAlert.setTitle("Confirm Deletion");
        newAlert.showAndWait();
        if (newAlert.getResult() == ButtonType.CANCEL) {
            newAlert.close();
        } else if (newAlert.getResult() == ButtonType.OK) {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            String deleteQuery = "DELETE FROM dairy_farm.animal_records WHERE animal_name = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setString(1, profileName.getText());
                preparedStatement.executeUpdate();
            }catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
            animalRecordsSearchModelObservableList.remove(animalRecordsTable.getSelectionModel().getSelectedItem());
            profileBirthDate.setValue(null);
            profileAgeAtFirstService.clear();
            profileAnimalType.clear();
            profileBreed.clear();
            profileColor.clear();
            profileCurrentAge.clear();
            profileDamID.clear();
            profileEarTag.clear();
            profileID.clear();
            profileName.clear();
            profilePasture.clear();
            profileSireID.clear();
            profileWeightAtBirth.clear();
            profileWightAtFirstService.clear();
            Notifications notify = Notifications.create()
                    .text("Record successfully deleted")
                    .position(Pos.TOP_RIGHT)
                    .hideCloseButton()
                    .hideAfter(Duration.seconds(3));
            notify.darkStyle();
            notify.showInformation();
        }
    }
    @FXML
    void saveAnimalPressed(){
        if(addAnimalComboBox.getValue() == null || addAnimalID.getText().isEmpty() || addAnimalName.getText().isEmpty()
                ||addEarTag.getText().isEmpty() || addSireID.getText().isEmpty() || addDamID.getText().isEmpty() ||
                addBreed.getText().isEmpty() || addColor.getText().isEmpty() || addWeightAtBirth.getText().isEmpty() ||
                addAgeAtFirstService.getText().isEmpty() || addPasture.getText().isEmpty() || addBirthDate.getValue() == null){
            Notifications notifications = Notifications.create()
                    .text("Please enter all details before saving")
                    .position(Pos.TOP_RIGHT)
                    .hideCloseButton()
                    .hideAfter(Duration.seconds(3));
            notifications.darkStyle();
            notifications.showError();
        }else{
            add_Animal_Records();
            if(!(animalRecordsTable.getItems().isEmpty())){
                addLastRow();
            }
            Notifications newNotification = Notifications.create()
                    .text("Details successfully saved")
                    .position(Pos.TOP_RIGHT)
                    .hideCloseButton()
                    .hideAfter(Duration.seconds(3));
            newNotification.darkStyle();
            newNotification.showInformation();
            addAnimalID.clear();
            addAnimalName.clear();
            addEarTag.clear();
            addSireID.clear();
            addDamID.clear();
            addBreed.clear();
            addColor.clear();
            addWeightAtBirth.clear();
            addWeightAtFirstService.clear();
            addAgeAtFirstService.clear();
            addPasture.clear();
            addBirthDate.setValue(null);
            addAnimalComboBox.setItems(addCombo);
            addAnimalComboBox.getSelectionModel().select(0);
        }
    }
    @FXML
    void comboBoxShown(){
        addAnimalComboBox.setItems(addCombo);
        addAnimalComboBox.getSelectionModel().select(0);
    }
    @FXML
    void itemsShown(){
        animalComboBox.setItems(addCombo);
        animalComboBox.getSelectionModel().select(0);
    }
    @FXML
    void clearTable(){
        animalRecordsSearchModelObservableList.clear();
        profileID.clear();
        profileName.clear();
        profileEarTag.clear();
        profileBirthDate.setValue(null);
        profileCurrentAge.clear();
        profileAnimalType.clear();
        profileSireID.clear();
        profileDamID.clear();
        profileBreed.clear();
        profileColor.clear();
        profileWeightAtBirth.clear();
        profileAgeAtFirstService.clear();
        profileWightAtFirstService.clear();
        profilePasture.clear();
    }
    @FXML
    void saveButtonPressed(){
        Notifications updateNotification = Notifications.create()
                   .text("Details successfully updated")
                   .position(Pos.TOP_RIGHT)
                   .hideCloseButton()
                   .hideAfter(Duration.seconds(3));
        updateNotification.darkStyle();
        updateNotification.showInformation();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        String updateQuery = "UPDATE dairy_farm.animal_records set ear_tag = ?, sire_ID = ?, dam_ID = ?, color = ?, breed = ?, pasture = ?, " +
                "weight_at_birth = ?, age_at_first_service = ?, weight_at_first_service = ? WHERE animal_name = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, profileEarTag.getText());
            preparedStatement.setInt(2, Integer.parseInt(profileSireID.getText()));
            preparedStatement.setInt(3, Integer.parseInt(profileDamID.getText()));
            preparedStatement.setString(4, profileColor.getText());
            preparedStatement.setString(5, profileBreed.getText());
            preparedStatement.setString(6, profilePasture.getText());
            preparedStatement.setString(7, profileWeightAtBirth.getText());
            preparedStatement.setString(8, profileAgeAtFirstService.getText());
            preparedStatement.setString(9, profileWightAtFirstService.getText());
            preparedStatement.setString(10, profileName.getText());
            preparedStatement.executeUpdate();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        animalRecordsSearchModelObservableList.clear();
        if(animalRecordsTable.getItems().isEmpty()){
            loadData.setOnAction(actionEvent -> retrieveAnimals());
        }
        profileEarTag.clear();
        profileSireID.clear();
        profileDamID.clear();
        profileColor.clear();
        profileBreed.clear();
        profilePasture.clear();
        profileWeightAtBirth.clear();
        profileWightAtFirstService.clear();
        profileAgeAtFirstService.clear();
    }
    @FXML
    void editDetailsPressed(){
        save.setDisable(false);
        profileAgeAtFirstService.setEditable(true);
        profileBreed.setEditable(true);
        profileColor.setEditable(true);
        profileDamID.setEditable(true);
        profileEarTag.setEditable(true);
        profilePasture.setEditable(true);
        profileSireID.setEditable(true);
        profileWeightAtBirth.setEditable(true);
        profileWightAtFirstService.setEditable(true);
        profileWeightAtBirth.setStyle("-fx-control-inner-background: #FAF9F9");
        profileWightAtFirstService.setStyle("-fx-control-inner-background: #FAF9F9");
        profileSireID.setStyle("-fx-control-inner-background: #FAF9F9");
        profilePasture.setStyle("-fx-control-inner-background: #FAF9F9");
        profileEarTag.setStyle("-fx-control-inner-background: #FAF9F9");
        profileDamID.setStyle("-fx-control-inner-background: #FAF9F9");
        profileColor.setStyle("-fx-control-inner-background: #FAF9F9");
        profileBreed.setStyle("-fx-control-inner-background: #FAF9F9");
        profileAgeAtFirstService.setStyle("-fx-control-inner-background: #FAF9F9");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        setCellValueToTextField();
        addAnimalComboBox.setItems(comboList);
        animalComboBox.setItems(animalList);
        retrieveAnimals();
        numberOfAnimals();
        editDetails.setDisable(true);
        deleteAnimal.setDisable(true);
        save.setDisable(true);
    }
    private void add_Animal_Records(){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        String type = addAnimalComboBox.getSelectionModel().getSelectedItem();
        LocalDate date = addBirthDate.getValue();
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(date, currentDate);
        String currentAge = period.getYears()+"years "+ period.getMonths()+"months";
        String ID = addAnimalID.getText();
        String name = addAnimalName.getText();
        String tag = addEarTag.getText();
        String sire = addSireID.getText();
        String dam = addDamID.getText();
        String breed = addBreed.getText();
        String color = addColor.getText();
        String weight = addWeightAtBirth.getText();
        String age = addAgeAtFirstService.getText();
        String serviceWeight = addWeightAtFirstService.getText();
        String pasture = addPasture.getText();
        String insertIntoDB = "INSERT INTO dairy_farm.animal_records(animal_ID, animal_type, birth_date, current_age, " +
                "animal_name, ear_tag, sire_ID, dam_ID, breed, color, weight_at_birth, age_at_first_service, " +
                "weight_at_first_service, pasture)VALUES('";
        String insertDBValues = ID+"', '"+type+"', '"+date+"', '"+currentAge+"', '"+name+"', '"+tag+"', '"+sire+"', '"+dam+"', '"+breed+"', '"
                +color+"', '"+weight+"', '"+age+"', '"+serviceWeight+"', '"+pasture+"')";
        String dbValues = insertIntoDB + insertDBValues;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(dbValues);
            connection.close();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    private void setCellValueToTextField(){
        animalRecordsTable.setOnMouseClicked(mouseEvent -> {
            AnimalRecordsSearchModel animal = animalRecordsTable.getItems().get(animalRecordsTable.getSelectionModel().getSelectedIndex());
            profileID.setText(String.valueOf(animal.getID()));
            profileName.setText(animal.getName());
            profileAnimalType.setText(animal.getAnimalType());
            profileEarTag.setText(animal.getEarTag());
            profileSireID.setText(String.valueOf(animal.getSireID()));
            profileDamID.setText(String.valueOf(animal.getDamID()));
            profileColor.setText(animal.getColor());
            profileBreed.setText(animal.getBreed());
            profilePasture.setText(animal.getPasture());
            profileBirthDate.setValue(LocalDate.parse(animal.getBirthDate().toString()));
            profileCurrentAge.setText(String.valueOf(animal.getCurrentAge()));
            profileWeightAtBirth.setText(animal.getWeightAtBirth());
            profileAgeAtFirstService.setText(animal.getAgeAtFirstService());
            profileWightAtFirstService.setText(animal.getWeightAtFirstService());
            grayOutFields();
            deleteAnimal.setDisable(false);
            editDetails.setDisable(false);
        });
    }
    private void addLastRow(){
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        String recordQuery = " SELECT animal_ID, animal_type, birth_date, current_age, animal_name, ear_tag, sire_ID," +
                "dam_ID, breed, color, weight_at_birth, age_at_first_service, weight_at_first_service," +
                "pasture FROM dairy_farm.animal_records ORDER BY animal_ID DESC LIMIT 1 OFFSET 0";
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(recordQuery);
            resultSet.first();
            int animalID = resultSet.getInt("animal_ID");
            String animalType = resultSet.getString("animal_type");
            Date dateOfBirth = resultSet.getDate("birth_date");
            String currentAge = resultSet.getString("current_age");
            String animalName = resultSet.getString("animal_name");
            String earTag = resultSet.getString("ear_tag");
            int sireID = resultSet.getInt("sire_ID");
            int damID = resultSet.getInt("dam_ID");
            String breed = resultSet.getString("breed");
            String color = resultSet.getString("color");
            String weightAtBirth = resultSet.getString("weight_at_birth");
            String ageAtFirstService = resultSet.getString("age_at_first_service");
            String weightAtFirstService = resultSet.getString("weight_at_first_service");
            String pasture = resultSet.getString("pasture");
            animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(animalID, animalName, animalType, dateOfBirth, currentAge,
                    earTag, sireID, damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService, pasture));
            search();
            connection.close();
        }catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
    }
    private void numberOfAnimals(){
        animalComboBox.setOnAction(actionEvent -> {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            int count;
            int index = animalComboBox.getSelectionModel().getSelectedIndex();
            if(index == 0){
                numOfAnimals.setText("");
            }
            if(index == 1){
                String query = "SELECT COUNT(animal_type) AS TotalCows FROM dairy_farm.animal_records WHERE animal_type = 'Cow'";
                String recordsQuery = "SELECT * FROM dairy_farm.animal_records WHERE animal_type = 'Cow'";
                try {
                    Statement statement = connection.createStatement();
                    Statement newStatement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    ResultSet result = newStatement.executeQuery(recordsQuery);
                    while (resultSet.next()){
                        count = resultSet.getInt(1);
                        if(count==1) numOfAnimals.setText((count) + identity);
                        else numOfAnimals.setText((count) + numOfCows);
                    }
                    while (result.next()){
                        int ID = result.getInt("animal_ID");
                        String animalType = result.getString("animal_type");
                        Date dateOfBirth = result.getDate("birth_date");
                        String currentAge = result.getString("current_age");
                        String name = result.getString("animal_name");
                        String earTag = result.getString("ear_tag");
                        int sireID = result.getInt("sire_ID");
                        int damID = result.getInt("dam_ID");
                        String breed = result.getString("breed");
                        String color = result.getString("color");
                        String weightAtBirth = result.getString("weight_at_birth");
                        String ageAtFirstService = result.getString("age_at_first_service");
                        String weightAtFirstService = result.getString("weight_at_first_service");
                        String pasture = result.getString("pasture");
                        animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name, animalType, dateOfBirth, currentAge, earTag, sireID,
                                damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService,pasture));
                        search();
                    }
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }
            }
            if(index == 2){
                try {
                    String query = "SELECT COUNT(animal_type) AS TotalCows FROM dairy_farm.animal_records WHERE animal_type = 'Bull'";
                    String newQuery = "SELECT * FROM dairy_farm.animal_records WHERE animal_type = 'Bull'";
                    Statement statement = connection.createStatement();
                    Statement newStatement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    ResultSet newResult = newStatement.executeQuery(newQuery);
                    while (resultSet.next()){
                        count = resultSet.getInt(1);
                        if(count==1) numOfAnimals.setText((count) + bullIdentity);
                        else numOfAnimals.setText((count) + numOfBulls);
                    }
                    while(newResult.next()){
                        int ID = newResult.getInt("animal_ID");
                        String animalType = newResult.getString("animal_type");
                        Date dateOfBirth = newResult.getDate("birth_date");
                        String currentAge = newResult.getString("current_age");
                        String name = newResult.getString("animal_name");
                        String earTag = newResult.getString("ear_tag");
                        int sireID = newResult.getInt("sire_ID");
                        int damID = newResult.getInt("dam_ID");
                        String breed = newResult.getString("breed");
                        String color = newResult.getString("color");
                        String weightAtBirth = newResult.getString("weight_at_birth");
                        String ageAtFirstService = newResult.getString("age_at_first_service");
                        String weightAtFirstService = newResult.getString("weight_at_first_service");
                        String pasture = newResult.getString("pasture");
                        animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name, animalType, dateOfBirth, currentAge, earTag, sireID,
                                damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService,pasture));
                        search();
                    }
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }
            }
            if(index == 3){
                try {
                    String query = "SELECT COUNT(animal_type) AS TotalCows FROM dairy_farm.animal_records WHERE animal_type = 'Heifer'";
                    String newQuery = "SELECT * FROM dairy_farm.animal_records WHERE animal_type = 'Heifer'";
                    Statement statement = connection.createStatement();
                    Statement newStatement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    ResultSet newResult = newStatement.executeQuery(newQuery);
                    while (resultSet.next()){
                        count = resultSet.getInt(1);
                        if(count==1) numOfAnimals.setText((count) + heiferIdentity);
                        else numOfAnimals.setText((count) + numOfHeifers);
                    }
                    while(newResult.next()){
                        int ID = newResult.getInt("animal_ID");
                        String animalType = newResult.getString("animal_type");
                        Date dateOfBirth = newResult.getDate("birth_date");
                        String currentAge = newResult.getString("current_age");
                        String name = newResult.getString("animal_name");
                        String earTag = newResult.getString("ear_tag");
                        int sireID = newResult.getInt("sire_ID");
                        int damID = newResult.getInt("dam_ID");
                        String breed = newResult.getString("breed");
                        String color = newResult.getString("color");
                        String weightAtBirth = newResult.getString("weight_at_birth");
                        String ageAtFirstService = newResult.getString("age_at_first_service");
                        String weightAtFirstService = newResult.getString("weight_at_first_service");
                        String pasture = newResult.getString("pasture");
                        animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name, animalType, dateOfBirth, currentAge, earTag, sireID,
                                damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService,pasture));
                        search();
                    }
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }
            }
            if(index == 4){
                try {
                    String query = "SELECT COUNT(animal_type) AS TotalCows FROM dairy_farm.animal_records WHERE animal_type = 'Calf'";
                    String newQuery = "SELECT * FROM dairy_farm.animal_records WHERE animal_type = 'Calf'";
                    Statement statement = connection.createStatement();
                    Statement newStatement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    ResultSet newResult = newStatement.executeQuery(newQuery);
                    while (resultSet.next()){
                        count = resultSet.getInt(1);
                        if(count==1) numOfAnimals.setText((count) + calfIdentity);
                        else numOfAnimals.setText((count) + numOfCalves);
                    }
                    while(newResult.next()){
                        int ID = newResult.getInt("animal_ID");
                        String animalType = newResult.getString("animal_type");
                        Date dateOfBirth = newResult.getDate("birth_date");
                        String currentAge = newResult.getString("current_age");
                        String name = newResult.getString("animal_name");
                        String earTag = newResult.getString("ear_tag");
                        int sireID = newResult.getInt("sire_ID");
                        int damID = newResult.getInt("dam_ID");
                        String breed = newResult.getString("breed");
                        String color = newResult.getString("color");
                        String weightAtBirth = newResult.getString("weight_at_birth");
                        String ageAtFirstService = newResult.getString("age_at_first_service");
                        String weightAtFirstService = newResult.getString("weight_at_first_service");
                        String pasture = newResult.getString("pasture");
                        animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name, animalType, dateOfBirth, currentAge, earTag, sireID,
                                damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService,pasture));
                        search();
                    }
//                    connection.close();
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }
            }
        });
    }
    private void search(){
        animalIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        animalNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        animalTypeColumn.setCellValueFactory(new PropertyValueFactory<>("animalType"));
        animalRecordsTable.setItems(animalRecordsSearchModelObservableList);
        animalRecordsTable.getSortOrder().add(animalIDColumn);
        FilteredList<AnimalRecordsSearchModel> filteredList = new FilteredList<>(animalRecordsSearchModelObservableList, b-> true);
        nameSearchField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredList.setPredicate(animalRecordsSearchModel -> {
            if(newValue.isEmpty()){
                return true;
            }
            String searchResult = newValue.toLowerCase();
            if(animalRecordsSearchModel.getAnimalType().toLowerCase().contains(searchResult)){
                return true;
            }
            int value = animalRecordsSearchModel.getID();
            if(String.valueOf(value).toLowerCase().contains(searchResult)){
                return true;
            }
            else return animalRecordsSearchModel.getName().toLowerCase().contains(searchResult);
        }));
        SortedList<AnimalRecordsSearchModel> searchModelSortedList = new SortedList<>(filteredList);
        searchModelSortedList.comparatorProperty().bind(animalRecordsTable.comparatorProperty());
        animalRecordsTable.setItems(searchModelSortedList);
    }
    private void retrieveAnimals(){
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connect = dbConnection.getConnection();
        String recordsQuery = "SELECT * FROM dairy_farm.animal_records";
        loadData.setOnAction(actionEvent -> {
            if(!(animalRecordsTable.getItems().isEmpty()) && numOfAnimals.getText().isEmpty()){
                animalRecordsSearchModelObservableList.clear();
                numOfAnimals.setText("");
                animalComboBox.getSelectionModel().select(-1);
                Notifications notifications = Notifications.create()
                        .text("Data is up to date")
                        .position(Pos.TOP_RIGHT)
                        .hideCloseButton()
                        .hideAfter(Duration.seconds(3));
                notifications.darkStyle();
                notifications.showInformation();
            }
            else if(!(animalRecordsTable.getItems().isEmpty()) && (!(numOfAnimals.getText().isEmpty()))){
                animalRecordsSearchModelObservableList.clear();
                numOfAnimals.setText("");
                animalComboBox.getSelectionModel().select(0);
                Notifications notify = Notifications.create()
                        .text("Data is up to date")
                        .position(Pos.TOP_RIGHT)
                        .hideCloseButton()
                        .hideAfter(Duration.seconds(3));
                notify.darkStyle();
                notify.showInformation();
            }
            try {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(recordsQuery);
                while (resultSet.next()) {
                    int ID = resultSet.getInt("animal_ID");
                    String animalType = resultSet.getString("animal_type");
                    Date dateOfBirth = resultSet.getDate("birth_date");
                    String currentAge = resultSet.getString("current_age");
                    String name = resultSet.getString("animal_name");
                    String earTag = resultSet.getString("ear_tag");
                    int sireID = resultSet.getInt("sire_ID");
                    int damID = resultSet.getInt("dam_ID");
                    String breed = resultSet.getString("breed");
                    String color = resultSet.getString("color");
                    String weightAtBirth = resultSet.getString("weight_at_birth");
                    String ageAtFirstService = resultSet.getString("age_at_first_service");
                    String weightAtFirstService = resultSet.getString("weight_at_first_service");
                    String pasture = resultSet.getString("pasture");
                    animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name, animalType, dateOfBirth, currentAge, earTag, sireID,
                            damID, breed, color, weightAtBirth, ageAtFirstService, weightAtFirstService, pasture));
                    PreparedStatement ps = connect.prepareStatement("SELECT COUNT(*) FROM dairy_farm.animal_records WHERE animal_ID > ?");
                    ps.setInt(1, 0);
                    ResultSet result = ps.executeQuery();
                    int n = 0;
                    if (result.next()) {
                        n = result.getInt(1);
                    }
                    if (n > 0) {
                        if(!(animalRecordsTable.getItems().isEmpty()) && (!(numOfAnimals.getText().isEmpty()))) {
                            loadData.setOnAction(actionEvent1 -> animalRecordsSearchModelObservableList.add(new AnimalRecordsSearchModel(ID, name,
                                    animalType, dateOfBirth, currentAge, earTag, sireID, damID, breed, color, weightAtBirth, ageAtFirstService,
                                    weightAtFirstService, pasture)));
                        }
                    }
                    search();
                }
//                connect.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
    private void grayOutFields(){
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(profileBirthDate.getValue()) || item.isAfter(profileBirthDate.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb");
                        }
                    }
                };
            }
        };
        profileBirthDate.setDayCellFactory(dayCellFactory);
        save.setDisable(true);
        profileAgeAtFirstService.setEditable(false);
        profileAnimalType.setEditable(false);
        profileBreed.setEditable(false);
        profileColor.setEditable(false);
        profileCurrentAge.setEditable(false);
        profileDamID.setEditable(false);
        profileEarTag.setEditable(false);
        profileID.setEditable(false);
        profileName.setEditable(false);
        profilePasture.setEditable(false);
        profileSireID.setEditable(false);
        profileWeightAtBirth.setEditable(false);
        profileWightAtFirstService.setEditable(false);
        profileName.setStyle("-fx-control-inner-background: #E5E3E3");
        profileAnimalType.setStyle("-fx-control-inner-background: #E5E3E3");
        profileID.setStyle("-fx-control-inner-background: #E5E3E3");
        profileCurrentAge.setStyle("-fx-control-inner-background: #E5E3E3");
        profileWeightAtBirth.setStyle("-fx-control-inner-background: #E5E3E3");
        profileWightAtFirstService.setStyle("-fx-control-inner-background: #E5E3E3");
        profileSireID.setStyle("-fx-control-inner-background: #E5E3E3");
        profilePasture.setStyle("-fx-control-inner-background: #E5E3E3");
        profileEarTag.setStyle("-fx-control-inner-background: #E5E3E3");
        profileDamID.setStyle("-fx-control-inner-background: #E5E3E3");
        profileColor.setStyle("-fx-control-inner-background: #E5E3E3");
        profileBreed.setStyle("-fx-control-inner-background: #E5E3E3");
        profileAgeAtFirstService.setStyle("-fx-control-inner-background: #E5E3E3");
    }
}
