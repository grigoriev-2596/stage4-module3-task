create table authors
(
    id               bigserial    not null,
    creation_date    timestamp,
    last_update_date timestamp,
    name             varchar(255) not null,
    primary key (id)
);

create table comments
(
    id               bigserial    not null,
    content          varchar(255) not null,
    creation_date    timestamp,
    last_update_date timestamp,
    news_id          bigint,
    primary key (id)
);

create table news
(
    id               bigserial    not null,
    content          varchar(255) not null,
    creation_date    timestamp,
    last_update_date timestamp,
    title            varchar(255) not null,
    author_id        bigint,
    primary key (id)
);

create table news_tags
(
    news_id bigint not null,
    tag_id  bigint not null
);

create table tags
(
    id   bigserial    not null,
    name varchar(255) not null,
    primary key (id)
);

alter table if exists comments
    add constraint comment_news_fk foreign key (news_id) references news;
alter table if exists news
    add constraint news_author_fk foreign key (author_id) references authors;
alter table if exists news_tags
    add constraint news_tags_tag_id_fk foreign key (tag_id) references tags;
alter table if exists news_tags
    add constraint news_tags_news_id_fk foreign key (news_id) references news;