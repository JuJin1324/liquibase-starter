docker build --tag starter/liquibase-mysql:1.0 .
docker run -d \
-p 3312:3306 \
--name liquibase-starter-mysql \
starter/liquibase-mysql:1.0
