package dao.custom.impl;

import dao.CrudUtil;
import dao.custom.CashierDAO;
import entity.Cashier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CashierDAOImpl implements CashierDAO {

    @Override
    public String getCashierLastId() throws Exception {
        String sql = "SELECT MAX(castID) FROM cashier";
        ResultSet rst = CrudUtil.executeQuery(sql);
        if (rst.next()) {
            return rst.getString(1);
        }
        return "";
    }

    @Override
    public int getRowCount() throws ClassNotFoundException, SQLException {
        String sql = "SELECT COUNT(*) FROM cashier";
        ResultSet rst = CrudUtil.executeQuery(sql);
        if (rst.next()) {
            return rst.getInt(1);
        }
        return 0;
    }

    @Override
    public Cashier validate(String userName) throws SQLException, ClassNotFoundException {
        String sql = "SELECT castID, castlogin, castPassword, role FROM cashier WHERE castlogin=?";
        ResultSet resultSet = CrudUtil.executeQuery(sql, userName);
        if(resultSet.next()){
            return new Cashier(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4) // role
            );
        }
        return null;
    }

    @Override
    public boolean add(Cashier cashier) throws ClassNotFoundException, SQLException {
        String sql = "INSERT INTO cashier(castID, castName, castBirthDay, castAddress, castPhoto, castlogin, castPassword, role) VALUES(?,?,?,?,?,?,?,?)";
        return CrudUtil.executeUpdate(sql,
                cashier.getCastID(),
                cashier.getCastName(),
                cashier.getCastBirthDay(),
                cashier.getCastAddress(),
                cashier.getCastPhoto(),
                cashier.getCastlogin(),
                cashier.getCastPassword(),
                cashier.getRole()
        );
    }

    @Override
    public boolean delete(String ID) throws ClassNotFoundException, SQLException {
        String sql = "DELETE FROM cashier WHERE castID=?";
        return CrudUtil.executeUpdate(sql, ID);
    }

    @Override
    public boolean update(Cashier cashier) throws ClassNotFoundException, SQLException {
        String sql = "UPDATE cashier SET castName=?, castBirthDay=?, castAddress=?, castPhoto=?, castlogin=?, castPassword=?, role=? WHERE castID=?";
        return CrudUtil.executeUpdate(sql,
                cashier.getCastName(),
                cashier.getCastBirthDay(),
                cashier.getCastAddress(),
                cashier.getCastPhoto(),
                cashier.getCastlogin(),
                cashier.getCastPassword(),
                cashier.getRole(),
                cashier.getCastID()
        );
    }

    @Override
    public Cashier search(String ID) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM cashier WHERE castID=?";
        ResultSet rst = CrudUtil.executeQuery(sql, ID);
        if (rst.next()) {
            return new Cashier(
                    rst.getString("castID"),
                    rst.getString("castName"),
                    rst.getString("castBirthDay"),
                    rst.getString("castAddress"),
                    rst.getString("castPhoto"),
                    rst.getString("castlogin"),
                    rst.getString("castPassword"),
                    rst.getString("role")
            );
        }
        return null;
    }

    @Override
    public ObservableList<Cashier> getAll() throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM cashier";
        ResultSet rst = CrudUtil.executeQuery(sql);
        ObservableList<Cashier> allCashier = FXCollections.observableArrayList();
        while (rst.next()) {
            allCashier.add(new Cashier(
                    rst.getString("castID"),
                    rst.getString("castName"),
                    rst.getString("castBirthDay"),
                    rst.getString("castAddress"),
                    rst.getString("castPhoto"),
                    rst.getString("castlogin"),
                    rst.getString("castPassword"),
                    rst.getString("role")
            ));
        }
        return allCashier;
    }
}
