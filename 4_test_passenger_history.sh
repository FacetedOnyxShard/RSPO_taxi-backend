HOST_LOCALHOST_ADDRESS=$(ip route show default | awk '{print $3}')

curl -s "http://$HOST_LOCALHOST_ADDRESS:8002/api/trips?passenger_id=$PASSENGER_ID" | jq