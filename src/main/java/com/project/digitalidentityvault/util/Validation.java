package com.project.digitalidentityvault.util;

import com.project.digitalidentityvault.exception.InvalidEmailException;

public class Validation {
    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w+-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            throw new InvalidEmailException(Constants.EMAIL_INVALID);
        }
    }

    public static boolean isValidFileExtension(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return Constants.FILE_EXTENSION.contains(extension);
    }
}
