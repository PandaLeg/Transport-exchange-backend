create table cargo_offer (
    id int8 not null,
    additional varchar(255),
    cargo_id int8 not null,
    user_id int8,
    legal_user_id int8,
    primary key (id)
);

alter table if exists cargo_offer
    add constraint cargo_offer_cargo_id_fk
    foreign key (cargo_id) references cargo;

alter table if exists cargo_offer
    add constraint cargo_offer_user_id_fk
    foreign key (user_id) references users;

alter table if exists cargo_offer
    add constraint cargo_offer_legal_user_id_fk
    foreign key (legal_user_id) references legal_users;
