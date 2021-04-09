create table chats(
    id int8 not null,
    primary key (id)
);

create table user_chats(
    user_id int8 not null references users,
    chat_id int8 not null references chats,
    primary key (user_id, chat_id)
);

create table legal_user_chats(
    legal_user_id int8 not null references legal_users,
    chat_id int8 not null references chats,
    primary key (legal_user_id, chat_id)
);

create table chat_messages(
    id int8 not null,
    message varchar(255) not null,
    chat_id int8 not null references chats(id),
    user_id int8 references users(id),
    legal_user_id int8 references legal_users(id),
    primary key (id)
);

