package com.mycompany.shop.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;


@ApplicationScoped
@Sha256
public class Sha256HashGenerator implements HashGenerator {

    @Override
    public String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String base = Base64.getEncoder().encodeToString(hash);
            return String.valueOf(base);
        } catch (NoSuchAlgorithmException nsae) {
            return "";
        }
    }

}
