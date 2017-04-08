#!/bin/bash
set -e
createuser -s blacksales

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE blacksales_dev ENCODING 'UTF-8';
    GRANT ALL PRIVILEGES ON DATABASE blacksales_dev to blacksales;

    CREATE DATABASE blacksales_test ENCODING 'UTF-8';
    GRANT ALL PRIVILEGES ON DATABASE blacksales_test blacksales;
EOSQL
