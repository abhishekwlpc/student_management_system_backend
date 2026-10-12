package com.example.student_management_system_backend.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

public class TemporaryPasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()?";

    public static String generate(int length) {
        if(length < 8){
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }

        char[] password = new char[length];

        password[0] = CHARACTERS.charAt(RANDOM.nextInt(26));
        password[1] = CHARACTERS.charAt(26 + RANDOM.nextInt(26));
        password[2] = CHARACTERS.charAt(52 + RANDOM.nextInt(10));
        password[3] = CHARACTERS.charAt(62 + RANDOM.nextInt(CHARACTERS.length() - 62));
        password[4] = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
        password[5] = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
        password[6] = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
        password[7] = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));

        for (int i = 8; i < length; i++) {
            password[i] = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
        }

        for (int i = password.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }
        return new String(password);
    }
}
