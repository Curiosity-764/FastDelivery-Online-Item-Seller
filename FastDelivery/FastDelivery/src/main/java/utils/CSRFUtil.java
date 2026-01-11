package utils;

import java.security.SecureRandom;
import java.util.Base64;
import jakarta.servlet.http.HttpSession;

public class CSRFUtil {
    
    private static final String CSRF_TOKEN_NAME = "csrfToken";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();
    
    public static String generateToken() {
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    public static void storeToken(HttpSession session) {
        String token = generateToken();
        session.setAttribute(CSRF_TOKEN_NAME, token);
    }
    
    public static String getToken(HttpSession session) {
        return (String) session.getAttribute(CSRF_TOKEN_NAME);
    }
    
    public static boolean validateToken(HttpSession session, String tokenFromRequest) {
        String storedToken = getToken(session);
        return storedToken != null && storedToken.equals(tokenFromRequest);
    }
    
    public static void removeToken(HttpSession session) {
        session.removeAttribute(CSRF_TOKEN_NAME);
    }
}