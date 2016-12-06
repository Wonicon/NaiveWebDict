create database DICT;
use DICT;

create table user (
    username char(20) not null,
    password char(20) not null,
    login boolean not null,
    uid int not null auto_increment,
    primary key (uid)
);


create table dict (
    dict_name varchar(20) not null,
    dict_id int not null auto_increment,
    primary key (dict_id)
);

create table count (
    word varchar(20) not null,
    dict_id int not null,
    count int not null,
    primary key (word, dict_id),
    foreign key (dict_id) references dict(dict_id)
);

create table user_like (
    word varchar(20),
    uid int,
    dict_id int,
    primary key(word, uid, dict_id),
    foreign key (uid) references user(uid),
    foreign key (dict_id) references dict(dict_id)
);
