package entity;

import dao.AccountDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ATM implements Runnable{
    // The maximum money the ATM can store
    final static private Integer maxStorage = 10000;
    // The current stored money in ATM
    private Integer totalBalance;
    // Input Scanner
    private Scanner sc;
    // HashMap as a local cache, pin is the key and Account object is the value
    HashMap<Integer, Account> map = new HashMap<>();
    // log user's input and database manipulation into info.log
    Logger logger = Logger.getLogger("ATMLog");
    FileHandler fh;

    // A array of commands
    int[] cmd;
    // authenticated to indicated whether the user login successfully
    boolean authenticated;

    // constructor 1
    public ATM(Integer totalBalance) {
        this.totalBalance = totalBalance;
        sc = new Scanner(System.in);

        try {
            fh = new FileHandler("src/info.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);

    }

    // constructor 1 
    public ATM(Integer totalBalance, int[] cmd, Account[] list) {
        this.totalBalance = totalBalance;
        this.cmd = cmd;
        authenticated = false;
        for (Account current: list){
            map.put(current.getPin(), current);
        }
    }


    // check if the pin is correct
    private boolean login(int pin){
        Account account = AccountDao.selectByPin(pin);
        if (account == null) {
            System.out.println("PIN is wrong!");
            return false;
        }
        if (!map.containsKey(pin)){
            map.put(pin, account);
        } else {
            map.get(pin).setBalance(account.getBalance());
        }
        return true;
    }
    
    // query the balance
    private void query(int pin){
        synchronized (this.map.get(pin)) {
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance);
            System.out.println("Your current balance is " + currentBalance);
        }
    }

    // withdraw money
    private void withdraw(int pin, int amount){
        synchronized (this.map.get(pin)){
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance);

            if (amount >= 0 && amount <= this.totalBalance && amount <= currentBalance){
                this.totalBalance -= amount;

                this.map.get(pin).setBalance(currentBalance - amount);
                AccountDao.updateAccount(this.map.get(pin));
                this.logger.info("User withdraw " + amount);
                System.out.println(Thread.currentThread().getName());
                System.out.println("Withdraw success! Current balance is " + this.map.get(pin).getBalance());
            } else if (amount > this.totalBalance) {
                System.out.println("ATM out of Money");
                this.logger.warning("Withdraw Fail! Reason: ATM out of Money!");
            }
            else if (amount > currentBalance) {
                System.out.println("You don't have enough balance!");
                this.logger.warning("Withdraw Fail! Reason: Balance not enough!");
            }
            else {
                System.out.println("Invalid amount!");
                this.logger.warning("Invalid input!");
            }
        }
    }

    // deposit money
    private void deposit(int pin, int amount){
        synchronized (this.map.get(pin)) {
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance);
            if (amount <= 0) {
                System.out.println("Invalid amount!");
                this.logger.warning("Invalid input!");
                return;
            }
            if(this.totalBalance + amount > maxStorage){
                System.out.println("Fail! ATM out of storage! Try deposit less money.");
                this.logger.warning("Deposit Fail! Reason: ATM don't have enough space!");
                return;
            }
            this.totalBalance += amount;
            this.map.get(pin).setBalance(currentBalance + amount);
            AccountDao.updateAccount(this.map.get(pin));
            this.logger.info("User deposit " + amount);
            System.out.println("You have successfully deposit " + amount);
        }
    }

    @Override
    public void run() {
        int pin = -1;
        int n = cmd.length;
        for (int i = 0; i < n; i++){
            if (!authenticated){
                authenticated = login(cmd[i]);
                if (authenticated) pin = cmd[i];
                continue;
            }
            // You can use sleep to check the synchronization

            if (cmd[i] == 1) query(pin);
            else if (cmd[i] == 2){
                if (i+1 < n) {
                    withdraw(pin, cmd[i+1]);
                    i++;
                }
            }else if (cmd[i] == 3) {
                if (i+1 < n) {
                    deposit(pin, cmd[i+1]);
                    i++;
                }
            } else if (cmd[i] == 4) {
                pin = -1;
                authenticated = false;
            } else System.out.println("Invalid Input!");
        }
    }


    // This method simulate a user access an ATM
    public static void main(String[] args) {
        ATM atm = new ATM(3000);
        while (true){
            System.out.println("Welcome to ATM!");
            int inputPin;
            while (true){
                System.out.println("Please enter PIN:");
                inputPin = atm.sc.nextInt();
                atm.logger.info("User Input: " + inputPin);
                if (atm.login(inputPin)) {
                    atm.logger.info("User " + inputPin + " login success!");
                    break;
                }
                else {
                    atm.logger.warning("PIN is wrong!");
                    System.out.println("Your PIN is WRONG!");
                }
            }
            System.out.println("Success!");
            System.out.println("Please enter corresponding num to continue.");
            System.out.println("Enter 1 to check your balance.");
            System.out.println("Enter 2 to withdraw money.");
            System.out.println("Enter 3 to deposit money.");
            System.out.println("Enter 4 to logout.");
            while (true){
                int num = atm.sc.nextInt();
                atm.logger.info("User input num: " + num);
                if (num == 1) {
                    atm.query(inputPin);
                    atm.logger.info("User check balance.");
                }
                else if (num == 2){
                    System.out.println("Please enter the amount of money to withdraw:");
                    int amount = atm.sc.nextInt();
                    atm.withdraw(inputPin, amount);
                } else if (num == 3){
                    System.out.println("Please enter the amount of money to deposit:");
                    int amount = atm.sc.nextInt();
                    atm.deposit(inputPin, amount);
                }else if(num == 4){
                    atm.logger.info("User " + inputPin + " logout.");
                    break;
                } else {
                    atm.logger.warning("Invalid input!");
                    System.out.println("Please enter a valid num!");
                }

            }
            System.out.println("Logout!");
        }
    }
}
