package com.tafakkoor.e_learn.utils;

import com.google.gson.Gson;
import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Token;
import com.tafakkoor.e_learn.enums.Levels;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Util {
    private static final ThreadLocal<Util> UTIL_THREAD_LOCAL = ThreadLocal.withInitial(Util::new);
    private static final ThreadLocal<Gson> GSON_THREAD_LOCAL = ThreadLocal.withInitial(Gson::new);

    public Token buildToken(String token, AuthUser authUser) {
        return Token.builder()
                .token(token)
                .user(authUser)
                .validTill(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    public String generateBody(String username, String token) {
        String link = Container.BASE_URL + "auth/activate?token=" + token;
        return """
                Subject: Activate Your Account
                                
                Dear %s,
                                
                Thank you for registering on our website. To activate your account, please click on the following link:
                                
                %s
                                
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                                
                Best regards,
                E-Learn LTD.
                """.formatted(username, link);
    }


    public String generateBodyForInactiveUsers(String username) {
        return """
                Subject: Login to Your Account
                                
                Dear %s,
                                
                // message
                                
                %s
                                
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                                
                Best regards,
                E-Learn LTD.
                """.formatted(username, Container.BASE_URL); // TODO: 13/03/23 write message to users that 3 days inactive
    }


    public String generateBodyForBirthDay(String username) {
        return """
                Subject: Happy Birthday
                                
                Dear %s,
                                
                // message
                                
                %s
                                
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                                
                Best regards,
                E-Learn LTD.
                """.formatted(username, Container.BASE_URL); // TODO: 13/03/23 write message to celebrate birthday
    }

    public BigInteger convertToBigInteger(String number) {
        try {
            double doubleValue = Double.parseDouble(number);
            long longValue = (long) doubleValue;
            if (doubleValue == (double) longValue) {
                return BigInteger.valueOf(longValue);
            } else {
                String[] parts = number.split("E");
                BigInteger coefficient = new BigInteger(parts[0].replace(".", ""));
                BigInteger exponent = new BigInteger(parts[1]);
                return coefficient.multiply(BigInteger.TEN.pow(exponent.intValue()));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input string. Must be in scientific notation.");
        }
    }

    public Levels determineLevel(int score) {
        if (score <= 5) {
            return Levels.BEGINNER;
        } else if (score <= 11) {
            return Levels.ELEMENTARY;
        } else if (score <= 16) {
            return Levels.PRE_INTERMEDIATE;
        } else if (score <= 20) {
            return Levels.INTERMEDIATE;
        } else if (score <= 25) {
            return Levels.UPPER_INTERMEDIATE;
        } else if (score <= 27) {
            return Levels.ADVANCED;
        } else {
            return Levels.PROFICIENCY;
        }
    }

    public Gson getGson() {
        return GSON_THREAD_LOCAL.get();
    }

    public static Util getInstance() {
        return UTIL_THREAD_LOCAL.get();
    }
}
