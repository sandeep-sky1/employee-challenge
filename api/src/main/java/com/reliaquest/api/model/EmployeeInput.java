package com.reliaquest.api.model;

import lombok.NonNull;

public record EmployeeInput(
        @NonNull String name, @NonNull Integer salary, @NonNull Integer age, @NonNull String title) {
    public EmployeeInput {
        if (age < 16 || age > 75) {
            throw new IllegalArgumentException("Age must be between 16 and 75");
        }
    }
}
