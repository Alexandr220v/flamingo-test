package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.AppConfig;
import com.flamingo.qa.ui.browser.BrowserManager;
import com.flamingo.qa.ui.dto.Employee;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page.WaitForSelectorOptions;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Step;

import java.util.Comparator;
import java.util.List;


public class RegisterNewEmployeeFormPage extends BasePage {

    private static final String PATH = "/webtables";

    public RegisterNewEmployeeFormPage(BrowserManager browserManager) {
        super(browserManager);
    }

    @Step("Open table with employees list.")
    public RegisterNewEmployeeFormPage open() {
        navigate(AppConfig.INSTANCE.demoqaBaseUrl() + PATH);
        waitForVisible(".web-tables-wrapper");
        // Remove sticky ads and footers
        getPage().evaluate("document.querySelectorAll('iframe, #fixedban, footer').forEach(e => e.remove())");
        return this;
    }

    @Step("Register a new employee: {employee.firstName} {employee.lastName} {employee.email} {employee.age} {employee.salary} {employee.department}")
    public RegisterNewEmployeeFormPage submitEmployeeRegistrationForm(Employee employee) {
        clickAddNewRecordButton();
        fillFirstName(employee.getFirstName());
        fillLastName(employee.getLastName());
        fillEmail(employee.getEmail());
        fillAge(employee.getAge());
        fillSalary(employee.getSalary());
        fillDepartment(employee.getDepartment());
        submitForm();
        return this;
    }

    @Step("Click 'Add New Record' button")
    public RegisterNewEmployeeFormPage clickAddNewRecordButton() {
        getPage().locator("#addNewRecordButton").click();
        return this;
    }

    @Step("Click 'Submit' button")
    public RegisterNewEmployeeFormPage submitForm() {
        getPage().locator("#submit").click();
        return this;
    }

    @Step("Fill first name: {firstName}")
    public RegisterNewEmployeeFormPage fillFirstName(String firstName) {
        getPage().locator("#firstName").fill(firstName);
        return this;
    }

    @Step("Fill last name: {lastName}")
    public RegisterNewEmployeeFormPage fillLastName(String lastName) {
        getPage().locator("#lastName").fill(lastName);
        return this;
    }

    @Step("Fill email: {email}")
    public RegisterNewEmployeeFormPage fillEmail(String email) {
        getPage().locator("#userEmail").fill(email);
        return this;
    }

    @Step("Fill age: {age}")
    public RegisterNewEmployeeFormPage fillAge(int age) {
        getPage().locator("#age").fill(String.valueOf(age));
        return this;
    }

    @Step("Fill salary: {salary}")
    public RegisterNewEmployeeFormPage fillSalary(int salary) {
        getPage().locator("#salary").fill(String.valueOf(salary));
        return this;
    }

    @Step("Fill department: {department}")
    public RegisterNewEmployeeFormPage fillDepartment(String department) {
        getPage().locator("#department").fill(department);
        return this;
    }

    @Step("Search '{term}'")
    public RegisterNewEmployeeFormPage search(String term) {
        getPage().locator("#searchBox").fill(term);
        return this;
    }

    @Step("Search by unique email '{email}'")
    public RegisterNewEmployeeFormPage searchByEmail(String email) {
        search(email);
        return this;
    }

    @Step("Open to edit first employee.'")
    public RegisterNewEmployeeFormPage edit() {
        getPage().locator("span[title='Edit']").first().click();
        return this;
    }

    @Step("Delete first matching row")
    public RegisterNewEmployeeFormPage delete() {
        getPage().locator("span[title='Delete']").first().click();
        return this;
    }


    private static final String TABLE_ROW_SELECTOR = "table tbody tr";

    public List<List<String>> visibleRows() {
        Locator tableLocator = getPage().locator(TABLE_ROW_SELECTOR);
        try {
            getPage().waitForSelector(TABLE_ROW_SELECTOR, new WaitForSelectorOptions().setTimeout(1000));
        } catch (TimeoutError e) {
            return List.of();
        }
        List<Locator> rows = tableLocator.all();
        if (rows.isEmpty()) {
            return List.of();
        }
        return rows.stream()
                .map(row -> row.locator("td:not(:last-child)").allInnerTexts())
                .filter(rowValues -> rowValues.stream().anyMatch(text -> !text.trim().isEmpty()))
                .toList();
    }
    @Step("Get employees from table.")
    public List<Employee> getEmployees() {
        List<List<String>> rawRows = visibleRows();
        return rawRows.stream()
                .map(row -> Employee.builder()
                        .firstName(row.getFirst())
                        .lastName(row.get(1))
                        .age(Integer.parseInt(row.get(2)))
                        .email(row.get(3))
                        .salary(Integer.parseInt(row.get(4)))
                        .department(row.get(5))
                        .build())
                .toList();
    }

    @Step("Sort by column '{columnName} in {ascending} order and verify the order is correct.'")
    public boolean isSortedByColumn(String columnName, boolean ascending) {
        List<Employee> currentEmployees = getEmployees();
        List<String> currentOrderValues = currentEmployees.stream()
                .map(employee -> switch (columnName.toLowerCase().trim()) {
                    case "first name" -> employee.getFirstName();
                    case "last name"  -> employee.getLastName();
                    case "age"        -> String.valueOf(employee.getAge());
                    case "email"      -> employee.getEmail();
                    case "salary"     -> String.valueOf(employee.getSalary());
                    case "department" -> employee.getDepartment();
                    default -> throw new IllegalArgumentException("Unknown column header field name: " + columnName);
                })
                .toList();
        Comparator<String> customComparator = (val1, val2) -> {
            if (columnName.equalsIgnoreCase("age") || columnName.equalsIgnoreCase("salary")) {
                return Integer.compare(Integer.parseInt(val1), Integer.parseInt(val2));
            }
            return val1.compareToIgnoreCase(val2);
        };

        List<String> expectedSortedOrder = currentOrderValues.stream()
                .sorted(ascending ? customComparator : customComparator.reversed())
                .toList();
        return currentOrderValues.equals(expectedSortedOrder);
    }
}
