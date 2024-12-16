package com.reliaquest.api.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmployeeInputTest {

    @Test
    void testValidEmployeeInput() {
        String name = "Some Name";
        Integer salary = 50000;
        Integer age = 30;
        String title = "Software Engineer";

        EmployeeInput employeeInput = new EmployeeInput(name, salary, age, title);

        assertNotNull(employeeInput);
        assertEquals(name, employeeInput.name());
        assertEquals(salary, employeeInput.salary());
        assertEquals(age, employeeInput.age());
        assertEquals(title, employeeInput.title());
    }

    @Test
    void testValidEmployeeInputWithNullName() {
        Integer salary = 50000;
        Integer age = 30;
        String title = "Software Engineer";

        NullPointerException exception =
                assertThrows(NullPointerException.class, () -> new EmployeeInput(null, salary, age, title));

        assertEquals("name is marked non-null but is null", exception.getMessage());
    }

    @Test
    void testEmployeeInputWithAgeTooLow() {
        String name = "Some Name";
        Integer salary = 50000;
        Integer age = 15;
        String title = "Software Engineer";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new EmployeeInput(name, salary, age, title));

        assertEquals("Age must be between 16 and 75", exception.getMessage());
    }

    @Test
    void testEmployeeInputWithAgeTooHigh() {
        String name = "Some Name";
        Integer salary = 50000;
        Integer age = 80;
        String title = "Software Engineer";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new EmployeeInput(name, salary, age, title));

        assertEquals("Age must be between 16 and 75", exception.getMessage());
    }

    @Test
    void testEmployeeInputWithValidAgeEdgeCaseLow() {
        String name = "Some Name";
        Integer salary = 50000;
        Integer age = 16;
        String title = "Software Engineer";

        EmployeeInput employeeInput = new EmployeeInput(name, salary, age, title);

        assertNotNull(employeeInput);
    }

    @Test
    void testEmployeeInputWithValidAgeEdgeCaseHigh() {
        String name = "Some Name";
        Integer salary = 50000;
        Integer age = 75;
        String title = "Software Engineer";

        EmployeeInput employeeInput = new EmployeeInput(name, salary, age, title);

        assertNotNull(employeeInput);
    }
}
