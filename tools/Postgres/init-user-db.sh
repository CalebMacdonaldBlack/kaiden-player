#!/bin/bash
set -e
createuser -s kaiden_player

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE kaiden_player_dev ENCODING 'UTF-8';
    GRANT ALL PRIVILEGES ON DATABASE kaiden_player_dev to kaiden_player;

    CREATE DATABASE kaiden_player_test ENCODING 'UTF-8';
    GRANT ALL PRIVILEGES ON DATABASE kaiden_player_test kaiden_player;
EOSQL
