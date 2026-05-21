package com.flamingo.qa.ui;

import com.flamingo.qa.enums.FieldName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static com.flamingo.qa.enums.FieldName.*;
import static com.flamingo.qa.enums.FieldName.LAST_NAME;
import static com.flamingo.qa.ui.utils.FileUtils.getUploadFilePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Tag("ui")
@Execution(ExecutionMode.CONCURRENT)
class RegisterNewStudentTests extends BaseTest {

    @Test
    @DisplayName("Verify new student registration")
    void verifyNewStudentRegistration() {
        Student expectedStudent = Student.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .mobile("5551234567")
                .gender("Female")
                .dateOfBirth("1990-05-12")
                .subjects(List.of("Maths", "Computer Science"))
                .hobbies(List.of("Reading"))
                .picture(getUploadFilePath("flamingo-test.txt"))
                .address("221B Baker Street")
                .build();

        registerStudentFormPage
                .submitStudentRegistrationForm(expectedStudent)
                .waitForSuccessModal();

        assertSoftly(softly -> {
            softly.assertThat(registerStudentFormPage.isSuccessModalVisible()).as("success modal visible").isTrue();
            softly.assertThat(registerStudentFormPage.getModalTitle()).isEqualTo("Thanks for submitting the form");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Student Name")).isEqualTo("Ada Lovelace");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Student Email")).isEqualTo("ada@example.com");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Gender")).isEqualTo("Female");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Mobile")).isEqualTo("5551234567");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Subjects")).contains("Maths", "Computer Science");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Picture")).isEqualTo("flamingo-test.txt");
            softly.assertThat(registerStudentFormPage.getSubmittedValue("Address")).isEqualTo("221B Baker Street");
        });
    }

    @Test
    @DisplayName("Verify form submission with missing mandatory fields")
    void verifyFormSubmissionWithMissingMandatoryFields() {
        Student incompleteStudent = Student.builder()
                .firstName("") // Missing first name
                .lastName("")  // Missing last name
                .email("invalid-email") // Invalid email format
                .mobile("") // Missing mobile number
                .gender("") // Missing gender
                .dateOfBirth("1990-05-12")
                .subjects(List.of()) // No subjects
                .hobbies(List.of()) // No hobbies
                .picture(getUploadFilePath("flamingo-test.txt"))
                .address("") // Missing address
                .build();

        registerStudentFormPage.submitStudentRegistrationForm(incompleteStudent);
        List<FieldName> expectedInvalidFields = List.of(FIRST_NAME, LAST_NAME, USER_EMAIL, MALE, FEMALE, OTHER, USER_NUMBER);
        assertSoftly(softly -> {
            softly.assertThat(registerStudentFormPage.isSuccessModalVisible())
                    .as("Success modal should not be visible for incomplete forms")
                    .isFalse();

            softly.assertThat(expectedInvalidFields)
                    .allSatisfy(fieldName -> {
                        Boolean errorMessage = registerStudentFormPage.getFieldError(fieldName.getFieldName());
                        softly.assertThat(errorMessage).as("Validation check for missing/invalid field: %s", fieldName).isTrue();
                    });
        });

    }

}
