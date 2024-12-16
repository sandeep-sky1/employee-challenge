package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EmployeeDataResponse(@JsonProperty("data") List<Employee> data, @JsonProperty("status") String status) {}
