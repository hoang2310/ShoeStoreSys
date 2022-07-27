package pro1041.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pro1041.entity.Employee;
import pro1041.entity.Role;

public class Auth {

    private static Employee user;
    
    private static final String SALT = "SUGAR";
    private static final String ALGORITHM = "SHA-512";
    
    
    public static void clear() {
        user = null;
    }

    public static boolean isLogin() {
        return user != null;
    }

    public static boolean isManager() {
        return isLogin() && user.getRole() == Role.MANAGER;
    }

    public static Employee getUser() {
        return user;
    }

    public static void setUser(Employee user) {
        Auth.user = user;
    }
    
    public static boolean matches(String password, String hashedPassword){
        return getSecurePassword(password).equals(hashedPassword);
    }
    
    public static String getSecurePassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(SALT.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedPassword;
    }
}
