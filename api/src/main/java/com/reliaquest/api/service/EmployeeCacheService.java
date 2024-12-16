package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDataResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EmployeeCacheService {
    private final ApiClient apiClient;

    @Cacheable(value = "employees", condition = "#response.data() != null and #response.data().size() > 0")
    public List<Employee> getAllEmployees() {
        EmployeeDataResponse response = apiClient.getAllEmployees();
        log.debug("fetching employee list (size:{})", response.data().size());
        return response.data();
    }
}
