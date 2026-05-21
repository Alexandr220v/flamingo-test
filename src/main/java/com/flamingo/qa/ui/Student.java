package com.flamingo.qa.ui;

import java.nio.file.Path;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Student {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String mobile;
    private final String gender;
    private final String dateOfBirth;
    private final List<String> subjects;
    private final List<String> hobbies;
    private final Path picture;
    private final String address;

    public Student(String firstName, String lastName, String email, String mobile, String gender, String dateOfBirth, List<String> subjects, List<String> hobbies, Path picture, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.subjects = subjects;
        this.hobbies = hobbies;
        this.picture = picture;
        this.address = address;
    }
}
