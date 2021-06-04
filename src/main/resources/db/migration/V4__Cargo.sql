
create table cargo (
    id int8 not null,
    name varchar(100) not null,
    type_transportation varchar(100) not null,
    name_container varchar(100),
    name_car varchar(100),
    count int4,
    weight_from varchar(100),
    weight_up_to varchar(100),
    volume_from varchar(100),
    volume_up_to varchar(100),
    length_cargo varchar(50),
    width_cargo varchar(50),
    height_cargo varchar(50),
    adr varchar(50),
    loading_date_from date not null,
    loading_date_by date not null,
    body_type varchar(100) not null,
    incoterms varchar(100),
    cost varchar(20),
    currency varchar(10),
    prepayment varchar(20),
    additional varchar(255),
    status varchar(100),
    date_added timestamp not null,
    user_id int8,
    legal_user_id int8,
    primary key (id)
);

/*create table sea_cargo (
    id int8 not null,
    name varchar(100) not null,
    name_container varchar(100) not null,
    count_container int4,
    weight_from varchar(100),
    weight_up_to varchar(100),
    volume_from varchar(100),
    volume_up_to varchar(100),
    length_cargo varchar(50),
    width_cargo varchar(50),
    height_cargo varchar(50),
    imo varchar(50),
    loading_date_from date not null,
    loading_date_by date not null,
    vessel_type varchar(100) not null,
    incoterms varchar(100),
    cost varchar(20),
    currency varchar(10),
    prepayment varchar(20),
    additional varchar(255),
    status varchar(100),
    user_id int8,
    legal_user_id int8,
    primary key (id)
);*/

create table point_lu_cargo (
    id int8 not null,
    city_from varchar(255),
    country_from varchar(255),
    city_to varchar(255),
    country_to varchar(255),
    lat_first_point numeric(10,6),
    lng_first_point numeric(10,6),
    lat_second_point numeric(10,6),
    lng_second_point numeric(10,6),
    cargo_id int8 not null,
    primary key (id)
);

create table photo_cargo (
    id int8 not null,
    photo_url varchar(255),
    cargo_id int8 not null,
    primary key (id)
);

create table cargo_properties (
    cargo_id int8 not null references cargo,
    property_id int8 not null references properties,
    primary key (cargo_id, property_id)
);

alter table if exists cargo
    add constraint cargo_user_id_fk
    foreign key (user_id) references users;

alter table if exists cargo
    add constraint cargo_legal_user_id_fk
    foreign key (legal_user_id) references legal_users;

alter table if exists point_lu_cargo
    add constraint point_lu_cargo_cargo_id_fk
    foreign key (cargo_id) references cargo;

alter table if exists photo_cargo
    add constraint photo_cargo_cargo_id_fk
    foreign key (cargo_id) references cargo;
