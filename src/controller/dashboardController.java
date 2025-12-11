package controller;

import animatefx.animation.FadeIn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {
    @FXML
    public Pane context;

    @FXML
    private Button btnDashBoard;

    @FXML
    private Button btnAddCashier;

    @FXML
    private Button btnReport;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnAddItem;

    @FXML
    private Button btnSupplier;

    @FXML
    private Button btnAbout;

    @FXML
    private Button btnLogOut;

    @FXML
    private ScrollPane buttonScrollPane;

    @FXML
    private VBox buttonContainer;

    @FXML
    private VBox sidebar;

    // Track active button
    private Button activeButton;

    // Style constants
    private static final String ACTIVE_STYLE = "-fx-background-color: rgba(255,255,255,0.35); " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: rgba(255,255,255,0.5); " +
            "-fx-border-width: 2;";

    private static final String INACTIVE_STYLE = "-fx-background-color: rgba(255,255,255,0.15); " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-width: 1;";

    private static final String LOGOUT_STYLE = "-fx-background-color: rgba(0,0,0,0.4); " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: rgba(255,255,255,0.2); " +
            "-fx-border-width: 1;";

    // Load UI into context pane
    private void setUi(String location) throws IOException {
        context.getChildren().clear();
        context.getChildren().add(FXMLLoader.load(this.getClass().getResource("/view/" + location + ".fxml")));
    }

    // Set active button with visual feedback
    private void setActiveButton(Button button) {
        // Reset all buttons to inactive style
        resetAllButtons();

        // Set the clicked button to active style
        if (button != null) {
            button.setStyle(ACTIVE_STYLE);
            activeButton = button;
        }
    }

    // Reset all sidebar buttons to inactive style
    private void resetAllButtons() {
        btnDashBoard.setStyle(INACTIVE_STYLE);
        btnAddCashier.setStyle(INACTIVE_STYLE);
        btnReport.setStyle(INACTIVE_STYLE);
        btnCustomers.setStyle(INACTIVE_STYLE);
        btnAddItem.setStyle(INACTIVE_STYLE);
        btnSupplier.setStyle(INACTIVE_STYLE);
        btnAbout.setStyle(INACTIVE_STYLE);
        // Keep logout button style separate
        btnLogOut.setStyle(LOGOUT_STYLE);
    }

    // Sidebar actions with active button highlighting
    public void DashBoardOnAction() throws IOException {
        setUi("DashboardForm");
        setActiveButton(btnDashBoard);
        new FadeIn(context).play();
    }

    public void btnAddCashier() throws IOException {
        setUi("AddCashierForm");
        setActiveButton(btnAddCashier);
        new FadeIn(context).play();
    }

    public void btnAddCustomer() throws IOException {
        setUi("AddCustomerForm");
        setActiveButton(btnCustomers);
        new FadeIn(context).play();
    }

    public void btnReportOnAction() throws IOException {
        setUi("Report");
        setActiveButton(btnReport);
        new FadeIn(context).play();
    }

    public void btnAboutOnAction() throws IOException {
        setUi("AboutForm");
        setActiveButton(btnAbout);
        new FadeIn(context).play();
    }

    public void btnSuplayerOnAction() throws IOException {
        setUi("AddSuply");
        setActiveButton(btnSupplier);
        new FadeIn(context).play();
    }

    public void AddItemOnAction(ActionEvent actionEvent) throws IOException {
        setUi("AddItemForm");
        setActiveButton(btnAddItem);
        new FadeIn(context).play();
    }

    // Window control buttons
    public void btnCloseOnAction() {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.close();
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

    // Logout button
    @FXML
    public void btnLogOut() throws IOException {
        // Load the login form FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginForm.fxml"));
        AnchorPane loginRoot = loader.load();

        // Get the current stage from any node in your current scene
        Stage stage = (Stage) btnLogOut.getScene().getWindow(); // assuming btnLogOut is your Button

        // Set the new scene with login form in the same stage
        Scene scene = new Scene(loginRoot);
        stage.setScene(scene);

        // Optional: adjust stage size
        stage.sizeToScene();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up scroll pane
        buttonScrollPane.setFitToWidth(true);
        buttonScrollPane.setFitToHeight(true);

        // Bind the scroll pane's preferred height to available space
        buttonScrollPane.prefHeightProperty().bind(sidebar.heightProperty().subtract(200));

        // Ensure buttons are properly sized
        buttonContainer.prefWidthProperty().bind(sidebar.widthProperty().subtract(30));

        // Set initial styles for all buttons
        resetAllButtons();

        // Load default view and set active button
        try {
            DashBoardOnAction();
            // Dashboard button is already set as active in DashBoardOnAction()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}