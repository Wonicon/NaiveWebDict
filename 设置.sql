create database naive2;
use naive2;
create table count
(
word varchar(20),
dict_id int,
count int,
primary key(word,dict_id)
);
create table dict
(
dict_id int,
dict_name varchar(20),
primary key(dict_id,dict_name)
);
create table count
(
username varchar(20),
password varchar(20),
bool login,
primary key(username)
);
create table user_like
(
username varchar(20),
word varchar(20),
dict_id int,
primary key(username,word,dict_id)
);