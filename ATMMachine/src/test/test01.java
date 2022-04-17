package test;

import entity.ATM;
import entity.Account;


// This test simulate two user use two different machine to access the same account
// To make user data consistency

/*
* Expected output: The sum of money two users withdraw equals 2000
* Actual output:
No2
Withdraw success! Current balance is 1900
No2
Withdraw success! Current balance is 1800
No1
Withdraw success! Current balance is 1700
No1
Withdraw success! Current balance is 1600
No1
Withdraw success! Current balance is 1500
No1
Withdraw success! Current balance is 1400
No1
Withdraw success! Current balance is 1300
No1
Withdraw success! Current balance is 1200
No1
Withdraw success! Current balance is 1100
No1
Withdraw success! Current balance is 1000
No1
Withdraw success! Current balance is 900
No1
Withdraw success! Current balance is 800
No1
Withdraw success! Current balance is 700
No1
Withdraw success! Current balance is 600
No1
Withdraw success! Current balance is 500
No1
Withdraw success! Current balance is 400
No1
Withdraw success! Current balance is 300
No1
Withdraw success! Current balance is 200
No1
Withdraw success! Current balance is 100
No1
Withdraw success! Current balance is 0
You don't have enough balance!
You don't have enough balance!
You don't have enough balance!
......
*
* */
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
