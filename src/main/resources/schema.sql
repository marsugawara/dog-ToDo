DROP TABLE person IF exists;

DROP TABLE comment if exists;
create table comment(id int not null primary key AUTO_INCREMENT, day char,text char);

DROP table schedule if exists;
create table schedule (id int not null primary key AUTO_INCREMENT, dogtype int, title varchar not null, endcheck int, day char, checktime date);
