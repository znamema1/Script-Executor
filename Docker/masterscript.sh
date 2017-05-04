#!/bin/bash

echo "$1"
echo "$2"
echo "$3"
echo "$4"

echo cloning
git clone "$1" repo
if [ "$?" -ne 0 ]; then
	echo "Clone se podelal" >&2 
	exit 1
fi

cd repo
echo checkout
git checkout "$2"
if [ "$?" -ne 0 ]; then
	echo "Checkout se podelal" >&2
	exit 2
fi
cd ..

echo chmod
chmod 777 ./repo/"$3"
if [ "$?" -ne 0 ]; then
	echo "Chmod se podelal" >&2
	exit 3
fi

echo "curl"
curl -s http://10.0.2.15:8080/Executor-1.0/docker/getinput/"$4" > data
if [ "$?" -ne 0 ]; then
	echo "Curl se podelal" >&2
	exit 4
fi

echo run

cat data | ./repo/"$3" > out
if [ "$?" -ne 0 ]; then
	echo "Script se podelal" >&2
	exit 5
fi

echo "datain:"
cat data
echo "dataout:"
cat out

echo "send data"

curl -sX POST http://10.0.2.15:8080/Executor-1.0/docker/postoutput/"$4" -d "$(cat out)" --header "Content-Type:text/plain"
if [ "$?" -ne 0 ]; then
	echo "Curl POST se podelal" >&2
	exit 6
fi