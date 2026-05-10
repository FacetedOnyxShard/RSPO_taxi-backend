HOST_LOCALHOST_ADDRESS=$(ip route show default | awk '{print $3}')


# Неверный формат passenger_id
curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8002/api/trips \
  -H "Content-Type: application/json" \
  -d '{"passenger_id":"123", "origin":"A", "destination":"B"}' | jq

# Пустое поле origin
curl -s -X POST http://$HOST_LOCALHOST_ADDRESS:8002/api/trips \
  -H "Content-Type: application/json" \
  -d '{"passenger_id":"'$PASSENGER_ID'", "origin":"", "destination":"B"}' | jq