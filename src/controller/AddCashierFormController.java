package controller;

import bo.BOFactory;
import bo.custom.CashierBO;
import com.jfoenix.controls.*;
import dto.CashierDTO;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddCashierFormController implements Initializable {
    @FXML
    public Pane context;
    @FXML private JFXTextField txtCashierID;
    @FXML private JFXTextField txtCashierName;
    @FXML private JFXTextField txtCashierAddress;
    @FXML private JFXTextField txtLogin;
    @FXML private JFXPasswordField txtPassword;
    @FXML private DatePicker txtCashierBirthDay;
    @FXML private JFXComboBox<String> cmbRole;
    @FXML private ImageView imageid;
    @FXML private JFXButton btnPhoto;

    @FXML private TableView<CashierDTO> tblCashier;
    @FXML private TableColumn<CashierDTO, String> colCashId;
    @FXML private TableColumn<CashierDTO, String> colCashName;
    @FXML private TableColumn<CashierDTO, String> colCashAddress;
    @FXML private TableColumn<CashierDTO, String> colCashBirthDay;
    @FXML private TableColumn<CashierDTO, String> colCashRole;

    private CashierBO cashierBO;
    private String picName; // full image path

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize BO
        cashierBO = (CashierBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.CASHIER);

        // Table column property mapping
        colCashId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("castID"));
        colCashName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("castName"));
        colCashAddress.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("castAddress"));
        colCashBirthDay.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("castBirthDay"));
        colCashRole.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("role"));

        // ComboBox values
        cmbRole.getItems().addAll("Cashier", "Admin");

        // Load all cashier data
        loadAllCashier();
    }

    private void loadAllCashier() {
        try {
            ObservableList<CashierDTO> allCashiers = cashierBO.getAllCashier();
            tblCashier.setItems(allCashiers);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void choosePhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            picName = file.getAbsolutePath();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageid.setImage(image);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void addOnAction(ActionEvent event) {
        try {
            CashierDTO dto = new CashierDTO(
                    txtCashierID.getText(),
                    txtCashierName.getText(),
                    txtCashierBirthDay.getValue() != null ? txtCashierBirthDay.getValue().toString() : "",
                    txtCashierAddress.getText(),
                    picName,
                    txtLogin.getText(),
                    txtPassword.getText(),
                    cmbRole.getValue() != null ? cmbRole.getValue() : "Cashier"
            );

            boolean success = cashierBO.addCashier(dto);
            showTrayNotification(success, "Added Successfully", "Cashier added.", "Add Failed", "Failed to add cashier.");
            if (success) loadAllCashier();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchOnAction(ActionEvent event) {
        try {
            CashierDTO dto = cashierBO.searchCashier(txtCashierID.getText());
            if (dto != null) {
                populateFields(dto);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void populateFields(CashierDTO dto) {
        txtCashierID.setText(dto.getCastID());
        txtCashierName.setText(dto.getCastName());
        txtCashierAddress.setText(dto.getCastAddress());
        txtCashierBirthDay.setValue(dto.getCastBirthDay() != null && !dto.getCastBirthDay().isEmpty() ?
                LocalDate.parse(dto.getCastBirthDay()) : null);
        txtLogin.setText(dto.getCastlogin());
        txtPassword.setText(dto.getCastPassword());
        cmbRole.setValue(dto.getRole());
        setPic(dto.getCastPhoto());
    }

    private void setPic(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new File(path));
                imageid.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void btnMinimizeOnAction() {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setIconified(true);
    }

    public void btnMaximizeOnAction() {
        Stage stage = (Stage) context.getScene().getWindow();
        if (stage.isFullScreen() || stage.getWidth() == Screen.getPrimary().getBounds().getWidth()) {
            stage.setWidth(1200); // Restore original width
            stage.setHeight(700); // Restore original height
            stage.centerOnScreen();
        } else {
            stage.setWidth(Screen.getPrimary().getBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getBounds().getHeight());
            stage.setX(0);
            stage.setY(0);
        }
    }


    @FXML
    private void cashierUpdateOnAction(ActionEvent event) {
        try {
            CashierDTO dto = new CashierDTO(
                    txtCashierID.getText(),
                    txtCashierName.getText(),
                    txtCashierBirthDay.getValue() != null ? txtCashierBirthDay.getValue().toString() : "",
                    txtCashierAddress.getText(),
                    picName,
                    txtLogin.getText(),
                    txtPassword.getText(),
                    cmbRole.getValue() != null ? cmbRole.getValue() : "Cashier"
            );

            boolean success = cashierBO.updateCashier(dto);
            showTrayNotification(success, "Updated Successfully", "Cashier updated.", "Update Failed", "Failed to update cashier.");
            if (success) loadAllCashier();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cashierDeleteOnAction(ActionEvent event) {
        try {
            boolean success = cashierBO.deleteCashier(txtCashierID.getText());
            showTrayNotification(success, "Deleted Successfully", "Cashier deleted.", "Delete Failed", "Failed to delete cashier.");
            if (success) loadAllCashier();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void tblCashierClick() {
        CashierDTO dto = tblCashier.getSelectionModel().getSelectedItem();
        if (dto != null) {
            populateFields(dto);
        }
    }

    private void showTrayNotification(boolean success, String titleSuccess, String messageSuccess,
                                      String titleFail, String messageFail) {
        TrayNotification tray = new TrayNotification();
        tray.setAnimationType(AnimationType.POPUP);
        if (success) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION, messageSuccess).show();
            tray.setTitle(titleSuccess);
            tray.setMessage(messageSuccess);
            tray.setNotificationType(NotificationType.SUCCESS);
        } else {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, messageFail).show();
            tray.setTitle(titleFail);
            tray.setMessage(messageFail);
            tray.setNotificationType(NotificationType.ERROR);
        }
        tray.showAndDismiss(Duration.millis(3000));
    }
}
