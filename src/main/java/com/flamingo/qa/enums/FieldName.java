package com.flamingo.qa.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldName {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    USER_EMAIL("userEmail"),
    MALE("gender-radio-1"),
    FEMALE("gender-radio-2"),
    OTHER("gender-radio-3"),
    USER_NUMBER("userNumber");

    private final String fieldName;

}
