package Staff;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import org.json.*;

public class staffLogin {

    private JFrame frame;
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private boolean isPasswordVisible = false;
    private JPanel mainPanel;
    private JPanel formPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new staffLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public staffLogin() {
        initialize();
        frame.setVisible(true);
    }

    private void initialize() {
        createMainFrame();
        setupBackground();
        setupFormPanel();
        addTitle();
        addUsernameComponents();
        addPasswordComponents();
        addLoginButton();
    }

    private void createMainFrame() {
        frame = new JFrame("Staff Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // Center the window
        
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw background image if available
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/background 1.jpg"));
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    // Fallback to solid color if image not found
                    g.setColor(new Color(240, 248, 255)); // AliceBlue
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

    private void addUsernameComponents() {
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.BLACK);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 16));
        lblUsername.setBounds(254, 328, 100, 25);
        formPanel.add(lblUsername);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(364, 328, 250, 30);
        formPanel.add(textFieldUsername);
    }

    private void addPasswordComponents() {
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setBounds(254, 394, 100, 25);
        formPanel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(364, 394, 250, 30);
        formPanel.add(passwordField);

        JButton btnTogglePassword = new JButton("Show");
        btnTogglePassword.setFont(new Font("Arial", Font.PLAIN, 12));
        btnTogglePassword.setBounds(624, 393, 80, 30);
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
        btnLogIn.setBounds(420, 479, 143, 35);
        formPanel.add(btnLogIn);

        btnLogIn.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        Thread loginThread = new Thread(() -> {
            try {
                String username = textFieldUsername.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Please enter both username and password")
                    );
                    return;
                }

                JSONObject response = sendLoginRequest(username, password);
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

    private JSONObject sendLoginRequest(String username, String password) throws Exception {
        URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/staff_login.php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("username", username);
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
            System.out.println("Raw response: " + response.toString());
            return new JSONObject(response.toString());
        }
    }

    private void processLoginResponse(JSONObject jsonResponse) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (jsonResponse.getBoolean("success")) {
                    int staffId = jsonResponse.getInt("staff_id");
                    staffBooking bookingPage = new staffBooking(staffId);
                    bookingPage.frame.setVisible(true);
                    frame.dispose();
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