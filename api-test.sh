#!/usr/bin/env bash
B=http://localhost:8080
J='Content-Type: application/json'
C=$(docker ps -qf ancestor=postgres:16)
Q() { docker exec -i "$C" psql -U freshcart -d freshcart -tA "$@"; }

Q -q < order-service/src/main/resources/data.sql
T=$(curl -s -H "$J" -d '{"email":"customer1@freshcart.test","password":"1234567890"}' $B/auth/login)

order() { curl -s -o /dev/null -w '%{http_code}' -H "$J" -H "Authorization: Bearer $T" -d "$1" $B/order; }
item() { echo "{\"quantity\":$1,\"product\":$2}"; }
check() { echo "$1 | expected $2 | got $3"; }

check "1 normal order" 201 "$(order "{\"slot\":1,\"orderItems\":[$(item 2 1)]}")"
check "1 milk stock" 998 "$(Q -c 'select stock from product where id=1')"
check "2 more than stock" 409 "$(order "{\"slot\":1,\"orderItems\":[$(item 3 3)]}")"
check "3 second item fails" 409 "$(order "{\"slot\":1,\"orderItems\":[$(item 1 2),$(item 3 3)]}")"
check "3 bread unchanged" 1000 "$(Q -c 'select stock from product where id=2')"
check "4 slot full" 409 "$(order "{\"slot\":2,\"orderItems\":[$(item 1 1)]}")"
check "5 slot started" 400 "$(order "{\"slot\":3,\"orderItems\":[$(item 1 1)]}")"
check "6 duplicate product" 400 "$(order "{\"slot\":1,\"orderItems\":[$(item 1 1),$(item 1 1)]}")"
check "7 cancel" 200 "$(curl -s -o /dev/null -w '%{http_code}' -X POST -H "Authorization: Bearer $T" $B/order/2/cancel)"
check "7 milk back" 1000 "$(Q -c 'select stock from product where id=1')"

echo "8 ten orders at once, slot max 5:"
for i in 1 2 3 4 5 6 7 8 9 10; do
  order "{\"slot\":4,\"orderItems\":[$(item 1 1)]}" > /tmp/fc$i &
done
wait
cat /tmp/fc* | fold -w3 | sort | uniq -c
rm -f /tmp/fc*
check "8 orders in slot" "5 or fewer" "$(Q -c 'select count(*) from orders where slot_id=4')"
check "8 milk stock matches" "1000 minus that count" "$(Q -c 'select stock from product where id=1')"
