package student;

public class SessionManager {
    private static String jwtToken;
    private static int userId;

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    public static void setUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }

    public static boolean isAuthenticated() {
        return jwtToken != null && !jwtToken.isEmpty();
    }
}
