# THIS CODE WAS PART OF AN TECHNICAL INTERVIEW PROCESS

# Service Monitor

### Description
This is a maven project that builds status monitor application

### Test
To run the test  
`mvn clean test`  

## Build
To build the application  
`mvn clean package`  


### Run the application
When the application is built. The runnable jar can be found in the target folder and has the ending `-fat.jar`  
To run the application, type:
`java -jar target/vertx-1.0.0-SNAPSHOT-fat.jar -conf /src/main/conf/MainVerticle-conf.json`

The application starts a server on `localhost:8088` using the configuration file. If you skip the configuration file, the server runs on `localhost:8080`.  

##### Disclaimer
This project was done in chunks during two week and a maximum coding time of 10 hours. So the front-end and back-end is not as optimal as it could be.
