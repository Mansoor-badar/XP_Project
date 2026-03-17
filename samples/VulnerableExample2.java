public class VulnerableExample2 {

    public static void main(String[] args) {
        TestExample example = new TestExample();
        example.login("user", "password");
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        if (username.equals("admin") && password.equals("1234")) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Login failed!");
            return false;
        }
    }
}