To run application locally:
1) Go to application-local.yml and change the path to the csv/prices folder respectively to your project directory
2) Run application with spring local profile e.g. by using "-Dspring.profiles.active=local" JVM parameter 

To run application in docker container:
1) docker-compose up --build -d
2) no additional steps are required