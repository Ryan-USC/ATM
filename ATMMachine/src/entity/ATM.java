package entity;

import dao.AccountDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ATM implements Runnable{
    // MAX_STORAGE is an instance that specify the maximum storage that an ATM can store
    final static private Integer MAX_STORAGE = 10000;
    // totalBalance indicate the current balance the ATM stores
    private Integer totalBalance;
    // scanner is used to get user's input
    private Scanner scanner;
    // key is pin, value is Account object, when a user doing some operations, we just lock corresponding account to make user it is thread safe
    HashMap<Integer, Account> map = new HashMap<>();

    // logger is used to record the user's input and the outcome
    Logger logger = Logger.getLogger("ATMLog");
    FileHandler fh;

    // A array of commands
    int[] cmd;
    // authenticated to indicated whether the user login successfully
    boolean authenticated;

    public ATM(Integer totalBalance) {
        this.totalBalance = totalBalance;
        scanner = new Scanner(System.in);

        try {
            fh = new FileHandler("src/info.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    public ATM(Integer totalBalance, int[] cmd, Account[] list) {
        this.totalBalance = totalBalance;
        this.cmd = cmd;
        authenticated = false;
        for (Account current: list){
            map.put(current.getPin(), current);
        }
    }


    /**
     * login(int pin) is used to check whether user's pin is correct or not
     * @param pin Account pin
     * @return true -> login success, false -> login fail
     */
    private boolean login(int pin){
        /*
        *
        * */
        Account account = AccountDao.selectByPin(pin);
        if (account == null) {
            System.out.println("PIN is wrong!");
            return false;
        }
        if (!map.containsKey(pin)){
            map.put(pin, account);
        } else {
            // synchronize the balance between database and local cache
            map.get(pin).setBalance(account.getBalance());
        }
        return true;
    }

    /**
     * query(int pin) is used to query the balance of account
     * @param pin Account pin
     */
    private void query(int pin){
        synchronized (this.map.get(pin)) {
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance);
            System.out.println("Your current balance is " + currentBalance);
        }
    }


    /**
     * withdraw(int pin, int amount) is used to withdraw money of account
     * @param pin Account pin
     * @param amount Amount user want to withdraw
     */
    private void withdraw(int pin, int amount){
        synchronized (this.map.get(pin)){
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance);
            /*
            * To withdraw from their account, it must fulfill three conditions.
            *   1. amount is bigger than 0
            *   2. amount is less than the amount of money stored in current ATM
            *   3. amount is less than the balance in the user's account
            * */
            if (amount >= 0 && amount <= this.totalBalance && amount <= currentBalance){
                this.totalBalance -= amount;
                this.map.get(pin).setBalance(currentBalance - amount);
                AccountDao.updateAccount(this.map.get(pin));
                this.logger.info("User withdraw " + amount);
                System.out.println(Thread.currentThread().getName());
                System.out.println("Withdraw success! Current balance is " + this.map.get(pin).getBalance());
            } else if (amount > this.totalBalance) { // amount don't satisfy condition 2
                System.out.println("ATM out of Money");
                this.logger.warning("Withdraw Fail! Reason: ATM out of Money!");
            } else if (amount > currentBalance) {    // amount don't satisfy condition 3
                System.out.println("You don't have enough balance!");
                this.logger.warning("Withdraw Fail! Reason: Balance not enough!");
            } else {                                 // amount don't satisfy condition 1
                System.out.println("Invalid amount!");
                this.logger.warning("Invalid input!");
            }
        }
    }


    /**
     * deposit(int pin, int amount) is used to deposit money of account
     * @param pin Account pin
     * @param amount Amount user want to withdraw
     */
    private void deposit(int pin, int amount){
        synchronized (this.map.get(pin)) {
            int currentBalance = AccountDao.selectByPin(pin).getBalance();
            // update the information in our map
            this.map.get(pin).setBalance(currentBalance);
            // The amount of money to be deposit cannot be negative
            if (amount <= 0) {
                System.out.println("Invalid amount!");
                this.logger.warning("Invalid input!");
                return;
            }
            // The amount of money to be deposit plus the current amount of money in current ATM
            // cannot exceed the maximum money the ATM can store
            if(this.totalBalance + amount > MAX_STORAGE){
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
        // loop all the commands in cmd[] and execute corresponding method
        for (int i = 0; i < n; i++){
            if (!authenticated){
                authenticated = login(cmd[i]);
                if (authenticated) pin = cmd[i];
                continue;
            }
            // You can use sleep to check the synchronization
            if (i % 3 == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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


    public static void main(String[] args) {
        ATM atm = new ATM(3000);
        while (true){ // The machine is running
            System.out.println("Welcome to ATM!");
            int inputPin;
            while (true){
                System.out.println("Please enter PIN:");
                inputPin = atm.scanner.nextInt();
                atm.logger.info("User Input: " + inputPin);
                if (atm.login(inputPin)) {
                    atm.logger.info("User " + inputPin + " login success!");
                    break;
                } else {
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
                int num = atm.scanner.nextInt();
                atm.logger.info("User input num: " + num);

                if (num == 1) {
                    atm.query(inputPin);
                    atm.logger.info("User check balance.");
                }
                else if (num == 2){
                    System.out.println("Please enter the amount of money to withdraw:");
                    int amount = atm.scanner.nextInt();
                    atm.withdraw(inputPin, amount);
                } else if (num == 3){
                    System.out.println("Please enter the amount of money to deposit:");
                    int amount = atm.scanner.nextInt();
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
