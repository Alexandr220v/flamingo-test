package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.AppConfig;
import com.flamingo.qa.ui.Student;
import com.flamingo.qa.ui.browser.BrowserManager;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;

import com.flamingo.qa.utils.DateUtils;

public class RegisterStudentFormPage extends BasePage {

    private static final String PATH = "/automation-practice-form";


    public RegisterStudentFormPage(BrowserManager browserManager) {
        super(browserManager);
    }

    @Step("Open practice form")
    public RegisterStudentFormPage open() {
        navigate(AppConfig.INSTANCE.demoqaBaseUrl() + PATH);
        waitForVisible("#firstName");
        return this;
    }

    @Step("Submit student registration form")
    public RegisterStudentFormPage submitStudentRegistrationForm(Student student) {

        open()
                .fillFirstName(student.getFirstName())
                .fillLastName(student.getLastName())
                .fillEmail(student.getEmail())
                .fillPhone(student.getMobile())
                .selectGender(student.getGender())
                .setDateOfBirth(student.getDateOfBirth())
                .chooseSubjects(student.getSubjects())
                .tickHobbies(student.getHobbies())
                .uploadPicture(student.getPicture())
                .fillAddress(student.getAddress())
                .submit();
        return this;
    }

    @Step("Fill first name '{first}'")
    public RegisterStudentFormPage fillFirstName(String first) {
        getPage().locator("#firstName").fill(first);
        return this;
    }

    @Step("Fill last name '{last}'")
    public RegisterStudentFormPage fillLastName(String last) {
        getPage().locator("#lastName").fill(last);
        return this;
    }

    @Step("Fill email '{email}'")
    public RegisterStudentFormPage fillEmail(String email) {
        getPage().locator("#userEmail").fill(email);
        return this;
    }

    @Step("Fill phone '{phone}'")
    public RegisterStudentFormPage fillPhone(String phone) {
        getPage().locator("#userNumber").fill(phone);
        return this;
    }

    @Step("Select gender '{gender}'")
    public RegisterStudentFormPage selectGender(String gender) {
        getPage().locator("label")
                .filter(new Locator.FilterOptions().setHasText(gender))
                .first().click();
        return this;
    }

    @Step("Set date of birth with day {day}, month {month}, year {year}")
    public RegisterStudentFormPage setDateOfBirth(int day, int month, int year) {
        Locator dob = getPage().locator("#dateOfBirthInput");
        dob.click();
        // Select year
        Locator yearDropdown = getPage().locator(".react-datepicker__year-select");
        yearDropdown.selectOption(String.valueOf(year));
        // Select month
        Locator monthDropdown = getPage().locator(".react-datepicker__month-select");
        monthDropdown.selectOption(String.valueOf(month - 1));
        // Select day
        String dayLocator = format(".react-datepicker__day--%03d:not(.react-datepicker__day--outside-month)", day);
        Locator dayElement = getPage().locator(dayLocator);
        dayElement.click();

        return this;
    }

    @Step("Choose subjects {subjects}")
    public RegisterStudentFormPage chooseSubjects(List<String> subjects) {
        Locator input = getPage().locator("#subjectsInput");
        subjects.forEach(s -> {
            input.fill(s);
            getPage().locator(".subjects-auto-complete__option")
                    .filter(new Locator.FilterOptions().setHasText(s))
                    .first()
                    .click();
        });
        return this;
    }

    @Step("Tick hobbies {hobbies}")
    public RegisterStudentFormPage tickHobbies(List<String> hobbies) {
        hobbies.forEach(h -> getPage().locator("label")
                .filter(new Locator.FilterOptions().setHasText(h))
                .first().click());
        return this;
    }

    @Step("Upload picture {path}")
    public RegisterStudentFormPage uploadPicture(Path path) {
        getPage().locator("#uploadPicture").setInputFiles(path);
        return this;
    }

    @Step("Fill address '{address}'")
    public RegisterStudentFormPage fillAddress(String address) {
        getPage().locator("#currentAddress").fill(address);
        return this;
    }

    @Step("Submit form")
    public RegisterStudentFormPage submit() {
        Locator submit = getPage().locator("#submit");
        scrollIntoView(submit);
        submit.click();
        return this;
    }

    @Step("Wait for success modal to display")
    public RegisterStudentFormPage waitForSuccessModal() {
        try {
            getPage().waitForSelector(".modal-content",
                    new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));
        } catch (Exception e) {
            throw new RuntimeException("Form submission failed. Modal content not displayed. Check mandatory fields.", e);
        }
        return this;
    }

    public boolean isSuccessModalVisible() {
        return getPage().locator("#example-modal-sizes-title-lg").isVisible();
    }

    public String getModalTitle() {
        return getPage().locator("#example-modal-sizes-title-lg").innerText();
    }

    public String getSubmittedValue(String label) {
        return getPage().locator("table.table tr")
                .filter(new Locator.FilterOptions().setHasText(label))
                .locator("td").nth(1).innerText();
    }

    public RegisterStudentFormPage setDateOfBirth(String dateStr) {
        int[] parts = DateUtils.parseDate(dateStr);
        return setDateOfBirth(parts[2], parts[1], parts[0]);
    }

    public Boolean getFieldError(String fieldNameOrRadioButton) {
        String errorSelector = format("#%s:invalid", fieldNameOrRadioButton);
        return getPage().locator(errorSelector).isVisible();
    }
}
