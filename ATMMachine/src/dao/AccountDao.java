package dao;

import entity.Account;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDao {
    public static Account selectByPin(int pin){
        Account account = new Account();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "select pin,balance from accounts where pin = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,pin);
            rs = ps.executeQuery();
            if (rs.next()){
                account.setPin(pin);
                account.setBalance(Integer.parseInt(rs.getString("balance")));
            } else account = null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,rs);
        }
        return account;
    }

    public static void updateAccount(Account account){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "update accounts set balance= ? where pin= ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,account.getBalance());
            ps.setInt(2, account.getPin());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBUtil.close(conn,ps,rs);
        }
    }

}
