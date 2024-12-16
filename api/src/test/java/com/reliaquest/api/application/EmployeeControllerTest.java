package com.reliaquest.api.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.DeleteEmployeeResponse;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeCacheService;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeCacheService employeeCacheService;

    @InjectMocks
    @SuppressWarnings("unused")
    private EmployeeController employeeController;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee1;
    private Employee employee2;

    private final UUID id_1 = UUID.fromString("7ee3bf8f-9ec5-40c2-a079-ec88028c6a90");
    private final UUID id_2 = UUID.fromString("c297b3b1-1365-44aa-a690-7cfe57b7ec71");

    @BeforeEach
    public void setUp() {
        employee1 = new Employee(id_1, "John Smith", 120000, 38, "Senior Developer", "john.smoth@domain.com");
        employee2 = new Employee(id_2, "Dan Doe", 90000, 29, "Developer", "dan.doe@domain.com");
    }

    @Test
    public void shouldReturnAllEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeCacheService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(employee1.id().toString()))
                .andExpect(jsonPath("$[0].employee_name").value(employee1.name()))
                .andExpect(jsonPath("$[0].employee_salary").value(employee1.salary()))
                .andExpect(jsonPath("$[0].employee_age").value(employee1.age()))
                .andExpect(jsonPath("$[0].employee_title").value(employee1.title()))
                .andExpect(jsonPath("$[0].employee_email").value(employee1.email()))
                .andExpect(jsonPath("$[1].id").value(employee2.id().toString()))
                .andExpect(jsonPath("$[1].employee_name").value(employee2.name()))
                .andExpect(jsonPath("$[1].employee_salary").value(employee2.salary()))
                .andExpect(jsonPath("$[1].employee_age").value(employee2.age()))
                .andExpect(jsonPath("$[1].employee_email").value(employee2.email()));
    }

    @Test
    public void shouldReturnEmployeesByNameSearch() throws Exception {
        String searchString = "John";
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(employees);

        mockMvc.perform(get("/employees/search/{searchString}", searchString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employee_name").value(employee1.name()))
                .andExpect(jsonPath("$[0].employee_salary").value(employee1.salary()))
                .andExpect(jsonPath("$[0].employee_age").value(employee1.age()))
                .andExpect(jsonPath("$[0].employee_email").value(employee1.email()));
    }

    @Test
    public void shouldReturnEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(id_1.toString())).thenReturn(employee1);

        mockMvc.perform(get("/employees/{id}", id_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee1.id().toString()))
                .andExpect(jsonPath("$.employee_name").value(employee1.name()))
                .andExpect(jsonPath("$.employee_salary").value(employee1.salary()))
                .andExpect(jsonPath("$.employee_age").value(employee1.age()))
                .andExpect(jsonPath("$.employee_email").value(employee1.email()));
    }

    @Test
    public void shouldReturnNotFoundWhenEmployeeNotExists() throws Exception {
        String employeeId = "1";
        when(employeeService.getEmployeeById(employeeId)).thenReturn(null);

        mockMvc.perform(get("/employees/{id}", employeeId)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHighestSalary() throws Exception {
        Integer highestSalary = 200000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

        mockMvc.perform(get("/employees/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(highestSalary));
    }

    @Test
    public void shouldReturnTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> topTenNames = Arrays.asList("John Doe", "Jane Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topTenNames);

        mockMvc.perform(get("/employees/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("John Doe"))
                .andExpect(jsonPath("$[1]").value("Jane Doe"));
    }

    @Test
    public void shouldCreateEmployee() throws Exception {
        EmployeeInput employeeInput = new EmployeeInput("Jane Gore", 120000, 28, "Analyst");
        Employee createdEmployee =
                new Employee(UUID.randomUUID(), "Jane Gore", 120000, 28, "Analyst", "jane.gore@gmail.com");
        when(employeeService.createEmployee(employeeInput)).thenReturn(createdEmployee);

        String employeeStr = objectMapper.writeValueAsString(employeeInput);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeStr))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employee_name").value(createdEmployee.name()))
                .andExpect(jsonPath("$.employee_salary").value(createdEmployee.salary()))
                .andExpect(jsonPath("$.employee_age").value(createdEmployee.age()))
                .andExpect(jsonPath("$.employee_email").value(createdEmployee.email()));
    }

    @Test
    public void shouldReturnBadRequestWhenCreateEmployeeFails() throws Exception {
        EmployeeInput employeeInput = new EmployeeInput("Jane Gore", 120000, 28, "Analyst");
        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(null);

        String employeeStr = objectMapper.writeValueAsString(employeeInput);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeStr))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteEmployeeById() throws Exception {
        DeleteEmployeeResponse employeeResponse = new DeleteEmployeeResponse(true, "success", null);
        when(employeeService.deleteEmployeeById(id_1.toString())).thenReturn(employeeResponse);
        when(employeeService.getEmployeeById(id_1.toString())).thenReturn(employee1);

        mockMvc.perform(delete("/employees/{id}", id_1))
                .andExpect(status().isOk())
                .andExpect(content().string("success : 7ee3bf8f-9ec5-40c2-a079-ec88028c6a90"));
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteEmployeeNotExists() throws Exception {
        DeleteEmployeeResponse employeeResponse = new DeleteEmployeeResponse(false, "failure", "not found");
        when(employeeService.getEmployeeById(id_2.toString())).thenReturn(employee2);
        when(employeeService.deleteEmployeeById(id_2.toString())).thenReturn(employeeResponse);

        mockMvc.perform(delete("/employees/{id}", id_2))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("failure : not found : c297b3b1-1365-44aa-a690-7cfe57b7ec71"));
    }
}
