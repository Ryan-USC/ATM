package test;

import entity.ATM;
import entity.Account;


// This test simulate two user use two different machine to access the same account
// To make user data consistency
public class test1 {
    public static void main(String[] args) {

        int[] cmd = new int[]{2222, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 4};

        Account[] list = new Account[]{
                new Account(1111, 1000),
                new Account(2222, 2000),
                new Account(3333, 3000)
        };
        ATM atm1 = new ATM(3000, cmd, list);
        ATM atm2 = new ATM(5000, cmd, list);
        new Thread(atm1, "No1").start();
        new Thread(atm2, "No2").start();
    }
}
