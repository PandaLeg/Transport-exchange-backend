insert into roles (id, name) values (1, 'ROLE_ADMIN'), (2, 'ROLE_USER'), (3, 'ROLE_LEGAL_USER');

insert into users values(1, 'admin', 'kurtlansfer@gmail.com', 'Panda', 'Kung', 'Fu', 'Ukraine', 'Kyiv',
'+380669182530');
insert into user_roles values (1, 1), (1, 2);

insert into legal_users values(2, 'julia', 'julia29@gmail.com', 'Julia', 'Steshko', 'Urievna', 'Russian Federation',
'Moscow','+380669182530', 'TOP CARGO', 'UUIDCARGO_LT_899865');
insert into legal_user_roles values (2, 1), (2, 3);
