E-commerce Multi-Vendor Health Store Notion Document
Project Overview
This project is a full-stack e-commerce platform for hair, body, supplement, and weight loss products, integrated with affiliate links. It supports multiple vendors, customers, and admins, with features like product management, cart, checkout, and analytics.
Tech Stack

Backend: Spring Boot, Java 17, Maven, Spring Security, JWT, Java Mail Sender
Frontend: React, TypeScript, Vite, Redux Toolkit, MUI, Tailwind CSS, Formik, Yup, React Router DOM, Axios
Database: MySQL
Payment Gateways: Razorpay (India), Stripe (International)
Tools: IntelliJ IDEA Ultimate, VS Code, Node.js, MySQL, Docker, Postman

Project Setup Instructions
Prerequisites

Java 17: Install JDK 17.
Node.js: Version 18.x or higher.
MySQL: Version 8.x.
Docker: For containerization.
IntelliJ IDEA Ultimate: For backend development.
VS Code: For frontend development.
Postman: For API testing.
Git: For version control.

Backend Setup

Clone the Repository:git clone https://github.com/sajidbaba1/Full-Stack-Sajid-Health-Store-.git
cd Full-Stack-Sajid-Health-Store-
git checkout -b main


Open in IntelliJ IDEA:
File > Open > Select backend-spring-boot folder.
Ensure JDK 17 is configured in Project Settings.


Configure MySQL:
Install MySQL and create a database named health_store.
Update application.properties with your MySQL credentials.


Configure APIs:
Obtain Razorpay and Stripe API keys and update application.properties.
Set up Gmail SMTP for Java Mail Sender (generate an app password).
Add Gemini API key for the chatbot.


Run the Application:
Right-click HealthStoreApplication.java > Run.
The backend runs on http://localhost:5454.



Frontend Setup

Open in VS Code:
Open the frontend-vite folder in VS Code.


Install Dependencies:npm install


Run the Development Server:npm run dev


The frontend runs on http://localhost:5173.


Configure API Endpoints:
Update src/Config/config.ts with the backend API URL (http://localhost:5454).



Database Setup

Create Database:CREATE DATABASE health_store;


Update application.properties:spring.datasource.url=jdbc:mysql://localhost:3306/health_store
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update



Docker Setup

Build Docker Image:docker build -t health-store-backend:latest ./backend-spring-boot
docker build -t health-store-frontend:latest ./frontend-vite


Run Containers:docker run -p 5454:5454 health-store-backend:latest
docker run -p 80:80 health-store-frontend:latest


Push to Docker Hub:docker tag health-store-backend:latest your_dockerhub_username/health-store-backend:latest
docker push your_dockerhub_username/health-store-backend:latest



Project Structure

Root:
assets/: Images and documentation.
backend spring-boot/: Spring Boot backend.
frontend-vite/: React frontend.


Backend:
src/main/java/com/healthstore/: Main package.
src/main/resources/: Configuration files.


Frontend:
src/: React components, Redux slices, and routes.
public/: Static assets.
assets/images/: Pexels images.



Features

Customer:
Chatbot for queries (order history, cart, product details).
Product browsing with filters, sorting, and pagination.
Cart management, checkout with coupons, and payment gateways.
Order history, reviews, and wishlist.


Seller:
Dashboard with earning graphs and reports.
Product and order management.
Transaction tracking.


Admin:
Dashboard, seller management, coupon management, home page customization, and deal management.


New Features:
Affiliate link tracking.
AI-driven product recommendations.
Multi-language support.
SEO optimization.



Development Workflow

Develop feature by feature, with commits for each.
Test APIs with Postman (CURL commands provided).
Write unit and integration tests using JUnit and React Testing Library.
Update README.md, .gitignore, .env, and Dockerfile as the project progresses.

Testing

Backend: Use JUnit and Mockito for unit tests, TestRestTemplate for integration tests.
Frontend: Use React Testing Library for component tests.
API Testing: Use Postman with CURL commands for each endpoint.

Deployment

Deploy Docker images to Docker Hub.
Use a cloud provider (e.g., AWS, Heroku) for hosting.
Configure Nginx for the frontend and MySQL in a Docker network.

Next Steps

Set up the Git repository and make the initial commit.
Create the backend project structure and configure MySQL.
Implement the first API (e.g., home endpoint).
Set up the frontend project with Vite and TypeScript.
Add features incrementally, testing each with Postman.
