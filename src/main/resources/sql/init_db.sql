
BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS websites(
    id              INTEGER PRIMARY KEY AUTOINCREMENT   NOT NULL,
    title           TEXT                                NOT NULL,
    url             TEXT                                NOT NULL,
    domain          TEXT                                NOT NULL,
    parent_child    INTEGER                             DEFAULT 0, -- 0 (Parent), 1 (Child)
    is_visited      INTEGER                             DEFAULT 0, -- 0 (False), 1 (True)
    time            INTEGER                             DEFAULT 0  -- unix timestamp
);

CREATE TABLE IF NOT EXISTS table_update_info(
    name            TEXT PRIMARY KEY                    NOT NULL,
    mtime           INTEGER                             DEFAULT 0  -- unix timestamp
);

REPLACE INTO table_update_info VALUES("websites", STRFTIME("%s", "now"));

COMMIT;

