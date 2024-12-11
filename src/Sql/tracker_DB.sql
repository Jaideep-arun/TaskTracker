CREATE DATABASE trackerDB;

USE trackerDB;

DROP TABLE tickets;

CREATE TABLE TICKETS(
    ticket_id VARCHAR(20) PRIMARY KEY,
    category_id VARCHAR(20),
    sub_category_id VARCHAR(50),
    assignee_Id VARCHAR(20),
    reported_Id VARCHAR(20),
    subject VARCHAR(50),
    description VARCHAR(200),
    status_id VARCHAR(20),
    priority_id VARCHAR(20),
    create_datetime DATETIME,
    last_modified_datetime DATETIME
);
DROP TABLE comments;
CREATE TABLE Comments(
    comment_id VARCHAR(20) PRIMARY KEY,
    ticket_id VARCHAR(20),
    user_id VARCHAR(20),
    message VARCHAR(200),
    FOREIGN KEY (ticket_id) REFERENCES Tickets(ticket_id)
);

CREATE TABLE Category(
    category_id VARCHAR(20) PRIMARY KEY,
    category_desc VARCHAR(20)
);
CREATE TABLE Sub_Category(
    sub_category_id VARCHAR(50) PRIMARY KEY,
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
    ('Hardware','Hardware'),
    ('Software','Software'),
    ('Access_Management','Access_Management'),
    ('Status','Status'),
    ('Priority','Priority');

INSERT INTO Sub_Category VALUES
    ('SubCAT1','CAT1','Allocate_Laptop'),
    ('SubCAT2','CAT1','Allocate_Hardware'),
    ('SubCAT1','CAT1','Allocate_Laptop');

INSERT INTO Sub_Category VALUES
    ('Allocate_Laptop', 'Hardware', 'Allocate_Laptop'),
    ('Allocate_Hardware', 'Hardware', 'Allocate_Hardware'),
    ('Hardware_replacement', 'Hardware', 'Hardware_replacement'),
    ('Software_Installation', 'Software', 'Software_Installation'),
    ('Antivirus', 'Software', 'Antivirus'),
    ('Email_Password_update', 'Software', 'Email_Password_update'),
    ('Laptop_Slowness_issue', 'Software', 'Laptop_Slowness_issue'),
    ('Software_Issue', 'Software', 'Software_Issue'),
    ('Software_access', 'Access_Management', 'Software_access'),
    ('Wifi_Access', 'Access_Management', 'Wifi_Access'),
    ('Database_Access', 'Access_Management', 'Database_Access'),
    ('VPN_Access', 'Access_Management', 'VPN_Access'),
    ('Open', 'Status', 'Open'),
    ('Assigned', 'Status', 'Assigned'),
    ('In_Progress', 'Status', 'In_Progress'),
    ('Completed', 'Status', 'Completed'),
    ('Low', 'Priority', 'Low'),
    ('Medium', 'Priority', 'Medium'),
    ('High', 'Priority', 'High'),
    ('Critical', 'Priority', 'Critical');

INSERT INTO Admin_team VALUES
    ('Admin1','Jaideep','jai@admin.com'),
    ('Admin2','Arun','arun@admin.com');

INSERT INTO User VALUES
    ('User1','Jai','jai@user.com'),
    ('User2','babu','babu@user.com');

INSERT INTO TICKETS VALUES(
    'T1_2024',
    'Hardware',
    'Allocate_Laptop',
    'Admin1',
    'User1',
    'Laptop Allocation',
    'Please allocate a new laptop for me as my old laptop is crashing often',
    'Open',
    'High',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
SELECT * FROM tickets

SELECT * FROM tickets WHERE ticket_id = "T1_2024"

INSERT INTO comments VALUES(
   'ComTic1',
   'T1_2024',
    'User1',
    'Change Laptop'
);
UPDATE tickets SET assignee_Id='Admin2' , last_modified_datetime = CURRENT_TIMESTAMP WHERE ticket_id = 'T1_2024';
--from Java test
UPDATE tickets SET assignee_Id='Admin1' WHERE ticket_id = 'T1_2024';

DELETE FROM tickets WHERE ticket_id = 'T2_2024';

INSERT INTO TICKETS VALUES(
    'T1_2024',
    'Hardware',
    'Allocate_Laptop',
    'Admin1',
    'User1',
    'Laptop Allocation',
    'Please allocate a new laptop for me as my old laptop is crashing often',
    'Open',
    'High',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO comments VALUES
    ('com1','T1_2024','Admin1','A new Laptop will be given by tomorrow');

select count (message) from comments where ticket_id='T1_2024';

select message from comments where ticket_id='T1_2024';

INSERT INTO TICKETS VALUES('T2_2024','Software','Laptop_Slowness_issue','null','User1','Laptop Slow','Laptop is very slow and running very hot','Critical','Open',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

ALTER Table tickets MODIFY COLUMN sub_category_id VARCHAR(50);


