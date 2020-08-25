# Description
Just a example for resilience4j using kotlin!

## Building
Execute the following command
```jshelllanguage
$ mvn clean package
```

## Running
Execute the following command
```jshelllanguage
$ java -jar target/ktor-resilience4j-example.jar
```

## Testing
 Get a greeting
 ```jshelllanguage
$ curl -X GET http://localhost:8080/greeting
```

Switch to get error or success
```jshelllanguage
curl -X GET http://localhost:8080/switch
```