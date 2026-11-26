# Smart Home Automation Simulator (Java Swing + MySQL)

Control lights, fans, ACs and security devices from a GUI, and schedule ON/OFF actions.

## Setup
1. Install JDK 11+ and MySQL.
2. Run the schema:   `mysql -u root -p < schema.sql`
3. Edit `db.properties` with your MySQL password.
4. Download MySQL Connector/J (mysql-connector-j-x.x.x.jar) from https://dev.mysql.com/downloads/connector/j/
   and put it in the `lib/` folder.

## Build & run
Linux/Mac:
    mkdir -p out && javac -d out src/smarthome/*.java
    java -cp "out:lib/*" smarthome.Main

Windows:
    mkdir out
    javac -d out src\smarthome\*.java
    java -cp "out;lib/*" smarthome.Main

## Features
- Devices tab: toggle ON/OFF (security = arm/disarm), change brightness / fan speed / AC temp, add/delete devices
- Schedules tab: schedule many device actions, one-time or daily; background timer runs them
- Activity Log tab: every manual and scheduled action is stored in MySQL
