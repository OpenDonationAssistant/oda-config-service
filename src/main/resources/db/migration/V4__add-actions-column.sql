alter table config add column actions jsonb;
update config set actions = '[]'::jsonb;
