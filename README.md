# Cloud App Project
This Project is built using Java, Spring Boot and MySQL.

## Environment details
For building the application you would need the following:
JDK 17
maven 3.8.6
mysql 8.3.0

## To Run
Clone the Repo in local
You can build the project, run the tests by running mvn clean package
Once successfully built, you can run the project in any IDE of choice

## Github Actions
Added branch protection by preventing merge if any workflow fails.
Added unit test cases to the workflow to make sure.

## API Endpoints

/healthz : to check the health
/v1/user : no need to authenticate and creates a user with details given in JSON
/v1/user/self : basic token authentication needed, update & get is present
