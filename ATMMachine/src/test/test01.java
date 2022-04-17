package test;

import entity.ATM;
import entity.Account;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// This test simulate two user use two different machine to access the same account
// To make user data consistency
public class test01 {
    public static void main(String[] args) {
        int[] cmd = new int[]{1111, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 4};

        Account account1 = new Account(1111, 2000);
        Account[] list = new Account[]{
                account1,
                new Account(2222, 100),
                new Account(3333, 5000)
        };
        ATM atm1 = new ATM(5000, cmd, list);
        ATM atm2 = new ATM(5000, cmd, list);
        new Thread(atm1, "No1").start();
        new Thread(atm2, "No2").start();

    }

}
