create table if not exists team (
  id bigserial primary key,
  name varchar(100) not null unique,
  created_at timestamp not null default now()
);