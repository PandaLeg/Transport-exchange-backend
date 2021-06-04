insert into roles (id, name) values (1, 'ROLE_ADMIN'), (2, 'ROLE_USER'), (3, 'ROLE_LEGAL_USER');

insert into users values(1, 'admin', 'kurtlansfer@gmail.com', 'Panda', 'Kung', 'Fu', null, 'Ukraine', 'Kyiv',
'+380669182530', null, null, null, null, CURRENT_TIMESTAMP);
insert into user_roles values (1, 1), (1, 2);

insert into users values(2, 'julia', 'julia29@gmail.com', null, null, null, 'Julia Steshko Urievna',
'Russian Federation','Moscow', '+380669182530', 'TOR TRANSPORT', 'UUIDTRANSPORT_LT_999869', null, null,
CURRENT_TIMESTAMP);
insert into user_roles values (2, 1), (2, 3);
