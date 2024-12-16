# Problem Description

Please check `README.md` for detailed information.

## Solution Overview

The solution implements the `/employee` API with the following endpoints:

- **`getAllEmployees()`**
- **`getEmployeesByNameSearch(name fragment)`**
- **`getEmployeeById(id)`**
- **`getHighestSalaryOfEmployees()`**
- **`getTop10HighestEarningEmployeeNames()`**
- **`createEmployee(...)`**
- **`deleteEmployeeById(...)`**

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.x**
- **Wiremock**

## Local Setup

### Running Tests

To run the tests, use the following command:

```bash
./gradlew :api:test
```

### Building the Project

To build the project, use the following command:

```bash
./gradlew :api:build
```

### Running the Application Server

To start the application server, use:

```bash
./gradlew :api:bootRun
```

### Author
#### Sandeep Mestry
#### sanmestry@gmail.com