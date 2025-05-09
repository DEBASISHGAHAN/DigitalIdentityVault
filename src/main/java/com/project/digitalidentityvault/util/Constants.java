package com.project.digitalidentityvault.util;

import java.util.List;

public class Constants {

    public static final String EMAIL_INVALID = "Enter Valid Email";
    public static final String INVALID_PASSWORD = "Enter Valid Password";
    public static final String INVALID_OTP =  "Enter Valid OTP";
    public static final String REGISTERED_SUCCESSFULLY = "You Registered Successfully. Please Verify OTP. Sent to Your Email";
    public static final String LOGIN_SUCCESSFULLY = "Login Successfully";
    public static final String DOCUMENT_UPLOAD = "Document Uploaded Successfully";
    public static final String USER_EXISTED = "Email Already Registered!, Please Verify OTP";
    public static final String REGISTERED_SUBJECT = "OTP Verification";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String ACCOUNT_VERIFICATION = "Account Verified Successfully.";
    public static final String VERIFIED_USER = "User is already verified";
    public static final String RESEND_OTP = "OTP resent successfully";
    public static final String USER_LOGOUT = "Logged out successfully";
    public static final Long DOCUMENT_UPLOAD_MAX_SIZE_MB = 10*1024*1024L;
    public static final List<String> FILE_EXTENSION= List.of("png", "jpg", "jpeg", "pdf");
    public static final String FILE_EMPTY = "File is empty. Please re-upload";
    public static final String FILE_UPLOAD_SIZE_EXCEEDS = "Document size should be less than 10 mb";
    public static final String INVALID_FILE_FORMAT = "Invalid file format. Only PNG, JPG, JPEG, PDF are allowed.";
}
