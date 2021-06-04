
create table properties(
    id int8 not null,
    ru_name varchar(100),
    en_name varchar(100),
    ua_name varchar(100),
    property varchar(50),
    primary key (id)
);


insert into properties values(1, 'Верхняя', 'Top', 'Верхнє', 'loading');
insert into properties values(2, 'Задняя', 'Back', 'Заднє', 'loading');
insert into properties values(3, 'Боковая', 'Side', 'Бічне', 'loading');

insert into properties values(4, 'Со снятием стоек', 'With racks removal', 'Зі зняттям стійок', 'loading');
insert into properties values(5, 'Со снятием поперечин', 'With crossbars removal', 'Зі зняттям поперечин', 'loading');
insert into properties values(6, 'С полной растентовкой', 'With full cover', 'З повною розтентовкою', 'loading');
insert into properties values(7, 'Без ворот', 'Without gates', 'Без воріт', 'loading');

insert into properties values(8, 'Верхняя', 'Top', 'Верхнє', 'unloading');
insert into properties values(9, 'Задняя', 'Back', 'Заднє', 'unloading');
insert into properties values(10, 'Боковая', 'Side', 'Бічне', 'unloading');
insert into properties values(11, 'Со снятием стоек', 'With racks removal', 'Зі зняттям стійок', 'unloading');
insert into properties values(12, 'Со снятием поперечин', 'With crossbars removal', 'Зі зняттям поперечин', 'unloading');
insert into properties values(13, 'С полной растентовкой', 'With full cover', 'З повною розтентовкою', 'unloading');
insert into properties values(14, 'Без ворот', 'Without gates', 'Без воріт', 'unloading');

insert into properties values(15, 'TIR', 'TIR', 'TIR', 'permissionType');
insert into properties values(16, 'CMR', 'CMR', 'CMR', 'permissionType');
insert into properties values(17, 'По декларации', 'By declaration', 'За декларацією', 'permissionType');
insert into properties values(18, 'Медкнижка', 'Medical record', 'Медкнига', 'permissionType');
insert into properties values(19, 'T1', 'T1', 'T1', 'permissionType');
insert into properties values(20, 'ЕКМТ', 'CEMT', 'ЕКМТ', 'permissionType');
insert into properties values(21, 'Ж/Д накладная', 'Railway Bill', 'Залізнична накладна', 'permissionType');

insert into properties values(22, 'Сумма', 'Amount', 'Сума', 'typePayment');
insert into properties values(23, 'Запрос ставки', 'Bid request', 'Запит ставки', 'typePayment');

insert into properties values(24, 'км', 'km', 'км', 'costPer');
insert into properties values(25, 'сутки', 'day', 'добу', 'costPer');

insert into properties values(26, 'наличными', 'cash','готівкою', 'paymentForm');
insert into properties values(27, 'безналичными', 'non-cash', 'безготівкою', 'paymentForm');
insert into properties values(28, 'комбинированная', 'combined', 'комбінована', 'paymentForm');
insert into properties values(29, 'карта', 'card', 'карта', 'paymentForm');
insert into properties values(30, 'электронный платёж', 'electronic payment', 'електронний платіж', 'paymentForm');

insert into properties values(31, 'на загрузке', 'on loading', 'на завантаженні', 'paymentTime');
insert into properties values(32, 'на выгрузке', 'unloading', 'при розвантаженні', 'paymentTime');
insert into properties values(33, 'по оригиналам', 'by originals', 'за оригіналами', 'paymentTime');

insert into properties values(34, 'LCL(неполная)', 'LCL(incomplete)', 'LCL(неповна)', 'containerLoading');
insert into properties values(35, 'FCL(полная)', 'FCL(full)', 'FCL(повна)', 'containerLoading');
