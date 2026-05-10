# с этим тестом что-то не так, дальше тесты не прогонял
PASSENGER_ID=$1
HOST_LOCALHOST_ADDRESS=$(ip route show default | awk '{print $3}')

TRIP_ID=$(curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8002/api/trips \
  -H "Content-Type: application/json" \
  -d "{
    \"passenger_id\": \"$PASSENGER_ID\",
    \"origin\": \"Москва-Сити\",
    \"destination\": \"Парк Горького\"
  }" | jq -r '.id')


# водитель принял
curl -s -X PATCH http://$HOST_LOCALHOST_ADDRESS:8002/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"ACCEPTED"}' | jq

# начал движение
curl -s -X PATCH http://$HOST_LOCALHOST_ADDRESS:8002/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"STARTED"}' | jq

# завершил
curl -s -X PATCH http://$HOST_LOCALHOST_ADDRESS:8002/api/trips/$TRIP_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"COMPLETED"}' | jq


echo "После поездки статус водителя должен быть FREE:"
curl -s http://$HOST_LOCALHOST_ADDRESS:8001/api/drivers/$DRIVER_ID | jq '.status'