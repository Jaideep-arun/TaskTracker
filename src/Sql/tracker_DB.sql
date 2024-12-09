CREATE DATABASE trackerDB;

USE trackerDB;

DROP TABLE Sub_Category;

CREATE TABLE TICKETS(
    ticket_id VARCHAR(20) PRIMARY KEY,
    category_id VARCHAR(20),
    sub_category_id VARCHAR(20),
    assignee_Id VARCHAR(20),
    reported_Id VARCHAR(20),
    subject VARCHAR(50),
    description VARCHAR(200),
    status_id VARCHAR(20),
    priority_id VARCHAR(20),
    create_datetime DATETIME,
    last_modified_datetime DATETIME
);

CREATE TABLE Comment(
    comment_id VARCHAR(20) PRIMARY KEY,
    ticket_id VARCHAR(20),
    user_id VARCHAR(20),
    message VARCHAR(200),
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id)
);

CREATE TABLE Category(
    category_id VARCHAR(20) PRIMARY KEY,
    category_desc VARCHAR(20)
);
CREATE TABLE Sub_Category(
    sub_category_id VARCHAR(20) PRIMARY KEY,
    category_id VARCHAR(20),
    sub_category_desc VARCHAR(50)
    
);
CREATE TABLE Admin_team(
    admin_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50),
    emailId VARCHAR(20)
);
CREATE TABLE User(
    user_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50),
    emailId VARCHAR(80)
)

INSERT INTO Category values 
    ('CAT1','Hardware'),
    ('CAT2','Software'),
    ('CAT3','Access_Management'),
    ('CAT4','Status'),
    ('CAT5','Priority');

INSERT INTO Sub_Category VALUES
    ('SubCAT1','CAT1','Allocate_Laptop'),
    ('SubCAT2','CAT1','Allocate_Hardware'),
    ('SubCAT1','CAT1','Allocate_Laptop');

INSERT INTO Sub_Category VALUES
    ('SubCAT1', 'CAT1', 'Allocate_Laptop'),
    ('SubCAT2', 'CAT1', 'Allocate_CAT1'),
    ('SubCAT3', 'CAT1', 'Hardware_replacement'),
    ('SubCAT4', 'CAT2', 'Software_Installation'),
    ('SubCAT5', 'CAT2', 'Antivirus'),
    ('SubCAT6', 'CAT2', 'Email_Password_update'),
    ('SubCAT7', 'CAT2', 'Laptop_Slowness_issue'),
    ('SubCAT8', 'CAT2', 'Software_Issue'),
    ('SubCAT9', 'CAT3', 'Software_access'),
    ('SubCAT10', 'CAT3', 'Wifi_Access'),
    ('SubCAT11', 'CAT3', 'Database_Access'),
    ('SubCAT12', 'CAT3', 'VPN_Access'),
    ('SubCAT13', 'CAT4', 'Open'),
    ('SubCAT14', 'CAT4', 'Assigned'),
    ('SubCAT15', 'CAT4', 'In Progress'),
    ('SubCAT16', 'CAT4', 'Completed'),
    ('SubCAT17', 'CAT5', 'Low'),
    ('SubCAT18', 'CAT5', 'Medium'),
    ('SubCAT19', 'CAT5', 'High'),
    ('SubCAT20', 'CAT5', 'Critical');

INSERT INTO Admin_team VALUES
    ('Admin1','Jaideep','jai@admin.com'),
    ('Admin2','Arun','arun@admin.com');

INSERT INTO User VALUES
    ('User1','Jai','jai@user.com'),
    ('User2','babu','babu@user.com');

INSERT INTO TICKETS VALUES(
    'T1_2024',
    'CAT1',
    'SubCAT1',
    'Admin1',
    'User1',
    'Laptop Allocation',
    'Please allocate a new laptop for me as my old laptop is crashing often',
    'SubCAT13',
    'SubCAT19',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
SELECT * FROM tickets