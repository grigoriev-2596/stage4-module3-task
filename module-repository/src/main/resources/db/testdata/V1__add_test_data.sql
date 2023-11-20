insert into authors (name, creation_date, last_update_date)
values ('Ivan Testov', '2023-01-01 00:00:46', '2023-01-01 00:00:46');
insert into authors (name, creation_date, last_update_date)
values ('Petya Fomin', '2023-02-02 00:00:46', '2023-02-01 00:00:46');

insert into tags (name)
values ('weather');
insert into tags (name)
values ('games');

insert into news (title, content, author_id, creation_date, last_update_date)
values ('Weather in Minsk', 'It''s very sunny in Minsk today', 1, '2023-03-01 00:00:46', '2023-03-01 00:00:46');
insert into news (title, content, author_id, creation_date, last_update_date)
values ('CS:GO 2', 'Valve announced Counter-Strike 2', 2, '2023-04-01 00:00:46', '2023-04-01 00:00:46');

insert into comments (content, news_id, creation_date, last_update_date)
values ('It''s actually warm today', 1, '2023-03-01 01:00:46', '2023-03-01 01:00:46');
insert into comments (content, news_id, creation_date, last_update_date)
values ('we''ve been waiting there for a long time', 2, '2023-04-01 01:00:46', '2023-04-01 01:00:46');

insert into news_tags (news_id, tag_id)
values (1, 1);
insert into news_tags (news_id, tag_id)
values (2, 2);
