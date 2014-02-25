ProcessTutorial
===============

SE3800 Final Project

Requirements
------------
Redis (clone the git repository and 'nohup /<path>/<to>/<redis>/src/redis-server &')
Postgres (version 9.3 was used for the project although earlier version will likely work)
Java 7

Instructions (for Linux/Mac)
----------------------------
* Clone this repository
* Make sure redis is running
* Make sure postgres is running (a schema named 'se3800' and a user named 'se3800' and password 'v738T4*2anoco82r3' is required)
* Open terminal and navigate the to root of the project
* Run './gradlew fatJar' which compiles the project into one big jar file
* Run './gradlew migrate' which will setup your database tables
* Run './gradlew testData' which will insert a base admin user with username 'admin' and password 'test123'
* Run 'java -jar build/libs/ProcessTutorial.jar server ProcessTutorial.yml' to start running the project. This will run the project until you hit 'ctrl+c' to quit the project.
* If you want to leave the project running and not worry about it until later run 'nohup java -jar build/libs/ProcessTutorial.jar server ProcessTutorial.yml &' which will disown the process and leave it running until the server shuts down.
* If you choose to leave the project running in the background, you need to find out the process ID and kill it, do a 'sudo killall java' which may kill other processes, or do a POST to the server 'http://server.address:8080/shutdown' with a JSON body as follows
    
    {
        "password":"ptShutdown"
    }
    

Notes
------
* The server runs on port 8080, so you can either modify you IP tables to forward all traffic on port 80 of your machine to port 8080 so it will reach the server, or just modify your base urls in the web project to go to port 8080, which is the default for the web frontend.
* Most server configurations are in the ProcessTutorial.yml file. Open this in a text editor to see the port the server runs on, the database user and password, etc.
* If you want to see if the server is running, you can do a get to the health check route, 'curl http://localhost:8080/healthcheck' and it should respond with a 'status OK'.
