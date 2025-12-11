package controller;

import TM.dtmTM;
import bo.BOFactory;
import bo.custom.CustomerBO;
import bo.custom.Impl.CustomerBOImpl;
import bo.custom.Impl.ItemBOImpl;
import bo.custom.Impl.PlaceOrderBOImpl;
import bo.custom.ItemBO;
import bo.custom.PlaceOrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import dto.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PlaceOrderController implements Initializable {

    public Label txtDate;
    public Label txtTime;
    public JFXComboBox<String> CustomerComboTxt;
    public JFXTextField txtCustName;
    public JFXTextField txtCustPhone;
    public JFXTextField txtCustAddress;
    public JFXTextField txtCustEmail;
    public Label txtTotal;
    public TableView<dtmTM> dtm;
    public TableColumn<dtmTM, String> colitemcode;
    public TableColumn<dtmTM, String> colitemname;
    public TableColumn<dtmTM, String> colprice;
    public TableColumn<dtmTM, String> colqty;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemName;
    public JFXTextField txtPrice;
    public JFXTextField txtQTY;
    public Label txtOrderId;
    public Label txtQtyCount;
    public Label finalTotal;
    public JFXTextField Discount;
    public Label txtCashierID;
    public JFXButton btnRemove;

    CustomerBO customerBO = new CustomerBOImpl();
    ItemBO itemBO = new ItemBOImpl();
    PlaceOrderBO placeOrderBO = new PlaceOrderBOImpl();

    ObservableList<CustomerDTO> customerDTOS;
    ArrayList<String> allCustomers = new ArrayList<>();
    ArrayList<String> allItem = new ArrayList<>();
    ArrayList<dtmTM> items = new ArrayList<>();
    private int count;
    private int index;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colitemcode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colitemname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colqty.setCellValueFactory(new PropertyValueFactory<>("QTY"));

        generateDateTime();
        getAllCustomer();
        reloadCustomer();
        getAllItem();
        setTxtorderID();
    }

    public void setTxtorderID() {
        try {
            int id = placeOrderBO.getRowCount();
            if (id < 9) {
                this.txtOrderId.setText("K00" + (id + 1));
            } else if (id < 99) {
                this.txtOrderId.setText("K0" + (id + 1));
            } else {
                this.txtOrderId.setText("K" + (id + 1));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void getAllCustomer() {
        try {
            customerDTOS = customerBO.getAllCustomer();
            for (CustomerDTO dto : customerDTOS) {
                allCustomers.add(dto.getCustID() + " " + dto.getCustName());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void getAllItem() {
        try {
            ObservableList<ItemDTO> itemDTOS = itemBO.getAllItem();
            for (ItemDTO dto : itemDTOS) {
                allItem.add(dto.getItemCode() + " " + dto.getDescription());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateDateTime() {
        this.txtDate.setText(LocalDate.now().toString());
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
            this.txtTime.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void reloadCustomer() {
        CustomerComboTxt.setItems(FXCollections.observableArrayList(allCustomers));
    }

    public void btnComboOnAction(ActionEvent actionEvent) {
        try {
            String value = CustomerComboTxt.getValue();
            String[] a = value.split("\\s+");
            CustomerDTO customerDTO = customerBO.searchCustomer(a[0]);
            txtCustName.setText(customerDTO.getCustName());
            txtCustAddress.setText(customerDTO.getCustAddress());
            txtCustEmail.setText(customerDTO.getCustEmail());
            txtCustPhone.setText(customerDTO.getCustPhoneNo());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchItemOnAction() {
        try {
            String itemID = txtItemCode.getText();
            String[] a = itemID.split("\\s+");
            ItemDTO searchItem = itemBO.searchItem(a[0]);
            if (searchItem != null) {
                txtItemName.setText(searchItem.getDescription());
                txtPrice.setText(searchItem.getUnitPrice());
            } else {
                TrayNotification tray = new TrayNotification();
                tray.setAnimationType(AnimationType.POPUP);
                tray.setTitle("Searched Item Not Found");
                tray.setMessage("Try Again");
                tray.setNotificationType(NotificationType.ERROR);
                tray.showAndDismiss(Duration.millis(3000));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCartOnAction() {
        String code = txtItemCode.getText();
        String name = txtItemName.getText();
        String price = txtPrice.getText();
        String QTY = txtQTY.getText();
        dtmTM rowData = new dtmTM(code, name, price, QTY);
        items.add(rowData);
        dtm.setItems(FXCollections.observableArrayList(items));

        count += Integer.parseInt(QTY);
        txtQtyCount.setText(String.valueOf(count));

        double total = 0;
        for (dtmTM item : items) {
            total += Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQTY());
        }
        txtTotal.setText(String.valueOf(total));
        finalTotal.setText(txtTotal.getText());
    }

    public void RemoveOnAction(ActionEvent actionEvent) {
        if (!items.isEmpty() && index >= 0) {
            items.remove(index);
            dtm.setItems(FXCollections.observableArrayList(items));
            reloadTotalQty();
        }
    }

    private void reloadTotalQty() {
        int qty = 0;
        double total = 0;
        for (dtmTM dtmTM : items) {
            qty += Integer.parseInt(dtmTM.getQTY());
            total += Double.parseDouble(dtmTM.getPrice()) * Integer.parseInt(dtmTM.getQTY());
        }
        txtQtyCount.setText(String.valueOf(qty));
        txtTotal.setText(String.valueOf(total));
    }

    public void tblMouseClick(MouseEvent mouseEvent) {
        dtmTM c = dtm.getSelectionModel().getSelectedItem();
        txtItemCode.setText(c.getCode());
        txtItemName.setText(c.getName());
        txtPrice.setText(c.getPrice());
    }

    public boolean placeOrderOnAction(ActionEvent actionEvent) {
        placeOrderBO = (PlaceOrderBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PO);

        String orderID = txtOrderId.getText();
        String orderDate = txtDate.getText();
        String orderTime = txtTime.getText();
        String custName = txtCustName.getText();
        String custPhoneNo = txtCustPhone.getText();
        String custAddress = txtCustAddress.getText();
        String custEmail = txtCustEmail.getText();
        String castID = txtCashierID.getText();
        double amount = Double.parseDouble(finalTotal.getText());
        String discount = Discount.getText();

        try {
            boolean placed = placeOrderBO.placeOrder(
                    new OrdersDTO(orderID, orderDate, orderTime, custName, custPhoneNo, custAddress, custEmail, castID, items, amount, discount)
            );

            if (placed) {
                OrderDetailFieldRest();
                new Alert(Alert.AlertType.CONFIRMATION, "Order Successfully", ButtonType.OK).show();
                TrayNotification tray = new TrayNotification();
                tray.setAnimationType(AnimationType.POPUP);
                tray.setTitle("ORDER SUCCESS");
                tray.setMessage("SAY THANK YOU FOR CUSTOMER");
                tray.setNotificationType(NotificationType.SUCCESS);
                tray.showAndDismiss(Duration.millis(3000));
                setTxtorderID();
            } else {
                new Alert(Alert.AlertType.ERROR, "Order Unsuccessfully", ButtonType.OK).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void tbltemsOnMouoseReleased(MouseEvent mouseEvent) {
        index = dtm.getSelectionModel().getSelectedIndex();
        btnRemove.setDisable(false);
    }

    public void OrderDetailFieldRest() {
        txtCustName.clear();
        txtCustPhone.clear();
        txtCustAddress.clear();
        txtCustEmail.clear();
        txtTotal.setText(null);
        txtQtyCount.setText(null);
        finalTotal.setText(null);
        txtItemCode.clear();
        txtItemName.clear();
        txtPrice.clear();
        txtQTY.clear();
    }

    public void adddiscountKey(KeyEvent keyEvent) {
        // Optional: handle discount input here
    }

    public void printBillOnAction(ActionEvent actionEvent) {
        try {
            InputStream is = this.getClass().getResourceAsStream("/reports/invoicee.jrxml");
            JasperReport jr = JasperCompileManager.compileReport(is);
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("itemCode", colitemcode.getText());
            hs.put("itemName", colitemname.getText());
            hs.put("QTY", colqty.getText());
            hs.put("Price", colprice.getText());
            hs.put("OrderID", txtOrderId.getText());

            JasperPrint jp = JasperFillManager.fillReport(jr, hs, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jp);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCashierID(String cashierID) {
        txtCashierID.setText(cashierID);
        System.out.println(cashierID + " cashierID");
    }
}
