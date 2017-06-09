DROP TABLE person if exists;
CREATE TABLE person(age CHAR, name CHAR);

DROP TABLE comento if exists;
create table comento(comm char);

DROP table schedule if exists;
create table schedule (id int not null primary key AUTO_INCREMENT,dogtype int, title varchar not null, endcheck int, day char, checktime date, comment varchar);
