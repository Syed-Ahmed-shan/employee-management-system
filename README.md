Employee Management System

A full-stack, role-based web application for managing employees and tasks. It has separate access levels for Admin, Manager, and Employee, secure JWT authentication, task assignment with status tracking, and role-specific dashboards.

Tech stack: Java · Spring Boot · React.js · JDBC · MySQL · SQL · JWT · REST APIs

Table of Contents
Features
Roles and Permissions
Tech Stack
Architecture
Getting Started
Configuration
API Overview
Project Structure
Security
Screenshots
Future Improvements
Author
Features
Role-based access control with three roles: Admin, Manager, Employee.
Secure authentication and authorization using JWT (JSON Web Tokens).
Employee management (CRUD): create, view, update, and delete employee records.
Task assignment module: managers assign tasks to employees.
Timeline-based status tracking: every task moves through defined states, and each change is recorded.
History logging: a full log of task state transitions for accountability.
Role-specific dashboards: each role sees only the data and actions relevant to it.
REST API backend connected to a React frontend.
Roles and Permissions
Capability	Admin	Manager	Employee
Manage employee records (CRUD)	Yes	Limited	No
Assign tasks	Yes	Yes	No
Update status of own tasks	Yes	Yes	Yes
View task history and timeline	Yes	Yes	Own only
Access role-specific dashboard	Yes	Yes	Yes

Adjust this table to match exactly what your code allows.

Tech Stack
Layer	Technology
Backend	Java, Spring Boot, REST APIs
Data access	JDBC
Database	MySQL (SQL)
Security	JWT-based authentication and RBAC
Frontend	React.js, HTML, CSS, JavaScript
Architecture
React Frontend  <--REST/JSON + JWT-->  Spring Boot Backend  <--JDBC-->  MySQL Database
The user logs in from the React app.
The Spring Boot backend validates credentials and returns a signed JWT.
The frontend sends the JWT with every request; the backend checks the token and the user's role before allowing access.
Data is read and written to MySQL through JDBC.
Getting Started
Prerequisites
Java JDK 17 or later (use the version your project is built with)
Maven (or Gradle, if you use it)
Node.js and npm
MySQL Server
1. Clone the repository
bash
git clone https://github.com/Syed-Ahmed-shan/employee-management-system.git
cd employee-management-system
2. Set up the database
sql
CREATE DATABASE employee_management;

Then run the SQL script from the project (for example schema.sql) to create the tables. Update the file name if yours is different.

3. Configure the backend

Edit the backend configuration file (usually src/main/resources/application.properties):

properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_management
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
jwt.secret=YOUR_LONG_RANDOM_SECRET

Never commit real passwords or secrets to GitHub. Keep them in environment variables or a local file that is listed in .gitignore.

4. Run the backend
bash
mvn spring-boot:run

The server starts on http://localhost:8080 by default.

5. Run the frontend
bash
cd frontend
npm install
npm start

The React app opens on http://localhost:3000 by default.

Change the folder names and ports above if your project uses different ones.

Configuration
Setting	Purpose
spring.datasource.url	MySQL connection URL
spring.datasource.username	Database user
spring.datasource.password	Database password
jwt.secret	Secret key used to sign JWT tokens
API Overview

Replace these example routes with the real ones from your controllers.

Method	Endpoint	Description	Access
POST	/api/auth/login	Log in and receive a JWT	Public
GET	/api/employees	List employees	Admin, Manager
POST	/api/employees	Create an employee	Admin
PUT	/api/employees/{id}	Update an employee	Admin
DELETE	/api/employees/{id}	Delete an employee	Admin
POST	/api/tasks	Assign a task	Admin, Manager
PUT	/api/tasks/{id}/status	Update task status	Assigned employee
GET	/api/tasks/{id}/history	View task status history	Admin, Manager

Send the token with protected requests:

Authorization: Bearer <your_jwt_token>
Project Structure
employee-management-system/
├── backend/            # Spring Boot application (controllers, services, JDBC data access)
├── frontend/           # React application (components, pages, dashboards)
├── database/           # SQL scripts
└── README.md

Update this tree to match your actual folders.

Security
Passwords should be stored hashed, never as plain text.
JWT tokens are verified on every protected request.
Role checks are enforced on the server, not only in the UI.
Secrets and credentials are kept out of the repository.
Screenshots


Future Improvements
Email notifications for task assignment and status changes
Pagination, search, and filters for employee and task lists
Unit and integration tests for services and APIs
Docker support for one-command setup
Deployment with a CI/CD pipeline
Author

Syed Ahmed Nawaz

GitHub: Syed-Ahmed-shan
LinkedIn: Syed Ahmed Nawaz
Email: ahmedsyed200326@gmail.com
