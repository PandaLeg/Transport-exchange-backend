create sequence hibernate_sequence start 1 increment 1;

create table users(
    id int8 not null,
    password varchar(100) not null,
    email varchar(100) not null,
    first_name varchar(50),
    last_name varchar(50),
    patronymic varchar(50),
    full_name varchar(100),
    country varchar(50),
    city varchar(50),
    phone varchar(50),
    company_name varchar(50),
    company_code varchar(50),
    profile_picture varchar(255),
    profile_background varchar(255),
    last_visit timestamp,
    status varchar(30),
    primary key (id)
);

create table roles (
    id int8 not null,
    name varchar(255),
    primary key (id)
);

create table user_roles(
    user_id int8 not null references users,
    role_id int8 not null references roles,
    primary key (user_id, role_id)
);

create table legal_users(
    id int8 not null,
    password varchar(100) not null,
    email varchar(100) not null,
    first_name varchar(100),
    last_name varchar(100),
    patronymic varchar(100),
    country varchar(50),
    city varchar(50),
    phone varchar(50),
    company_name varchar(50),
    company_code varchar(50),
    profile_picture varchar(255),
    profile_background varchar(255),
    primary key (id)
);

create table legal_user_roles(
    legal_user_id int8 not null references legal_users,
    role_id int8 not null references roles,
    primary key (legal_user_id, role_id)
);

create table confirmation(
    id int8 not null,
    description varchar(255),
    user_id int8 not null references users(id)
)



