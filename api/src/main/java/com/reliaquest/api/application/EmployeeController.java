package com.reliaquest.api.application;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.DeleteEmployeeResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeCacheService;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;
    private final EmployeeCacheService employeeCacheService;

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeCacheService.getAllEmployees();
        log.debug("Returning employees: {}", employees);
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        log.debug("Returning employees: {} for search string {}", employees, searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Employee employee = employeeService.getEmployeeById(id);
        log.debug("Returning employee: {} for id {}", employee, id);
        return employee != null
                ? ResponseEntity.ok(employee)
                : ResponseEntity.notFound().build();
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        log.debug("Returning highest salary amongst employees {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        log.debug("Returning top 10 highest earning employees {}", topTenNames);
        return ResponseEntity.ok(topTenNames);
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeInput employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        log.debug("Created employee {}", employee);
        return employee != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(employee)
                : ResponseEntity.badRequest().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        DeleteEmployeeResponse deleteEmployeeResponse = employeeService.deleteEmployeeById(id);
        if (deleteEmployeeResponse != null) {
            if (deleteEmployeeResponse.error() != null) {
                log.error("Error deleting employee {}: error {}", id, deleteEmployeeResponse.error());
                return ResponseEntity.badRequest()
                        .body(deleteEmployeeResponse.status() + " : " + deleteEmployeeResponse.error() + " : " + id);
            } else {
                log.error("Successfully deleting employee {}: status {}", id, deleteEmployeeResponse.status());
                return ResponseEntity.ok().body(deleteEmployeeResponse.status() + " : " + id);
            }
        }
        log.error("Error deleting employee {}", id);
        return ResponseEntity.internalServerError().body("Error deleting employee with id " + id);
    }
}
