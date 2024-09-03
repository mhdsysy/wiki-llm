#!/bin/bash
set -e

echo "Starting script to set up the dashboard ..."
echo "The current user is: $(whoami)"

# Use environment variables set in entrypoint.sh or provide default values
POSTGRES_DB=${POSTGRES_DB:-docker}
POSTGRES_USER=${POSTGRES_USER:-docker}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD:-secret}
DB_HOST=${DB_HOST:-127.0.0.1}
DB_PORT=${DB_PORT:-5432}

# Update the environment variables with dynamic values
export DATABASE_URL=postgres://${POSTGRES_USER}:${POSTGRES_PASSWORD}@${DB_HOST}:${DB_PORT}/${POSTGRES_DB}
export SITE_SEARCH_DATABASE_URL=postgres://${POSTGRES_USER}:${POSTGRES_PASSWORD}@${DB_HOST}:${DB_PORT}/${POSTGRES_DB}
export DASHBOARD_STATIC_DIRECTORY=/usr/share/pgml-dashboard/dashboard-static
export DASHBOARD_CMS_DIRECTORY=/usr/share/pgml-cms
export SEARCH_INDEX_DIRECTORY=/var/lib/pgml-dashboard/search-index
export ROCKET_SECRET_KEY=$(openssl rand -hex 32)
export ROCKET_ADDRESS=0.0.0.0
export RUST_LOG=info

# Execute the pgml-dashboard command
exec /usr/bin/pgml-dashboard
