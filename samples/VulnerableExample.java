import java.sql.*;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;

public class VulnerableExample {

    private static String password = "admin123"; // hardcoded credential

    public void login(HttpServletRequest request) throws Exception {

        String user = request.getParameter("user");
        String pass = request.getParameter("pass");

        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test", "root", password);

        Statement stmt = conn.createStatement();

        // SQL Injection vulnerability
        String query = "SELECT * FROM users WHERE username = '" + user + "'";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            System.out.println("Logged in!");
        }

        // Weak crypto
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(pass.getBytes());
    }
}