DRIVER_ID=$1
HOST_LOCALHOST_ADDRESS=$(ip route show default | awk '{print $3}')

curl -s -X PATCH http://$HOST_LOCALHOST_ADDRESS:8001/api/drivers/$DRIVER_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"OFFLINE"}' | jq


curl -s -v -X POST http://$HOST_LOCALHOST_ADDRESS:8002/api/trips \
  -H "Content-Type: application/json" \
  -d "{
    \"passenger_id\": \"$PASSENGER_ID\",
    \"origin\": \"Кремль\",
    \"destination\": \"ВДНХ\"
  }" | jq
# Ответ: "No free drivers available at the moment", код 503

curl -s -X PATCH http://localhost:8001/api/drivers/$DRIVER_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"FREE"}' | jq > /dev/null