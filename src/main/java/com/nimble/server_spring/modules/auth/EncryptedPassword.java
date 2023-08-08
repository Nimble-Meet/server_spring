package com.nimble.server_spring.modules.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

@Getter
public class EncryptedPassword {
    private final String password;

    private EncryptedPassword(String password) {
        this.password = password;
    }

    static EncryptedPassword from(String hashedPassword) {
        return new EncryptedPassword(hashedPassword);
    }

    static EncryptedPassword encryptFrom(String plainPassword) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return new EncryptedPassword(hashedPassword);
    }

    boolean equals(String plainPassword) {
        return BCrypt.checkpw(plainPassword, password);
    }
}
