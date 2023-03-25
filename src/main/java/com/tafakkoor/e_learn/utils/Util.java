package com.tafakkoor.e_learn.utils;

import com.google.gson.Gson;
import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Token;
import com.tafakkoor.e_learn.enums.Levels;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

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

    public String generateUniqueName(@NonNull String fileName) {
        return UUID.randomUUID() + "." + StringUtils.getFilenameExtension(fileName);
    }

    public String generateBody(String username, String token) {
        String link = Container.BASE_URL + "auth/activate?token=" + token;
        return """                      
                <h1>Dear %s,</h1>          
                <p>                                
                Thank you for registering on our website. To activate your account, please click on Activate:
                The link will be valid for 10 minutes. After that, you will need to register again.
                </p>
                <br>
                                
                <a href="%s">Activate</a>
                
                <br>
                <br>
                <p>
                If the link does not work, please copy and paste the following link in your browser:
                %s
                </p>
                <br>
                <p>             
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                </p>
                
                <br>
                <br>
                <br>
                <strong>Best regards,</strong>
                <br>
                <strong>E-Learn LTD.</strong>
                """.formatted(username, link, link);
    }


    public String generateBodyForInactiveUsers(String username) {
        return """                                
                <h1>Dear %s,</h1>
                
                <p>                
                I has been 3 days since you last logged in to your account. We hope you are doing well and enjoying our services.
                To really improve your learning experience, we recommend you to login to your account and continue learning process.
                As a reminder, you can login to your account by clicking on the following link:
                </p>
                <br>
                
                <a href="http://localhost:8080/auth/login">Login</a>
                
                <br>
                <br>
                <p>                                                
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                </p>
                   
                <br> 
                <br>
                <br>
                <strong>Best regards,</strong>
                <br>
                <strong>E-Learn LTD.</strong>
                """.formatted(username);
    }


    public String generateBodyForBirthDay(String username) {
        return """    
                <h1>Dear %s,</h1>                            
                <p>                                
                We wish you a very happy birthday. We hope you have a wonderful day and a great year ahead.
                We are very grateful to have you as a member of our community. We hope you will continue to enjoy our services.
                </p>
                <br>
                <p>            
                If you have any questions or need assistance, please contact us at [SUPPORT_EMAIL OR TELEGRAM_BOT].
                </p>
                
                <br>
                <br>
                <br>
                <strong>Best regards,</strong>
                <br>
                <strong>E-Learn LTD.</strong>
                """.formatted(username);
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
