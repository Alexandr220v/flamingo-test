package com.flamingo.qa.ui.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Employee {
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private int salary;
    private String department;
}
