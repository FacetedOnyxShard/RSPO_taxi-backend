#!/bin/bash

HOST_ADDRESS=$(ip route show default | awk '{print $3}')
USER_SERVICE_PORT=8001
TRIP_SERVICE_PORT=8002
WORKER_SERVICE_PORT=8003

echo "Регистрация пассажира:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Иван Петров",
    "email": "ivan@example.com",
    "phone": "+79161234567"
  }')
echo "$response" | jq
PASSENGER_ID=$(echo "$response" | jq -r '.id')
echo "  Passenger ID: $PASSENGER_ID"


echo "Регистрация водителя:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Алексей Водитель",
    "email": "driver@example.com",
    "phone": "+79261234567",
    "licenseNumber": "77УР123456",
    "status": "FREE"
  }')
echo "$response" | jq
DRIVER_ID=$(echo "$response" | jq -r '.id')
echo "  Driver ID: $DRIVER_ID"


echo "Создание поездки:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips \
  -H "Content-Type: application/json" \
  -d "{
    \"passenger_id\": \"$PASSENGER_ID\",
    \"origin\": \"ул. Ленина, 10\",
    \"destination\": \"ул. Пушкина, 20\"
  }")
echo "$response" | jq
TRIP_ID=$(echo "$response" | jq -r '.id')
echo "  Trip ID: $TRIP_ID"


echo -e "Получить информацию о поездке:"
curl -s -X GET http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID | jq


echo -e "Получить список поездок пассажира:"
curl -s -X GET "http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips?passenger_id=$PASSENGER_ID" | jq


# Изменение статуса поездки
echo "Принять поездку водителем: "
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "ACCEPTED"}' | jq

echo "Начать поездку: "
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "STARTED"}' | jq

echo "Завершить поездку: "
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}' | jq
    

echo -e "\nПросмотр уведомлений:"
curl -s -X GET "http://$HOST_ADDRESS:$WORKER_SERVICE_PORT/notifications?trip_id=$TRIP_ID" | jq


# helpers
curl -s -X GET http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/passengers | jq
curl -s -X GET http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/drivers | jq