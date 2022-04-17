package entity;

public class Account {
    private Integer pin;
    private Integer balance;

    public Account() {
    }

    public Account(Integer pin, Integer balance) {
        this.pin = pin;
        this.balance = balance;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
