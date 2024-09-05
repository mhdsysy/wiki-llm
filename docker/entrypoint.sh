#!/bin/bash
set -e

echo "Starting script to set up PostgreSQL environment..."

# Check if environment variables are set, if not, set defaults
POSTGRES_DB=${POSTGRES_DB:-docker}
POSTGRES_USER=${POSTGRES_USER:-docker}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-secret}

echo "Starting Postgres"
service postgresql start

if ! sudo -u postgres psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='root'" | grep -q 1; then
  echo "Creating system user: root"
  sudo -u postgres createuser root --superuser --login 2> /dev/null 1>&2
fi

# Check if the system user already exists before attempting to create it
if id -u "$POSTGRES_USER" >/dev/null 2>&1; then
    echo "System user $POSTGRES_USER already exists, skipping creation."
else
    echo "Creating system user: $POSTGRES_USER"
    useradd $POSTGRES_USER -m
fi

# Ensure the .psql_history file exists in the home directory of DB_USER
PSQL_HISTORY_FILE="/home/$POSTGRES_USER/.psql_history"
if [ -f "$PSQL_HISTORY_FILE" ]; then
    echo ".psql_history file for user $POSTGRES_USER already exists."
else
    echo "Creating .psql_history file for user: $POSTGRES_USER"
    sudo -u $POSTGRES_USER touch "$PSQL_HISTORY_FILE"
fi

# Check if the PostgreSQL role already exists
if sudo -u postgres psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='$POSTGRES_USER'" | grep -q 1; then
    echo "PostgreSQL role $POSTGRES_USER already exists, skipping creation."
else
    echo "Creating PostgreSQL role: $POSTGRES_USER with superuser privileges"
    sudo -u postgres createuser $POSTGRES_USER --superuser --login
fi

# Always set the password for the PostgreSQL role
echo "Setting password for PostgreSQL role: $POSTGRES_USER"
sudo -u postgres psql -c "ALTER ROLE $POSTGRES_USER PASSWORD '$POSTGRES_PASSWORD' SUPERUSER LOGIN"

# Check if the database already exists
if sudo -u postgres psql -lqt | cut -d \| -f 1 | grep -qw "$POSTGRES_DB"; then
    echo "Database $POSTGRES_DB already exists, skipping creation."
else
    echo "Creating database: $POSTGRES_DB owned by user: $POSTGRES_USER"
    sudo -u postgres createdb -O $POSTGRES_USER $POSTGRES_DB
fi

# Grant all privileges on the database $POSTGRES_DB to $POSTGRES_USER (idempotent)
echo "Granting all privileges on database: $POSTGRES_DB to user: $POSTGRES_USER"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $POSTGRES_DB TO $POSTGRES_USER"

# Set the search_path if not already set
CURRENT_SEARCH_PATH=$(sudo -u postgres psql -d $POSTGRES_DB -tAc "SHOW search_path")
if [[ "$CURRENT_SEARCH_PATH" == *"pgml"* ]]; then
    echo "Search path for $POSTGRES_USER in database $POSTGRES_DB already includes pgml, skipping."
else
    echo "Setting search_path for PostgreSQL role: $POSTGRES_USER in database: $POSTGRES_DB"
    sudo -u postgres psql -d $POSTGRES_DB -c "ALTER ROLE $POSTGRES_USER SET search_path TO public,pgml"
fi

sudo -u postgres psql -d $POSTGRES_DB -c "CREATE EXTENSION IF NOT EXISTS pgml"
sudo -u postgres psql -d $POSTGRES_DB -c "CREATE EXTENSION IF NOT EXISTS vector"

echo "PostgreSQL setup complete."

# Execute any additional commands or keep the container running
exec "$@"
