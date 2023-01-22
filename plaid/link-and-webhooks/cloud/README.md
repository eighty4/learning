# Cloud Platform

## Local dev

### Start platform dependencies
Local databases are managed via a Docker Compose config in a different repository
```
git clone git@github.com:eighty4/platform.git
cd platform
docker compose up -d
```

### Initialize database schema
```
./migrate.sh
```

### Create JWT keys
```
openssl genrsa -out jwt.private.pem 2048
openssl rsa -in jwt.private.pem -pubout -outform PEM -out jwt.public.pem
```

### Start API
Run the API for local development using the `start` script in the `cloud/service` package
```
yarn start
```
