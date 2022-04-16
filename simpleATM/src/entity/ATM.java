package entity;

import java.util.HashMap;
import java.util.Scanner;

public class ATM implements Runnable{
    // The maximum money the machine can hold
    final static private Integer maxStorage = 10000;
    // The current money the machine holds
    private Integer totalBalance;
    // HashMap stores PIN and corresponding Account object
    HashMap<Integer, Account> map = new HashMap<>();
    // input
    private Scanner sc;
    // A array of commands
    int[] cmd;
    // authenticated to indicated whether the user login successfully
    boolean authenticated;

    /*
    * Constructor
    * */
    public ATM(Integer totalBalance) {
        this.totalBalance = totalBalance;
        sc = new Scanner(System.in);
        // Here we assume we have three accounts info
        Account account1 = new Account(1234,1000);
        map.put(1234,account1);
        Account account2 = new Account(2345,500);
        map.put(2345,account2);
        Account account3 = new Account(3456,100);
        map.put(3456,account3);
    }


    public ATM(Integer totalBalance, int[] cmd, Account[] list) {
        this.totalBalance = totalBalance;
        this.cmd = cmd;
        authenticated = false;
        for (Account current: list){
            map.put(current.getPin(), current);
        }
    }

    // validate PIN
    private boolean login(int pin){
        if (map.containsKey(pin)) return true;
        System.out.println("PIN is wrong!");
        return false;
    }

    /*
    * check current balance
    * */
    private void query(int pin){
        synchronized (this.map.get(pin)) {
            System.out.println("Your current balance is " + this.map.get(pin).getBalance());
        }
    }

    /*
    * Withdraw money
    * */
    private void withdraw(int pin, int amount){
        synchronized (this.map.get(pin)){
            System.out.println(Thread.currentThread().getName());
            if (amount >= 0 && amount <= this.totalBalance && amount <= this.map.get(pin).getBalance()){
                this.totalBalance -= amount;
                int currentBalance = this.map.get(pin).getBalance();
                this.map.get(pin).setBalance(currentBalance - amount);
                System.out.println("Withdraw success! Current balance is " + this.map.get(pin).getBalance());
            } else if (amount > this.totalBalance)
                System.out.println("ATM out of Money");
            else if (amount > this.map.get(pin).getBalance())
                System.out.println("You don't have enough balance!");
            else System.out.println("Invalid amount!");
        }
    }

    private void deposit(int pin, int amount){
        synchronized (this.map.get(pin)) {
            if (amount <= 0) {
                System.out.println("Invalid amount!");
                return;
            }
            if(this.totalBalance + amount > maxStorage){
                System.out.println("Fail! ATM out of storage! Try deposit less money.");
                return;
            }
            this.totalBalance += amount;
            int currentBalance = this.map.get(pin).getBalance();
            this.map.get(pin).setBalance(currentBalance + amount);
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

            if (i == n/2) {
                try {
                    Thread.sleep(1000);
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


    /*
    * Single ATM machine with CLI
    * */
    public static void main(String[] args) {
        ATM atm = new ATM(3000);
        while (true){
            System.out.println("Welcome to ATM!");
            int inputPin;
            while (true){
                System.out.println("Please enter PIN:");
                inputPin = atm.sc.nextInt();
                if (atm.map.containsKey(inputPin))
                    break;
                else System.out.println("Your PIN is WRONG!");
            }
            System.out.println("Success!");
            System.out.println("Please enter corresponding num to continue.");
            System.out.println("Enter 1 to check your balance.");
            System.out.println("Enter 2 to withdraw money.");
            System.out.println("Enter 3 to deposit money.");
            System.out.println("Enter 4 to logout.");
            while (true){
                int num = atm.sc.nextInt();
                synchronized (atm.map.get(inputPin)){
                    if (num == 1)
                        atm.query(inputPin);
                    else if (num == 2){
                        System.out.println("Please enter the amount of money to withdraw:");
                        int amount = atm.sc.nextInt();
                        atm.withdraw(inputPin, amount);
                    } else if (num == 3){
                        System.out.println("Please enter the amount of money to deposit:");
                        int amount = atm.sc.nextInt();
                        atm.deposit(inputPin, amount);
                    }else if(num == 4){
                        break;
                    } else System.out.println("Please enter a valid num!");
                }
            }
            System.out.println("Logout!");
        }
    }
}
