#!/bin/sh

liquibase \
  --url=jdbc:postgresql://postgres:5432/user_service \
  --classpath=/liquibase/drivers/postgresql-42.7.11.jar \
  --username=$DB_USERNAME \
  --password=$DB_PASSWORD \
  --changeLogFile=changelog/user_service.yaml \
  update

liquibase \
  --url=jdbc:postgresql://postgres:5432/trip_service \
  --classpath=/liquibase/drivers/postgresql-42.7.11.jar \
  --username=$DB_USERNAME \
  --password=$DB_PASSWORD \
  --changeLogFile=changelog/trip_service.yaml \
  update

liquibase \
  --url=jdbc:postgresql://postgres:5432/worker_service \
  --classpath=/liquibase/drivers/postgresql-42.7.11.jar \
  --username=$DB_USERNAME \
  --password=$DB_PASSWORD \
  --changeLogFile=changelog/worker_service.yaml \
  update