package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.model.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final ApiClient apiClient;
    private final EmployeeCacheService employeeCacheService;

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.debug("Get employees by search string {}", searchString);
        List<Employee> employees = employeeCacheService.getAllEmployees();
        return employees.stream()
                .filter(employee -> employee.name().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        log.debug("Get employee by id {}", id);
        EmployeeResponse employeeResponse = apiClient.getEmployeeById(id);
        return employeeResponse.data();
    }

    public Integer getHighestSalaryOfEmployees() {
        log.debug("Get highest salary of employees");
        List<Employee> employees = employeeCacheService.getAllEmployees();
        return employees.stream().map(Employee::salary).max(Integer::compareTo).orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.debug("Getting top 10 highest earning employees");
        List<Employee> employees = employeeCacheService.getAllEmployees();
        return employees.stream()
                .sorted(Comparator.comparingInt(Employee::salary).reversed())
                .limit(10)
                .map(Employee::name)
                .collect(Collectors.toList());
    }

    @CachePut(value = "employees")
    public Employee createEmployee(EmployeeInput employeeInput) {
        log.debug("Creating employee {}", employeeInput);
        return apiClient.createEmployee(employeeInput);
    }

    @CacheEvict(value = "employees")
    public DeleteEmployeeResponse deleteEmployeeById(String id) {
        log.debug("Deleting employee by id {}", id);
        Employee employee = getEmployeeById(id);
        if (employee != null && employee.name() != null) {
            log.info("Deleting employee with name {}", employee.name());
            return apiClient.deleteEmployeeByName(new DeleteEmployeeInput(employee.name()));
        }
        return null;
    }
}
