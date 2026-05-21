package com.flamingo.qa.ui;

import com.flamingo.qa.ui.dto.Employee;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Comparator;

import static com.flamingo.qa.ui.EmployeeDataProvider.createTestEmployee;
import static com.flamingo.qa.ui.EmployeeDataProvider.generateUniqueSuffix;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
@Execution(ExecutionMode.CONCURRENT)
@Feature("Employee Management")
class RegisterNewEmployeeTests extends BaseTest {


    @Test
    @DisplayName("Verify that a new employee can be registered successfully.")
    void verifyRegisterNewEmployee() {
        String unique = generateUniqueSuffix();
        Employee employee = createTestEmployee(unique);

        registerNewEmployeeFormPage
                .open()
                .submitEmployeeRegistrationForm(employee)
                .search(employee.getEmail());

        List<Employee> employees = registerNewEmployeeFormPage.getEmployees();
        assertThat(employees).as("Exactly one row matches the unique email").hasSize(1);
        assertThat(employees.getFirst()).usingRecursiveComparison().isEqualTo(employee);
    }

    @Test
    @DisplayName("Verify that an employee can be updated successfully.")
    void verifyUpdateEmployee() {
        String unique = generateUniqueSuffix();
        Employee employee = createTestEmployee(unique);

        registerNewEmployeeFormPage
                .open()
                .submitEmployeeRegistrationForm(employee)
                .search(employee.getEmail());

        Employee expectedUpdated = Employee.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .age(employee.getAge())
                .salary(80000)
                .department("DevOps")
                .build();

        registerNewEmployeeFormPage
                .edit()
                .fillSalary(expectedUpdated.getSalary())
                .fillDepartment(expectedUpdated.getDepartment())
                .submitForm();

        assertThat(registerNewEmployeeFormPage.getEmployees().getFirst())
                .usingRecursiveComparison()
                .isEqualTo(expectedUpdated);
    }

    @Test
    @DisplayName("Verify that an employee can be searched successfully.")
    void verifySearchEmployee() {
        String unique = generateUniqueSuffix();
        Employee employee = createTestEmployee(unique);

        registerNewEmployeeFormPage
                .open()
                .submitEmployeeRegistrationForm(employee)
                .search(employee.getEmail());

        List<Employee> employees = registerNewEmployeeFormPage.getEmployees();
        assertThat(employees).as("Exactly one row matches the unique email").hasSize(1);
        assertThat(employees.getFirst()).usingRecursiveComparison().isEqualTo(employee);
    }

    @Test
    @DisplayName("Verify that an employee can be deleted successfully.")
    void verifyDeleteEmployee() {
        String unique = generateUniqueSuffix();
        Employee employee = createTestEmployee(unique);

        registerNewEmployeeFormPage
                .open()
                .submitEmployeeRegistrationForm(employee)
                .search(employee.getEmail());

        registerNewEmployeeFormPage.delete();

        assertThat(registerNewEmployeeFormPage.getEmployees())
                .as("Employee was removed after delete")
                .isEmpty();
    }

    @Test
    @DisplayName("Verify that employees can be sorted by first name in ascending order.")
    void verifyEmployeeAreSortedInTable() {
        //Sorting is not implemented in the UI, so we will just verify that the table is sorted by first name in ascending order (A-Z) by default
        registerNewEmployeeFormPage.open();
        assertThat(registerNewEmployeeFormPage.isSortedByColumn("First Name", true))
                .as("Table rows should be sorted by First Name in Ascending order (A-Z)")
                .isTrue();
    }
}
