This application is the test task for the project interview. It's written in Java17, SpringBoot 3 and packaged in Docker container.

**To run application locally** (use java 17 or higher):
1) Go to _application-local.yml_ and change the path to the csv/prices folder respectively to your project directory
2) _./gradlew clean build_ 
3) Run application from IDE with spring local profile e.g. by using "-Dspring.profiles.active=local" JVM parameter 

**To run application in docker container:**
1) _./gradlew clean build_
2) _docker-compose up --build -d_
3) no additional steps are required

**Endpoints:**
1) all available endpoints are described here: http://localhost:8080/swagger-ui/index.html and can be called directly from swagger-ui
2) please be informed that there exists rate limit for every endpoint (5 requests per minute)
3) have a good try :)

**To add new coins:**
1) just put new csv files into _csv/prices_ folder with the file name format (**COIN_values.csv**) 
2) they will be automatically picked up after the application restart

