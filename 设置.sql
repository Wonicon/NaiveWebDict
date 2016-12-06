set password =password("onts0624");//这个是设置密码，我的代码那边设置的是onts0624
grant all privileges on naive2.* to 'root'@'%';//这个是设置远程可以使用root以不用密码的形式访问naive2库中的所有表
FLUSH PRIVILEGES;//使得上面的改变生效
create database naive2;//开始创建表格
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
