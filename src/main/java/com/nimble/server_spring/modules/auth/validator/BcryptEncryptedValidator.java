package com.nimble.server_spring.modules.auth.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class BcryptEncryptedValidator implements ConstraintValidator<BcryptEncrypted, String> {
    @Override
    public void initialize(BcryptEncrypted constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile("/^[a-zA-Z0-9$./]{60}$/gi");
        return !pattern.matcher(value).find();
    }
}
