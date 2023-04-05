create table websites (
    id serial primary key not null,
    url varchar(2000) unique not null,
    login varchar(20) unique not null,
    password varchar(2000) unique not null
);

comment on table websites is 'Таблица сайтов, зарегестрированных в сервисе подмены ссылок';
comment on column websites.id is 'Уникальный идентификатор сайта';
comment on column websites.url is 'Url сайта';
comment on column websites.login is 'Логин ассоциированного с сайтом администратора';
comment on column websites.password is 'Пароль ассоциированного с сайтом администратора';