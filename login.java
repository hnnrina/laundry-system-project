package student;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import org.json.*;

public class login {

    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean isPasswordVisible = false;
    private JPanel mainPanel;
    private JPanel formPanel;

    public void showFrame() {
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public login() {
        initialize();
        frame.setVisible(true);
    }

    private void initialize() {
        createMainFrame();
        setupBackground();
        setupFormPanel();
        addTitle();
        addEmailComponents();
        addPasswordComponents();
        addLoginButton();
    }

    private void createMainFrame() {
        frame = new JFrame("Student Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); 
        
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/background 1.png"));
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    
                    g.setColor(new Color(255, 240, 245)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        frame.setContentPane(mainPanel);
    }

    private void setupBackground() {
        // Background is handled in the mainPanel's paintComponent
    }

    private void setupFormPanel() {
        formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setOpaque(false); // Transparent
        formPanel.setBounds(150, 100, 500, 400);
        mainPanel.add(formPanel);
    }

    private void addTitle() {
    }

    private void addEmailComponents() {
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.BLACK);
        lblEmail.setFont(new Font("Arial", Font.BOLD, 16));
        lblEmail.setBounds(243, 308, 100, 25);
        formPanel.add(lblEmail);

        emailField = new JTextField();
        emailField.setBounds(349, 308, 250, 30);
        formPanel.add(emailField);
    }

    private void addPasswordComponents() {
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setBounds(243, 398, 100, 25);
        formPanel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setEchoChar('•');
        passwordField.setBounds(349, 398, 250, 30);
        formPanel.add(passwordField);

        JButton btnTogglePassword = new JButton("Show");
        btnTogglePassword.setFont(new Font("Arial", Font.PLAIN, 12));
        btnTogglePassword.setBounds(636, 397, 80, 30);
        btnTogglePassword.setBackground(new Color(220, 220, 220));
        formPanel.add(btnTogglePassword);

        btnTogglePassword.addActionListener(e -> togglePasswordVisibility(btnTogglePassword));
    }

    private void togglePasswordVisibility(JButton toggleButton) {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '•');
        toggleButton.setText(isPasswordVisible ? "Hide" : "Show");
    }

    private void addLoginButton() {
        JButton btnLogIn = new JButton("Log In");
        btnLogIn.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogIn.setBackground(new Color(70, 130, 180)); // SteelBlue
        btnLogIn.setForeground(Color.WHITE);
        btnLogIn.setBounds(396, 478, 150, 35);
        formPanel.add(btnLogIn);

        btnLogIn.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        Thread loginThread = new Thread(() -> {
            try {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (email.isEmpty() || password.isEmpty()) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Please enter both email and password")
                    );
                    return;
                }

                JSONObject response = sendLoginRequest(email, password);
                processLoginResponse(response);

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(frame, "Login failed due to an error: " + ex.getMessage())
                );
            }
        });
        loginThread.start();
    }

    private JSONObject sendLoginRequest(String email, String password) throws Exception {
        URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/login.php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            
            return new JSONObject(response.toString());
        }
    }

    private void processLoginResponse(JSONObject jsonResponse) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (jsonResponse.getBoolean("success")) {
                    int userId = jsonResponse.getInt("id");
                    String token = jsonResponse.optString("token", "");

                    SessionManager.setUserId(userId);
                    SessionManager.setJwtToken(token);

                    frame.dispose(); // Close login window
                    new booking(userId).showFrame(); // Or pass token if needed

                } else {
                    JOptionPane.showMessageDialog(frame,
                        "Login failed: " + jsonResponse.optString("message", "Invalid credentials"));
                }
            } catch (JSONException e) {
                JOptionPane.showMessageDialog(frame, "Error processing login response");
            }
        });
    }

}