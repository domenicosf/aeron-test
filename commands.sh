#!/bin/sh

java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.LowLatencyMediaDriver &> lowlatencymedia.log &
java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.BasicPublisher &> basicpub.log &
java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.BasicSubscriber &> basicsub.log &

while true ;do wait ;done