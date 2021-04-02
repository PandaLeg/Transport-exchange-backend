
create table properties(
    id int8 not null,
    ru_name varchar(100),
    en_name varchar(100),
    ua_name varchar(100),
    property varchar(50),
    primary key (id)
);


insert into properties values(4, 'Верхняя', 'Top', 'Верхнє', 'loading');
insert into properties values(5, 'Задняя', 'Back', 'Заднє', 'loading');
insert into properties values(6, 'Боковая', 'Side', 'Бічне', 'loading');

insert into properties values(7, 'Со снятием стоек', 'With racks removal', 'Зі зняттям стійок', 'loading');
insert into properties values(8, 'Со снятием поперечин', 'With crossbars removal', 'Зі зняттям поперечин', 'loading');
insert into properties values(9, 'Со полной растентовкой', 'With full cover', 'З повною розтентовкою', 'loading');
insert into properties values(10, 'Без ворот', 'Without gates', 'Без воріт', 'loading');

insert into properties values(11, 'Верхняя', 'Top', 'Верхнє', 'unloading');
insert into properties values(12, 'Задняя', 'Back', 'Заднє', 'unloading');
insert into properties values(13, 'Боковая', 'Side', 'Бічне', 'unloading');
insert into properties values(14, 'Со снятием стоек', 'With racks removal', 'Зі зняттям стійок', 'unloading');
insert into properties values(15, 'Со снятием поперечин', 'With crossbars removal', 'Зі зняттям поперечин', 'unloading');
insert into properties values(16, 'Со полной растентовкой', 'With full cover', 'З повною розтентовкою', 'unloading');
insert into properties values(17, 'Без ворот', 'Without gates', 'Без воріт', 'unloading');

insert into properties values(18, 'TIR', 'TIR', 'TIR', 'permissionType');
insert into properties values(19, 'CMR', 'CMR', 'CMR', 'permissionType');
insert into properties values(20, 'По декларации', 'By declaration', 'По декларації', 'permissionType');
insert into properties values(21, 'Медкнижка', 'Medical record', 'Медкнига', 'permissionType');
insert into properties values(22, 'T1', 'T1', 'T1', 'permissionType');
insert into properties values(23, 'ЕКМТ', 'CEMT', 'ЕКМТ', 'permissionType');

insert into properties values(24, 'Сумма', 'Amount', 'Сума', 'typePayment');
insert into properties values(25, 'Запрос ставки', 'Bid request', 'Запит ставки', 'typePayment');

insert into properties values(26, 'км', 'km', 'км', 'costPer');
insert into properties values(27, 'сутки', 'day', 'добу', 'costPer');

insert into properties values(28, 'наличными', 'cash','готівкою', 'paymentForm');
insert into properties values(29, 'безналичными', 'non-cash', 'безготівкою', 'paymentForm');
insert into properties values(30, 'комбинированная', 'combined', 'комбінована', 'paymentForm');
insert into properties values(31, 'карта', 'card', 'карта', 'paymentForm');
insert into properties values(32, 'электронный платёж', 'electronic payment', 'електронний платіж', 'paymentForm');

insert into properties values(33, 'на загрузке', 'on loading', 'на завантаженні', 'paymentTime');
insert into properties values(34, 'на выгрузке', 'unloading', 'при розвантаженні', 'paymentTime');
insert into properties values(35, 'по оригиналам', 'by originals', 'за оригіналами', 'paymentTime');
