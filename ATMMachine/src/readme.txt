ATM machine:
1. Login with 4-digit PIN
2. Check account balance
3. Withdraw money
4. Deposit money

Dependency:
    mysql-connector-java-8.0.13

Directory:
src
    --- dao
        --- AccountDao.java
    --- entity
        --- Account.java
        --- ATM.java
    --- test
        --- test01.java
        --- test02.java
    --- util
        --- DBUtil.java
    --- info.log


How to build?
Step 1:
    Assumption:
    1. User cannot deposit money exceed the ATM can store
    2. Deposit and Without number cannot be negative
    3. User can only enter digit(0 - 9)

Step 2:
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

How to run this application?
1. Run the main method in ATM class will bring you to an ATM machine via CLI
    The default account information store in ATM is belows:

    Just run the main method and follow the instructions!

How to test the application?
1. Test for two user access the same account in two ATM
    Run main method in test1.java
2. Test for simulating concurrent online users
    Run main method in test2.java
