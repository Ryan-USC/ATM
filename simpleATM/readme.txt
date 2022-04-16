ATM machine:
1. Login with 4-digit PIN
2. Check account balance
3. Withdraw money
4. Deposit money

How to build?
Assumption:
1. User cannot deposit money exceed the ATM can store
2. Deposit and Without number cannot be negative

Step 1:
    Entity:
        Account:
            Attributes:
                pin indicate 4-digit PIN number
                balance indicate account balance
        ATM:
            Attributes:
                maxStorage indicate the maximum money the ATM machine can store
                totalBalance indicate current amount of money the ATM stores
                map is a HashMap whose key is pin and value is Account Object

            Methods:
                login(int pin): Authentication step
                query(int pin): check balance of current account
                withdraw(int pin, int amount): withdraw money
                deposit(int pin, int amount): deposit money
Step 2:
How to run this application?
1. Run the main method in ATM class will bring you to an ATM machine via CLI
    The default account information store in ATM is belows:
        Account1: Pin: 1234
                  Balance: 1000
        Account2: Pin: 2345
                  Balance: 500
        Account2: Pin: 3456
                  Balance: 100
    Just run the main method and follow the instructions!

How to test the application?
1. Test for two user access the same account in two ATM
    Run main method in test1.java
2. Test for simulating concurrent online users
    Run main method in test2.java