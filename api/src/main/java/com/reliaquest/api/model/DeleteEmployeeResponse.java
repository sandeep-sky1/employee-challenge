package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteEmployeeResponse(
        @JsonProperty("data") boolean data,
        @JsonProperty("status") String status,
        @JsonProperty("error") String error) {}
