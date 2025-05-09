package com.project.digitalidentityvault.util;

import java.util.Random;

public class OtpGenerator {
    static Random random = new Random();

    public static String generateOtp(){
        return String.valueOf(10000 + random.nextInt(89999));
    }
}
