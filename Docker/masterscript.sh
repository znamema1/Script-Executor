#!/bin/bash

git clone "$1" repo
if [ "$?" -ne 0 ]; then
	exit 1
fi

cd repo
git checkout "$2"
if [ "$?" -ne 0 ]; then
	exit 2
fi
cd ..

chmod 777 ./repo/"$3"
if [ "$?" -ne 0 ]; then
	exit 3
fi

curl -s http://10.0.2.15:8080/Executor-1.0/docker/getinput/"$4" > data
if [ "$?" -ne 0 ]; then
	exit 4
fi

cat data | ./repo/"$3" > out
if [ "$?" -ne 0 ]; then
	exit 5
fi

curl -sX POST http://10.0.2.15:8080/Executor-1.0/docker/postoutput/"$4" -d "$(cat out)" --header "Content-Type:text/plain"
if [ "$?" -ne 0 ]; then
	exit 6
fi