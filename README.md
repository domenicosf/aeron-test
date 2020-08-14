#Aeron Example Project

This project will present a basic publisher, basic subscriber and a low latency media driver.

All the classes code are extracted from [Aeron project](https://github.com/real-logic/aeron)

Here we containerized the application and write a step by step tutorial of how run the examples.

The steps are:

* Run gradlew build for unix system's or gradlew.bat build for windows;
* After that you can choose run using docker or in the host machine:
    * To run using docker you need to execute the following commands: 
        * docker build -t aeron-test:1.0 .
        * docker run -v "c:\\logs:/opt/aeron" --shm-size 512M -it aeron-test:1.0
    * To run using host execute the following (based on linux operating system):
        * java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.LowLatencyMediaDriver &> lowlatencymedia.log &
        * java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.BasicPublisher &> basicpub.log &
        * java -cp ./build/libs/aeron-test-1.0-SNAPSHOT.jar br.com.domenicosf.aeron.BasicSubscriber &> basicsub.log &

When running the commands in the host machine the order of the commands are important because the media driver must start at first, after that the aeron inside the BasicPublisher and BasicSubscriber classes localize the temporary file created by the media driver avoiding the usage of embedded media driver.