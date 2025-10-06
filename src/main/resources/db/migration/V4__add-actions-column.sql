alter table config add column actions jsonb;
update config.config set actions = '[]'::jsonb;
