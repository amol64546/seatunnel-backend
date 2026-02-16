# SeaTunnel Orchestrator Backend

SeaTunnel Orchestrator is a backend service designed to manage, orchestrate, and monitor ETL (Extract, Transform, Load) pipelines and bricks. Built with Java and Spring Boot, it provides RESTful APIs for pipeline and brick management, job orchestration, and monitoring.

## Functionality
- **Pipeline Management:** Create, update, delete, and retrieve ETL pipelines.
- **Brick Management:** Manage ETL bricks (reusable ETL components) including CRUD operations.
- **Job Orchestration:** Start, stop, and monitor ETL jobs, supporting different job modes and statuses.
- **Validation:** Input validation for pipelines and bricks using custom annotations and validators.
- **Exception Handling:** Centralized error handling for API requests.
- **MongoDB Integration:** Persist pipelines, bricks, and job metadata in MongoDB.

## APIs
The backend exposes RESTful APIs for managing ETL pipelines and bricks. Main endpoints include:

### Pipeline APIs
- `GET /api/pipelines` — List all pipelines
- `GET /api/pipelines/{id}` — Get pipeline by ID
- `POST /api/pipelines` — Create a new pipeline
- `PUT /api/pipelines/{id}` — Update an existing pipeline
- `DELETE /api/pipelines/{id}` — Delete a pipeline

### Brick APIs
- `GET /api/bricks` — List all bricks
- `GET /api/bricks/{id}` — Get brick by ID
- `POST /api/bricks` — Create a new brick
- `PUT /api/bricks/{id}` — Update an existing brick
- `DELETE /api/bricks/{id}` — Delete a brick

### Job Orchestration APIs
- `POST /api/pipelines/{id}/start` — Start a pipeline job
- `POST /api/pipelines/{id}/stop` — Stop a running pipeline job
- `GET /api/jobs/{id}/status` — Get job status
- `GET /api/jobs/overview` — Get overview of all jobs

Refer to the controller classes in `src/main/java/com/seatunnel/orchestrator/controller/` for more details and additional endpoints.

## Design
The project follows a modular, layered architecture:
- **Controller Layer:** Exposes RESTful endpoints for pipelines and bricks (see `controller/`).
- **Service Layer:** Contains business logic for orchestrating ETL jobs and managing entities (see `service/`).
- **Repository Layer:** Handles data persistence with MongoDB (see `repository/`).
- **Model Layer:** Defines data models for pipelines, bricks, jobs, and related entities (see `model/`).
- **Validation & Exception Handling:** Custom annotations and global exception handler for robust API behavior.
- **Configuration:** MongoDB converters and application properties for flexible setup (see `config/` and `resources/`).

## Features
- ETL pipeline and brick management
- Job orchestration and monitoring
- RESTful API endpoints
- MongoDB integration
- Exception handling and validation

## Project Structure
- `src/main/java/com/seatunnel/orchestrator/` - Main Java source code
- `src/main/resources/` - Application configuration
- `src/test/java/com/seatunnel/orchestrator/` - Test cases
- `pom.xml` - Maven build configuration

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB instance

### Build and Run

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd SeaTunnel-Orchestrator
   ```
2. **Configure MongoDB:**
   Edit `src/main/resources/application.properties` to set your MongoDB connection details.
3. **Build the project:**
   ```bash
   ./mvnw clean package
   ```
4. **Run the application:**
   ```bash
   java -jar target/seatunnel-orchestrator-0.0.1-SNAPSHOT.jar
   ```

### API Usage
The backend exposes RESTful endpoints for managing ETL pipelines and bricks. See the controller classes in `src/main/java/com/seatunnel/orchestrator/controller/` for details.

## Contributing
Contributions are welcome! Please open issues or submit pull requests for improvements and bug fixes.

## License
This project is licensed under the Apache 2.0 License.

## Contact
For questions or support, please open an issue in the repository.
