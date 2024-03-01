create table config(
  id varchar(255) not null,
	name varchar(255) not null,
	owner_id varchar(255) not null,
	value jsonb not null
)
