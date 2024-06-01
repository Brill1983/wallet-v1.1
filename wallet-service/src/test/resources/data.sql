delete from users;
alter table users alter column id restart with 1;
delete from wallets;
alter table wallets alter column id restart with 1;
delete from transactions;
alter table transactions alter column id restart with 1;