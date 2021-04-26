create table cargo_offer (
    id int8 not null,
    additional varchar(255),
    cargo_id int8 not null references cargo(id),
    user_id int8 references users(id),
    legal_user_id int8 references legal_users(id),
    primary key (id)
);

create table transport_offer (
    id int8 not null,
    additional varchar(255),
    transport_id int8 not null references transport(id),
    user_id int8 references users(id),
    legal_user_id int8 references legal_users(id),
    primary key (id)
);
