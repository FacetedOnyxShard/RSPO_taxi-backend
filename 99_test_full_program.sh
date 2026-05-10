#!/bin/bash

HOST_ADDRESS=$(ip route show default | awk '{print $3}')
USER_SERVICE_PORT=8001
TRIP_SERVICE_PORT=8002
WORKER_SERVICE_PORT=8003

source .env
PASSENGER_PASSWORD=$(echo -n "$PASSENGER_PASSWORD" | tr -d '\r\n"' | sed 's/\\//g')
DRIVER_PASSWORD=$(echo -n "$DRIVER_PASSWORD" | tr -d '\r\n"' | sed 's/\\//g')

echo "Регистрация пассажира:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/passengers \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Иван Петров\",
    \"email\": \"ivan@example.com\",
    \"phone\": \"+79161234567\",
    \"password\": \"$PASSENGER_PASSWORD\"
  }")
echo "$response" | jq
PASSENGER_ID=$(echo "$response" | jq -r '.id')
echo "  Passenger ID: $PASSENGER_ID"

echo "Логин пассажира:"
auth_response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"ivan@example.com\",
    \"password\": \"$PASSENGER_PASSWORD\",
    \"role\": \"passenger\"
  }")
PASSENGER_TOKEN=$(echo "$auth_response" | jq -r '.token')
echo "  Token = $PASSENGER_TOKEN"
echo "  Token получен"

echo "Регистрация водителя:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/drivers \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Алексей Водитель\",
    \"email\": \"driver@example.com\",
    \"phone\": \"+79261234567\",
    \"licenseNumber\": \"77УР123456\",
    \"status\": \"FREE\",
    \"password\": \"$DRIVER_PASSWORD\"
  }")
echo "$response" | jq
DRIVER_ID=$(echo "$response" | jq -r '.id')
echo "  Driver ID: $DRIVER_ID"

echo "Логин водителя:"
auth_response=$(curl -s -X POST http://$HOST_ADDRESS:$USER_SERVICE_PORT/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"driver@example.com\",
    \"password\": \"$DRIVER_PASSWORD\",
    \"role\": \"driver\"
  }")
DRIVER_TOKEN=$(echo "$auth_response" | jq -r '.token')
echo "  Token получен"

echo "Создание поездки:"
response=$(curl -s -X POST http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d "{
    \"passenger_id\": \"$PASSENGER_ID\",
    \"origin\": \"ул. Ленина, 10\",
    \"destination\": \"ул. Пушкина, 20\",
    \"distance\": 12.5
  }")
echo "$response"
echo "$response" | jq
TRIP_ID=$(echo "$response" | jq -r '.id')
echo "  Trip ID: $TRIP_ID"

echo "Получить информацию о поездке:"
curl -s -X GET http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq

echo "Получить список поездок пассажира:"
curl -s -X GET "http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips?passenger_id=$PASSENGER_ID" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq

echo "Принять поездку водителем:"
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status": "ACCEPTED"}' | jq

echo "Начать поездку:"
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status": "STARTED"}' | jq

echo "Завершить поездку:"
curl -s -X PATCH http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status": "COMPLETED"}' | jq

echo "Оценить поездку (5 звёзд):"
curl -s -X POST http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID/rate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"rating": 5}' | jq

echo "Повторный запрос информации о поездке (с рейтингом):"
curl -s -X GET http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/$TRIP_ID \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq

echo -e "\nПросмотр уведомлений:"
curl -s -X GET "http://$HOST_ADDRESS:$WORKER_SERVICE_PORT/notifications?trip_id=$TRIP_ID" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq

echo -e "\nСписок всех пассажиров (требует авторизацию):"
curl -s -X GET http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/passengers \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq

echo -e "\nСписок всех водителей (требует авторизацию):"
curl -s -X GET http://$HOST_ADDRESS:$USER_SERVICE_PORT/api/drivers \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq


echo "=== Statistics without date ==="
curl -s -X GET "http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/statistics" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq
echo

echo "=== Statistics with date 2026-05-11 ==="
curl -s -X GET "http://$HOST_ADDRESS:$TRIP_SERVICE_PORT/api/trips/statistics?date=2026-05-11" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" | jq