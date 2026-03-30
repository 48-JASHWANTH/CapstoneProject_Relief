# Relief - Disaster Insurance Management System

## Overview
Relief is a comprehensive Disaster Insurance Management System designed to streamline the administration of disaster-relief insurance policies. It provides a robust platform for customers to apply for insurance and file claims, while equipping agents, claims officers, and administrators with powerful tools to underwrite policies, process claims, and manage system-wide disaster zones and risk pools.

The system is built with a modern technology stack, featuring a secure **Spring Boot (Java 21)** backend API and a responsive, standalone **Angular 17+** frontend integrated with Tailwind CSS and Angular Material.

## System Architecture

The project is divided into two main modules:
- **`Relief` (Backend)**: A RESTful API built with Spring Boot. It uses Spring Data JPA for data persistence, Spring Security with JWT for authentication and authorization, and exposes API documentation via Springdoc OpenAPI.
- **`ReliefFrontend` (Frontend)**: A modern single-page application built with Angular 17. It utilizes standalone components, reactive forms, Angular Material for UI components, Tailwind CSS for styling, and Chart.js (via ng2-charts) for dashboards.

## Core Features by User Role

The system supports four distinct user roles, each with specialized access and capabilities:

### 1. Customer
- **Dashboard**: View overall policy and claim statuses.
- **Policy Management**: Apply for new disaster insurance policies (Flood, Earthquake, Cyclone, Hurricane) and pay premiums.
- **Claim Processing**: File new claims against active policies and track claim status.

### 2. Admin (System Administrator)
- **Dashboard**: System-wide overview with charts and risk pool summaries.
- **User & Agent Management**: Manage customer, agent, and officer accounts, including role assignments and status toggles.
- **Policy Management**: Final approval or rejection of policy applications forwarded by agents.
- **Disaster Zones & Risk Pools**: Define and manage geographic disaster zones and monitor financial risk pools to ensure system stability.

### 3. Agent (Underwriter)
- **Dashboard**: Track assigned policies, loss frequencies, and approval ratios.
- **Policy Underwriting**: Review customer applications, adjust premiums based on risk, and forward policies for admin approval.
- **Claim Visibility**: View claims filed against policies they underwrote.

### 4. Claims Officer
- **Dashboard**: Monitor all system claims, approved payouts, and high-value claim alerts.
- **Claim Processing**: Review filed claims, mark them as under review, and make final approval/rejection decisions with payout amounts.
- **High-Value Claims**: Dedicated workflow for evaluating claims exceeding standard thresholds.

## Technology Stack

### Backend (`Relief`)
- Java 21
- Spring Boot 3.x
- Spring Security + JWT (JSON Web Tokens)
- Spring Data JPA
- H2 Database (In-memory)
- Lombok
- Springdoc OpenAPI (Swagger UI)
- Maven

### Frontend (`ReliefFrontend`)
- Angular 17+ (Standalone Components)
- TypeScript
- Tailwind CSS
- Angular Material UI
- ng2-charts (Chart.js)
- jwt-decode

## Prerequisites
To run this project locally, ensure you have the following installed:
- [Java Development Kit (JDK) 21](https://jdk.java.net/21/)
- [Maven](https://maven.apache.org/)
- [Node.js](https://nodejs.org/) (v18 or higher recommended)
- [Angular CLI](https://angular.io/cli) (`npm install -g @angular/cli`)

## Setup and Installation

### 1. Running the Backend
1. Navigate to the backend directory:
   ```bash
   cd Relief
   ```
2. Build and run the Spring Boot application using the Maven wrapper:
   ```bash
   # On Windows
   mvnw.cmd spring-boot:run
   
   # On Mac/Linux
   ./mvnw spring-boot:run
   ```
3. The API will be available at `http://localhost:8080`.
4. Swagger API documentation can typically be found at `http://localhost:8080/swagger-ui.html`.

### 2. Running the Frontend
1. Open a new terminal and navigate to the frontend directory:
   ```bash
   cd ReliefFrontend
   ```
2. Install the Node dependencies:
   ```bash
   npm install
   ```
3. Start the Angular development server:
   ```bash
   ng serve
   ```
4. Open your browser and navigate to `http://localhost:4200`.

## Default Login Credentials
*(Based on default seed data, if applicable)*
- **Admin**: `admin@relief.com` / `admin123`
- **Agent**: `john@relief.com` / `agent123`
- **Claims Officer**: `sara@relief.com` / `officer123`
- **Customer**: `alice@relief.com` or `bob@relief.com` / `customer123`
