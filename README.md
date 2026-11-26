# 🏠 Smart Home Automation Simulator

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![MySQL](https://img.shields.io/badge/Database-MySQL%208-00758F?logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/Connector-JDBC-green)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

A desktop application built with **Java Swing** and **MySQL (JDBC)** that simulates a smart home. Users can control lights, fans, air conditioners and security devices through a graphical interface and schedule multiple device activities.

---

## 👨‍🎓 Developer Details

| | |
|---|---|
| **Name** | `[YOUR FULL NAME]` |
| **College** | `[YOUR COLLEGE NAME]` |
| **Department** | `[YOUR DEPARTMENT]` |
| **Year** | II Year (2nd Year) |
| **Project Type** | Object-Oriented Programming Mini Project |

---

## 📌 Problem Statement

Develop an application to control lights, fans, air conditioners and security devices. Users can trigger device operations through GUI events and schedule multiple device activities.

---

## ✨ Features

- 💡 Control **lights, fans, air conditioners and security devices** from one dashboard
- 🔘 **Turn devices ON / OFF** with a single click
- ⚙️ **Change device settings**
  - Light: brightness
  - Fan: speed
  - AC: temperature
  - Security: armed / disarmed
- ➕ **Add and delete devices** at runtime
- ⏰ **Schedule multiple device activities**
- 📝 **Activity log** that records every operation in the database
- 🎨 **Colorful full-screen interface** with color-coded device status
  - ON rows in green, ARMED rows in red, DISARMED rows in yellow
- 💾 **Persistent storage** using MySQL, so data stays after the app is closed

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Core application logic (OOP) |
| Java Swing | Graphical user interface |
| MySQL 8 | Database for devices, schedules and logs |
| JDBC (Connector/J) | Connecting Java to MySQL |
| VS Code | Development environment |
| Git & GitHub | Version control |

---

## 🧱 OOP Concepts Used

- **Classes and Objects**: each device is represented as an object
- **Encapsulation**: device data is kept private and accessed through methods
- **Abstraction**: common device behaviour is defined once and reused
- **Inheritance and Polymorphism**: different device types share behaviour and respond to the same actions in their own way
- **Event Handling**: GUI buttons trigger device operations through listeners

---

## 🗄️ Database Design

The database `smarthome` contains three tables:

| Table | Description |
|---|---|
| `devices` | Stores each device, its type, room, status and settings |
| `schedules` | Stores scheduled activities for devices |
| `activity_log` | Stores a history of every action performed |

---

## 📁 Project Structure

```
SmartHome/
├── src/
│   └── smarthome/
│       ├── Main.java        # Main application and GUI logic
│       ├── Theme.java       # Colors, fonts and styling
│       └── Launcher.java    # Starts the app with the full-screen theme
├── lib/
│   └── mysql-connector-j-26.10.0.jar
├── schema.sql               # Database creation script
├── db.properties.example    # Sample database configuration
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Java JDK 17 or later
- MySQL Server 8.x (with MySQL Workbench, optional)
- Git

### 1. Clone the repository

```bash
git clone https://github.com/priyanr15/Smart-Home.git
cd Smart-Home
```

### 2. Create the database

Make sure the MySQL service is running, then run:

```bash
mysql -u root -p -e "source schema.sql"
```

Or open `schema.sql` in MySQL Workbench and click the lightning-bolt icon.

### 3. Configure the database connection

Copy the example file and enter your MySQL root password:

```bash
copy db.properties.example db.properties
```

Open `db.properties` and set:

```properties
password=your_password_here
```

### 4. Compile and run

```bash
mkdir out
javac -cp "lib/*" -d out src\smarthome\*.java
java -cp "out;lib/*" smarthome.Launcher
```

> On Linux or macOS, use `:` instead of `;` in the classpath and `/` instead of `\`.

---

## 🧭 How to Use

1. Launch the app. It opens in full screen with sample devices.
2. Select a device in the table and click **Turn ON / OFF**.
3. Click **Change Setting** to adjust brightness, speed, temperature or security mode.
4. Use **Add Device** to create a new device, or **Delete** to remove one.
5. Open the schedule tab to plan device activities.
6. Check the activity tab to see the history of all actions.

---

## 📸 Screenshots

> Add your screenshots to a `screenshots/` folder and link them here.

```markdown
![Dashboard](screenshots/dashboard.png)
![Schedule](screenshots/schedule.png)
```

---

## 🔮 Future Enhancements

- Voice control and mobile app support
- Real-time energy usage statistics
- User login with different roles
- Integration with real IoT hardware

---

## 🔒 Note on Security

`db.properties` contains your database password and is excluded from the repository through `.gitignore`. Never upload real passwords to GitHub.

---

## 📬 Contact

**`[YOUR FULL NAME]`**
II Year, `[YOUR DEPARTMENT]`
`[YOUR COLLEGE NAME]`

GitHub: [@priyanr15](https://github.com/priyanr15)

---

⭐ If you found this project useful, consider giving it a star.
