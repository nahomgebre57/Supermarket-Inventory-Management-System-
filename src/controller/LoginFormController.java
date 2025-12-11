package controller;

import bo.custom.CashierBO;
import bo.custom.Impl.LoginBOImpl;
import bo.custom.LoginBO;
import dto.CashierDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.sql.SQLException;

public class LoginFormController {

    public TextField txtUserName;
    public PasswordField txtPassword;
    public AnchorPane root;
    CashierBO cashierBO;

    public void LoginOnAction() throws IOException {
        String userName = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            showNotification("Sign In", "Empty Username or Password", NotificationType.INFORMATION);
            return;
        }

        LoginBO loginBO = new LoginBOImpl();
        try {
            CashierDTO cashierDTO = loginBO.getValidated(userName);

            if (cashierDTO != null &&
                    cashierDTO.getCastlogin().equals(userName) &&
                    cashierDTO.getCastPassword().equals(password)) {

                String role = cashierDTO.getRole(); // Role column from database

                if ("admin".equalsIgnoreCase(role)) {
                    openDashboard();
                    showNotification("Sign In", "WELCOME ADMIN!", NotificationType.SUCCESS);
                } else if ("cashier".equalsIgnoreCase(role)) {
                    openCashierForm(cashierDTO.getCastID());
                    showNotification("Sign In", "WELCOME CASHIER!", NotificationType.SUCCESS);
                } else {
                    showNotification("Sign In", "Unknown role assigned.", NotificationType.WARNING);
                }

            } else {
                showNotification("Sign In", "Invalid Username or Password", NotificationType.ERROR);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showNotification("Sign In", "Database Error", NotificationType.ERROR);
        }
    }

    private void openDashboard() throws IOException {
        Stage window = (Stage) this.root.getScene().getWindow();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"))));
        window.centerOnScreen();
        window.show();
    }

    private void openCashierForm(String cashierID) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/CashierForm.fxml"));
        Parent parent = fxmlLoader.load();

        CashierFormController controller = fxmlLoader.getController();
        controller.setCashierID(cashierID);

        Stage window = (Stage) this.root.getScene().getWindow();
        window.setScene(new Scene(parent));
        window.centerOnScreen();
        window.show();
    }

    private void showNotification(String title, String message, NotificationType type) {
        TrayNotification tray = new TrayNotification();
        tray.setAnimationType(AnimationType.POPUP);
        tray.setTitle(title);
        tray.setMessage(message);
        tray.setNotificationType(type);
        tray.showAndDismiss(Duration.millis(3000));
    }

    public void btnCloaseOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
