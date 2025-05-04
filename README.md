# How to run this project
## 0. Set up the environment
Create a Google OAuth2 client ID and secret, then set up the environment variables in the `application.yml` file.
## 1. Run the docker container
Using this command to run all the necessary containers `docker-compose up -d`
## 2. Run the migrations
+ First, you need to change all the files in the migrations folder (which can be found in `src/resources/db/migrations`) from `.sql.bak` to `.sql`  
+ Then just run the application to auto migrate the database, if you can not do that, you can run the command manually, just copy, paste the sql commands from the files to the database and execute them one by one.  
+ In my case, I create a stop-words and dictionary in Vietnamese, if you do not need them, just ignore them in file `V4__create_index.sql.bak` 
## 3. Run the application
Just run the app, all the necessary documents can be found in `localhost:2999/swagger`

# Some tech-stacks in use
- Java 17
- Spring Boot 3.4.3
- Spring Data JPA
- Spring Security
- Postgres SQL
- Minio
- Websocket
- Local Cache
- Event Bus
- Google OAuth2