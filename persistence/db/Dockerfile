FROM postgres:17.9

# Set default database credentials for local development
ENV POSTGRES_DB=rallyon \
    POSTGRES_USER=rallyon_app \
    POSTGRES_PASSWORD=rallyon_secret

EXPOSE 5432

# Verify the instance accepts connections before reporting healthy
HEALTHCHECK --interval=10s --timeout=5s --start-period=10s --retries=5 \
    CMD pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" || exit 1
