package com.reliaquest.api.client;

import com.reliaquest.api.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public interface ApiClient {

    @GetExchange
    EmployeeDataResponse getAllEmployees();

    @GetExchange("/{id}")
    EmployeeResponse getEmployeeById(@PathVariable String id);

    @PostExchange
    Employee createEmployee(@RequestBody EmployeeInput employeeInput);

    @DeleteExchange
    DeleteEmployeeResponse deleteEmployeeByName(@RequestBody DeleteEmployeeInput deleteEmployeeInput);
}
