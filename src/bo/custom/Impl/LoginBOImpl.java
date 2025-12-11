package bo.custom.Impl;

import bo.custom.LoginBO;
import dao.DAOFactory;
import dao.custom.CashierDAO;
import dto.CashierDTO;
import entity.Cashier;

import java.sql.SQLException;

public class LoginBOImpl implements LoginBO {
    private final CashierDAO cashierDAO = (CashierDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTypes.CASHIER);

    @Override
    public CashierDTO getValidated(String userName) throws SQLException, ClassNotFoundException {
        Cashier cus = cashierDAO.validate(userName);
        if (cus != null) {
            return new CashierDTO(
                    cus.getCastID(),
                    cus.getCastName(),
                    cus.getCastBirthDay(),
                    cus.getCastAddress(),
                    cus.getCastPhoto(),
                    cus.getCastlogin(),
                    cus.getCastPassword(),
                    cus.getRole() // include role
            );
        }
        return null; // return null if user not found
    }
}
