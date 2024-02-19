create database customer_reports;
use  customer_reports;

create table CUSTOMER(
	ID bigint primary key,
    FIRSTNAME varchar(30),
    LASTNAME varchar(30),
    EMAIL varchar(255),
    PHONE varchar(255),
    GENDER char(20) not null,
    AGE int not null
)