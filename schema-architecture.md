# Smart Clinic – Architecture Documentation

## 1. Architecture Summary

The Smart Clinic application is a Spring Boot–based system designed using a layered architecture that follows clean separation of concerns and Spring Boot best practices.

The application supports both server-rendered web interfaces and RESTful APIs. Thymeleaf is used for rendering web dashboards for administrative and medical staff, while REST APIs provide JSON-based communication for client applications such as patient modules and external integrations.

The backend is organized into controller, service, and repository layers. Business logic is centralized in the service layer, while data persistence is handled through repositories connected to two databases: MySQL for relational data and MongoDB for document-based data. JPA entities and MongoDB document models are used to represent application data.

This architecture improves maintainability, scalability, and flexibility by clearly separating presentation, business logic, and data access responsibilities.

---

## 2. Step-by-Step Flow of Data and Control

1. **User Interface Layer**  
   Users interact with the system through either:
   - Thymeleaf-based web dashboards (e.g., AdminDashboard, DoctorDashboard), or  
   - REST API clients (e.g., patient modules, mobile apps), which communicate using HTTP and JSON.

2. **Controller Layer**  
   Incoming requests are routed to the appropriate controllers:
   - MVC controllers handle server-rendered views and return HTML templates.
   - REST controllers handle API requests and return JSON responses.  
   Controllers act as entry points, performing basic validation and delegating logic to the service layer.

3. **Service Layer**  
   The service layer contains the core business logic of the application. It:
   - Applies business rules and validations,
   - Coordinates workflows across multiple entities (e.g., verifying doctor availability before creating appointments),
   - Ensures a clear separation between presentation logic and data access.

4. **Repository Layer**  
   Services interact with repositories to access data:
   - MySQL repositories use Spring Data JPA to manage relational entities such as patients, doctors, appointments, and administrators.
   - MongoDB repositories use Spring Data MongoDB to manage document-based data such as prescriptions.  
   Repositories abstract database operations and provide a consistent data access interface.

5. **Database Layer**  
   The application uses a dual-database approach:
   - MySQL stores structured relational data with constraints and relationships.
   - MongoDB stores flexible, schema-less data suitable for evolving document structures.  
   This combination leverages the strengths of both relational and NoSQL databases.

6. **Model Binding**  
   Data retrieved from databases is mapped to Java models:
   - MySQL data is mapped to JPA entities annotated with `@Entity`.
   - MongoDB data is mapped to document models annotated with `@Document`.  
   These models provide an object-oriented representation of application data across layers.

7. **Response Generation**  
   The processed models are returned to the client:
   - In MVC flows, models are passed to Thymeleaf templates and rendered as dynamic HTML pages.
   - In REST flows, models or DTOs are serialized into JSON and returned as HTTP responses.  

   This completes the request–response lifecycle of the Smart Clinic application.
