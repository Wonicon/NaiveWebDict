create table user (
    username char(20) not null,
    password char(20) not null,
    login boolean not null,
    uid int not null auto_increment,
    primary key (uid)
);
