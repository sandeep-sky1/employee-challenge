package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDataResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeCacheServiceTest {

    @Mock
    private ApiClient apiClient;

    @InjectMocks
    private EmployeeCacheService employeeCacheService;

    private EmployeeDataResponse employeeDataResponse;

    private final UUID id = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        employeeDataResponse = new EmployeeDataResponse(
                List.of(new Employee(id, "John Doe", 50000, 30, "Developer", "john.doe@example.com")),
                "Successfully processed request.");
    }

    @Test
    void testGetAllEmployeesSuccess() {
        when(apiClient.getAllEmployees()).thenReturn(employeeDataResponse);

        List<Employee> employees = employeeCacheService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).name());

        verify(apiClient, times(1)).getAllEmployees();
    }
}
