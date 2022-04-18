create database ATM;

create table accounts(
pin int(4) not null,
balance int not null,
primary key(pin));

INSERT INTO accounts VALUES(1234, 10000);
INSERT INTO accounts VALUES(1111, 1000);
INSERT INTO accounts VALUES(2222, 100);
INSERT INTO accounts VALUES(3333, 5000);
