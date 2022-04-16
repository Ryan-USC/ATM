package test;

import entity.ATM;
import entity.Account;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/*
 * ThreadPool to simulate the number of concurrent online users
 *
 * */
public class test2 {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        int[] cmd1 = new int[]{2222, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100,
                2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 2, 100, 4};
        int[] cmd2 = new int[]{1111, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50,
                2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50,
                2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 2, 50, 4};
        Account[] list = new Account[]{
                new Account(1111, 1000),
                new Account(2222, 2000),
                new Account(3333, 3000)
        };
        ATM[] atms = new ATM[]{
                new ATM(3000, cmd1, list),
                new ATM(5000, cmd2, list),
                new ATM(4000, cmd1, list),
                new ATM(2000, cmd2, list),
                new ATM(5000, cmd2, list),
                new ATM(4000, cmd1, list)
        };
        for (ATM atm: atms){
            executor.execute(atm);
        }
        executor.shutdown();
    }
}
