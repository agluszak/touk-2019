#!/bin/bash

ask() {
  echo
  sleep 2s
  echo -ne "\e[91m- "
  echo "$1"
  sleep 1s
  echo -ne "\e[0m"
}

get() {
  curl -s -X GET "$1" | jq
}

post() {
  curl -s -X POST \
  "$1" \
  -H 'content-type: application/json' \
  -d "$2" | jq
}

echo -e "\e[1mONCE UPON A TIME IN CINEMA (A DRAMA IN 1 ACT)\e[0m"
ask "Hi, what movies can I watch on September 13th, 2119 between 10AM and 2PM?"
get 'http://localhost:8080/screenings?from=2119-09-13T10%3A00&to=2119-09-13T14%3A00'
ask "Okay, I'll go with the movie about John Paul II"
get 'http://localhost:8080/screenings/3'
ask "Okay, so one ticket for me and one for my child please. I would like to sit somewhere in the middle"
post 'http://localhost:8080/reservations' \
'{
	"screeningId": 3,
	"name": "Mieszysława",
	"surname": "Grejpfrut-Kowalska-Startrek",
	"seats": [55, 56],
	"tickets": [{"ticketType": 1, "quantity": 1}, {"ticketType": 3, "quantity" : 1}]
}'
ask "Oh, you don't allow having a triple surname? That's weird..."
post  'http://localhost:8080/reservations' \
'{
	"screeningId": 3,
	"name": "Mieszysława",
	"surname": "Grejpfrut-Kowalska",
	"seats": [55, 56],
	"tickets": [{"ticketType": 1, "quantity": 1}, {"ticketType": 3, "quantity" : 1}]
}'
ask "What do you mean 'already reserved'? By whom?!"
get 'http://localhost:8080/reservations/1'
ask "Thank God that in 2119 GDPR is no longer in power... Jan Kowalski? That's my ex-husband, probably with his new lover! I don't want to sit anywhere near them!"
post 'http://localhost:8080/reservations' \
'{
	"screeningId": 3,
	"name": "Mieszysława",
	"surname": "Grejpfrut-Kowalska",
	"seats": [53, 52],
	"tickets": [{"ticketType": 1, "quantity": 1}, {"ticketType": 3, "quantity" : 1}]
}'
ask "What? What the heck is an 'orphaned seat'?! That monster half-orphaned my child! Let's check another row..."
post 'http://localhost:8080/reservations' \
'{
	"screeningId": 3,
	"name": "Mieszysława",
	"surname": "Grejpfrut-Kowalska",
	"seats": [43, 42],
	"tickets": [{"ticketType": 1, "quantity": 1}, {"ticketType": 3, "quantity" : 1}]
}'
ask "Finally! There you go, keep the change..."
sleep 3s
echo -e "\e[4m...Oh hi doggy!\e[0m"
