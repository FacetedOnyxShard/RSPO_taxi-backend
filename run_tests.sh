#!/usr/bin/env bash
set -euo pipefail

# ============== Переменные ==============
HOST="${HOST:-localhost}"
USER_PORT="${USER_PORT:-8001}"
TRIP_PORT="${TRIP_PORT:-8002}"
WORKER_PORT="${WORKER_PORT:-8003}"
source .env
PASSENGER_PASSWORD=$(echo -n "$PASSENGER_PASSWORD" | tr -d '\r\n"' | sed 's/\\//g')
DRIVER_PASSWORD=$(echo -n "$DRIVER_PASSWORD" | tr -d '\r\n"' | sed 's/\\//g')

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

ERRORS=0
PASSED=0

# =============== Функции сравнения =================
assert_status() {
  local test_name="$1"
  local expected="$2"
  local actual="$3"
  if [ "$expected" -eq "$actual" ]; then
    echo -e "${GREEN}PASS${NC} $test_name"
    PASSED=$((PASSED+1))
  else
    echo -e "${RED}FAIL${NC} $test_name (ожидался статус $expected, получен $actual)"
    ERRORS=$((ERRORS+1))
  fi
}

assert_json() {
  local test_name="$1"
  local json="$2"
  local jq_filter="$3"
  local expected="$4"
  local actual
  actual=$(echo "$json" | jq -r "$jq_filter")
  if [ "$actual" = "$expected" ]; then
    echo -e "${GREEN}PASS${NC} $test_name"
    PASSED=$((PASSED+1))
  else
    echo -e "${RED}FAIL${NC} $test_name (ожидалось '$expected', получено '$actual')"
    ERRORS=$((ERRORS+1))
  fi
}

# ============ Подготовка данных =============
echo "=== Регистрация пассажира ==="
PASSENGER_RESP=$(curl -s -w "\n%{http_code}" -X POST "http://$HOST:$USER_PORT/api/passengers" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Passenger","email":"testp@example.com","phone":"+79160000001","password":"'"$PASSENGER_PASSWORD"'"}')
PASSENGER_BODY=$(echo "$PASSENGER_RESP" | head -n -1)
PASSENGER_STATUS=$(echo "$PASSENGER_RESP" | tail -n1)
echo "  статус: $PASSENGER_STATUS"
PASSENGER_ID=$(echo "$PASSENGER_BODY" | jq -r '.id')

echo "=== Аутентификация пассажира ==="
PASSENGER_TOKEN=$(curl -s -X POST "http://$HOST:$USER_PORT/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"testp@example.com","password":"'"$PASSENGER_PASSWORD"'","role":"passenger"}' | jq -r '.token')
echo "  токен: $PASSENGER_TOKEN"

echo "=== Регистрация водителя ==="
DRIVER_RESP=$(curl -s -w "\n%{http_code}" -X POST "http://$HOST:$USER_PORT/api/drivers" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Driver","email":"testd@example.com","phone":"+79160000002","licenseNumber":"99TEST123","status":"FREE","password":"'"$DRIVER_PASSWORD"'"}')
DRIVER_BODY=$(echo "$DRIVER_RESP" | head -n -1)
DRIVER_STATUS=$(echo "$DRIVER_RESP" | tail -n1)
echo "  статус: $DRIVER_STATUS"
DRIVER_ID=$(echo "$DRIVER_BODY" | jq -r '.id')

echo "=== Аутентификация водителя ==="
DRIVER_TOKEN=$(curl -s -X POST "http://$HOST:$USER_PORT/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"testd@example.com","password":"'"$DRIVER_PASSWORD"'","role":"driver"}' | jq -r '.token')
echo "  токен: $DRIVER_TOKEN"

# ===================== Тесты =====================
echo ""
echo "=== НАЧАЛО ТЕСТОВ ==="

echo "--- Тестирование аутентификации ---"
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET "http://$HOST:$USER_PORT/api/passengers")
assert_status "Без токена -> 401" 401 "$STATUS"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET "http://$HOST:$USER_PORT/api/passengers" \
  -H "Authorization: Bearer invalidtoken")
assert_status "Некорректный токен -> 401" 401 "$STATUS"

echo "--- Дублирование при регистрации ---"
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$USER_PORT/api/passengers" \
  -H "Content-Type: application/json" \
  -d '{"name":"X","email":"testp@example.com","phone":"+79160000099","password":"pass"}')
assert_status "Одинаковый email passenger -> 400" 400 "$STATUS"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$USER_PORT/api/drivers" \
  -H "Content-Type: application/json" \
  -d '{"name":"X","email":"testd@example.com","phone":"+79160000099","licenseNumber":"NEWLICENSE","status":"FREE","password":"pass"}')
assert_status "Одинаковый email driver -> 400" 400 "$STATUS"

echo "--- Создание поездки ---"
TRIP_RESP=$(curl -s -w "\n%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"passenger_id":"'"$PASSENGER_ID"'","origin":"A","destination":"B","distance":10}')
TRIP_BODY=$(echo "$TRIP_RESP" | head -n -1)
TRIP_STATUS=$(echo "$TRIP_RESP" | tail -n1)
TRIP_ID=$(echo "$TRIP_BODY" | jq -r '.id')
assert_status "Создание поездки -> 201" 201 "$TRIP_STATUS"
assert_json "Статус CREATED" "$TRIP_BODY" '.status' "CREATED"
assert_json "Цена = 500 (10*50)" "$TRIP_BODY" '.price' "500"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"passenger_id":"00000000-0000-0000-0000-000000000001","origin":"A","destination":"B","distance":1}')
assert_status "Несуществующий пассажир -> 500" 500 "$STATUS"

echo "--- Получение поездки ---"
GET_RESP=$(curl -s "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
assert_json "Точка отправления A" "$GET_RESP" '.origin' "A"

echo "--- Список поездок пассажира ---"
LIST_RESP=$(curl -s "http://$HOST:$TRIP_PORT/api/trips?passenger_id=$PASSENGER_ID" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
TRIP_COUNT=$(echo "$LIST_RESP" | jq '. | length')
assert_status "Длина списка >= 1" 1 "$( [ "$TRIP_COUNT" -ge 1 ] && echo 1 || echo 0 )"

echo "--- Нет свободных водителей ---"
sleep 1
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"passenger_id":"'"$PASSENGER_ID"'","origin":"C","destination":"D","distance":1}')
assert_status "Нет свободных водителей -> 503" 503 "$STATUS"

echo "--- Смена статусов поездки ---"
STATUS_RESP=$(curl -s -w "\n%{http_code}" -X PATCH "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status":"ACCEPTED"}')
STATUS_BODY=$(echo "$STATUS_RESP" | head -n -1)
HTTP_STATUS=$(echo "$STATUS_RESP" | tail -n1)
assert_status "Принятие поездки -> 200" 200 "$HTTP_STATUS"
assert_json "Статус ACCEPTED" "$STATUS_BODY" '.status' "ACCEPTED"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status":"STARTED"}')
assert_status "Начало поездки -> 200" 200 "$STATUS"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status":"COMPLETED"}')
assert_status "Завершение поездки -> 200" 200 "$STATUS"

echo "--- Тесты рейтинга ---"
RATE_RESP=$(curl -s -w "\n%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/rate" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"rating":5}')
RATE_STATUS=$(echo "$RATE_RESP" | tail -n1)
assert_status "Оценка завершённой поездки -> 200" 200 "$RATE_STATUS"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/rate" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"rating":0}')
assert_status "Рейтинг 0 -> 400" 400 "$STATUS"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips/$TRIP_ID/rate" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"rating":6}')
assert_status "Рейтинг 6 -> 400" 400 "$STATUS"

echo "--- Оценка незавершённой поездки ---"
curl -s -o /dev/null -X PATCH "http://$HOST:$USER_PORT/api/drivers/$DRIVER_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status":"FREE"}'

TRIP2_RESP=$(curl -s -X POST "http://$HOST:$TRIP_PORT/api/trips" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"passenger_id":"'"$PASSENGER_ID"'","origin":"E","destination":"F","distance":2}')
TRIP2_ID=$(echo "$TRIP2_RESP" | jq -r '.id')

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$TRIP_PORT/api/trips/$TRIP2_ID/rate" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PASSENGER_TOKEN" \
  -d '{"rating":3}')
assert_status "Оценка незавершённой -> 409" 409 "$STATUS"

echo "--- Статистика ---"
STATS_RESP=$(curl -s "http://$HOST:$TRIP_PORT/api/trips/statistics" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
TOTAL=$(echo "$STATS_RESP" | jq '.totalTrips')
AVG=$(echo "$STATS_RESP" | jq '.averagePrice')
if [ "$TOTAL" -ge 1 ] && (( $(echo "$AVG > 0" | bc -l) )); then
    echo -e "${GREEN}PASS${NC} Статистика содержит поездки и среднюю цену > 0"
    PASSED=$((PASSED+1))
else
    echo -e "${RED}FAIL${NC} Статистика некорректна (поездок=$TOTAL, средняя=$AVG)"
    ERRORS=$((ERRORS+1))
fi

echo "--- Уведомления ---"
NOTIF_RESP=$(curl -s "http://$HOST:$WORKER_PORT/notifications?trip_id=$TRIP_ID" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
NOTIF_COUNT=$(echo "$NOTIF_RESP" | jq '. | length')
if [ "$NOTIF_COUNT" -ge 1 ]; then
    echo -e "${GREEN}PASS${NC} Уведомления для поездки существуют ($NOTIF_COUNT шт.)"
    PASSED=$((PASSED+1))
else
    echo -e "${RED}FAIL${NC} Уведомления отсутствуют"
    ERRORS=$((ERRORS+1))
fi

echo "--- Назначение водителя ---"
curl -s -o /dev/null -X PATCH "http://$HOST:$USER_PORT/api/drivers/$DRIVER_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '{"status":"FREE"}'

ASSIGN_RESP=$(curl -s -w "\n%{http_code}" -X POST "http://$HOST:$USER_PORT/api/drivers/assign" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
ASSIGN_STATUS=$(echo "$ASSIGN_RESP" | tail -n1)
ASSIGN_BODY=$(echo "$ASSIGN_RESP" | head -n -1)
assert_status "Назначение свободного водителя -> 200" 200 "$ASSIGN_STATUS"
assert_json "ID водителя совпадает" "$ASSIGN_BODY" '.id' "$DRIVER_ID"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://$HOST:$USER_PORT/api/drivers/assign" \
  -H "Authorization: Bearer $PASSENGER_TOKEN")
assert_status "Нет свободных водителей -> 400" 400 "$STATUS"



echo ""
echo "================================="
echo -e "Тестов пройдено: ${GREEN}$PASSED${NC}"
echo -e "Тестов не пройдено: ${RED}$ERRORS${NC}"
echo "================================="
if [ "$ERRORS" -gt 0 ]; then
    exit 1
fi