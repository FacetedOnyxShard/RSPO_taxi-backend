HOST_LOCALHOST_ADDRESS=$(ip route show default | awk '{print $3}')

PASSENGER_ID=$(curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8001/api/passengers \
  -H "Content-Type: application/json" \
  -d '{"name":"Ivan","email":"ivan@example.com","phone":"+79161234567"}' | jq -r '.id')

DRIVER_ID=$(curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8001/api/drivers \
  -H "Content-Type: application/json" \
  -d '{"name":"Petr","email":"petr@example.com","phone":"+79169876543","licenseNumber":"LIC12345","status":"FREE"}' | jq -r '.id')


curl -s http://$HOST_LOCALHOST_ADDRESS:8001/api/passengers/$PASSENGER_ID | jq
curl -s http://$HOST_LOCALHOST_ADDRESS:8001/api/drivers/$DRIVER_ID | jq

curl -s http://$HOST_LOCALHOST_ADDRESS:8001/api/drivers | jq
curl -s http://$HOST_LOCALHOST_ADDRESS:8001/api/passengers | jq

curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8002/api/trips \
  -H "Content-Type: application/json" \
  -d "{
    \"passenger_id\": \"$PASSENGER_ID\",
    \"origin\": \"ул. КПП, 1\",
    \"destination\": \"ул. Красная площадь, 10\"
  }" | jq

echo "Id водителя для следующих тестов:"
echo $DRIVER_ID

echo "Id пассажира для следующих тестов:"
echo $PASSENGER_ID