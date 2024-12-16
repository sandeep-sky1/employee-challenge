package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeResponse(@JsonProperty("data") Employee data, @JsonProperty("status") String status) {}
