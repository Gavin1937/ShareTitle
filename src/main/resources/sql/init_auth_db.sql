
BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS auth(
    username        TEXT PRIMARY KEY                    NOT NULL,
    auth_hash       TEXT                                NOT NULL   -- MD5 hash of username and password
);

CREATE TABLE IF NOT EXISTS table_update_info(
    name            TEXT PRIMARY KEY                    NOT NULL,
    mtime           INTEGER                             DEFAULT 0  -- unix timestamp
);

REPLACE INTO table_update_info VALUES("auth", STRFTIME("%s", "now"));

COMMIT;

