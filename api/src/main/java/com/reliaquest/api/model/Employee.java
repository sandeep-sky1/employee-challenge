package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Employee(
        UUID id,
        @JsonProperty("employee_name") String name,
        @JsonProperty("employee_salary") Integer salary,
        @JsonProperty("employee_age") Integer age,
        @JsonProperty("employee_title") String title,
        @JsonProperty("employee_email") String email) {}
