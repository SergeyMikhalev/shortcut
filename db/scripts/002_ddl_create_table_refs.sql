create table refs (
    id serial primary key not null,
    url varchar(2000) unique not null,
    code varchar(2000) unique not null,
    site_id int not null references websites(id)
);

comment on table refs is 'Таблица ссылок и кодов замены к ним, ассоциированных с конкретным сайтом';
comment on column refs.id is 'Уникальный ссылки';
comment on column refs.url is 'Url ссылки';
comment on column refs.code is 'Код замены';
comment on column refs.site_id is 'Уникальный идентификатор сайта, частью которого является ссылка';