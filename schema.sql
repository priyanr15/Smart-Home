CREATE DATABASE IF NOT EXISTS smarthome;
USE smarthome;

CREATE TABLE IF NOT EXISTS devices (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(60) NOT NULL,
  type ENUM('LIGHT','FAN','AC','SECURITY') NOT NULL,
  room VARCHAR(40) NOT NULL,
  is_on BOOLEAN NOT NULL DEFAULT FALSE,
  level INT NOT NULL DEFAULT 0   -- light: brightness 0-100, fan: speed 1-5, AC: temp 16-30
);

CREATE TABLE IF NOT EXISTS schedules (
  id INT AUTO_INCREMENT PRIMARY KEY,
  device_id INT NOT NULL,
  action ENUM('ON','OFF') NOT NULL,
  run_time TIME NOT NULL,
  repeat_daily BOOLEAN NOT NULL DEFAULT FALSE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_run DATE NULL,
  FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS activity_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  device_id INT NULL,
  message VARCHAR(200) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO devices (name,type,room,is_on,level) VALUES
 ('Ceiling Light','LIGHT','Living Room',FALSE,80),
 ('Bedside Lamp','LIGHT','Bedroom',FALSE,50),
 ('Ceiling Fan','FAN','Living Room',FALSE,3),
 ('Split AC','AC','Bedroom',FALSE,24),
 ('Front Door Alarm','SECURITY','Entrance',FALSE,0);
