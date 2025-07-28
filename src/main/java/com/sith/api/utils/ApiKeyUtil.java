package com.sith.api.utils;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class ApiKeyUtil {
    private static final int API_KEY_LENGTH = 32;

    public String generateRawApiKey() {
        byte[] randomBytes = new byte[API_KEY_LENGTH];

        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e){
            secureRandom = new SecureRandom();
        }

        secureRandom.nextBytes(randomBytes);

        return Hex.encodeHexString(randomBytes);
    }

    public String hashApiKey(String rawKey) {
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(rawKey);
    }
}
