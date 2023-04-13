FROM mysql:8.0.31
LABEL maintainer="jujin1324@daum.net"
LABEL dockerhub="https://hub.docker.com/_/mysql"

EXPOSE 3312

ADD liquibase-starter.cnf /etc/mysql/conf.d/
ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE liquibase_local
ENV MYSQL_USER liquibase_starter
ENV MYSQL_PASSWORD liquibase_starter
