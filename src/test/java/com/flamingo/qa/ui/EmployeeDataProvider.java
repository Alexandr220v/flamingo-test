// Moved the helper methods to a data provider class
package com.flamingo.qa.ui;


import com.flamingo.qa.ui.dto.Employee;

import java.util.UUID;

import static com.flamingo.qa.ui.dto.Employee.*;

public class EmployeeDataProvider {

    public static Employee createTestEmployee(String uniqueSuffix) {
        String email = uniqueSuffix + "@example.com";
        return builder()
                .firstName("Albert")
                .lastName("Einstein")
                .email(email)
                .age(30)
                .salary(60000)
                .department("QA")
                .build();
    }

    public static String generateUniqueSuffix() {
        return "qa-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
