
create table transport(
    id int8 not null,
    body_type varchar(100) not null,
    carrying_capacity_from varchar(100),
    carrying_capacity_up_to varchar(100),
    volume_from varchar(100),
    volume_up_to varchar(100),
    length_transport varchar(50),
    width_transport varchar(50),
    height_transport varchar(50),
    adr varchar(50),
    loading_date_from date not null,
    loading_date_by date not null,
/*    city_first_loading_point varchar(100) not null,
    country_first_loading_point varchar(100) not null,
    city_first_unloading_point varchar(100) not null,
    country_first_unloading_point varchar(100) not null,
    lat_first numeric(10,6) not null,
    lng_first numeric(10,6) not null,
    lat_second numeric(10,6) not null,
    lng_second numeric(10,6) not null,*/
    cost varchar(20),
    currency varchar(10),
    prepayment varchar(20),
    additional varchar(255),
    user_id int8,
    legal_user_id int8,
    primary key (id)
);

create table point_lu_transport (
    id int8 not null,
    city_from varchar(255),
    country_from varchar(255),
    city_to varchar(255),
    country_to varchar(255),
    lat_first_point numeric(10,6),
    lng_first_point numeric(10,6),
    lat_second_point numeric(10,6),
    lng_second_point numeric(10,6),
    transport_id int8 not null,
    primary key (id)
);

create table photo_transport(
    id int8 not null,
    photo_url varchar(255),
    transport_id int8 not null,
    primary key (id)
);

create table transport_properties(
    transport_id int8 not null references transport,
    property_id int8 not null references properties,
    primary key (transport_id, property_id)
);

alter table if exists transport
    add constraint transport_user_id_fk
    foreign key (user_id) references users;

alter table if exists transport
    add constraint transport_legal_user_id_fk
    foreign key (legal_user_id) references legal_users;

alter table if exists point_lu_transport
    add constraint point_lu_transport_transport_id_fk
    foreign key (transport_id) references transport;

alter table if exists photo_transport
    add constraint photo_transport_transport_id_fk
    foreign key (transport_id) references transport;
