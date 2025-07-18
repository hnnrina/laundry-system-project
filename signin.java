package student;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import org.json.*;

public class signin {

    private JFrame frame;
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhone;
    private JTextField textAddress;
    private JPasswordField passwordField;
    private boolean isPasswordVisible = false;
    private JPanel mainPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                signin window = new signin();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public signin() {
        initialize();
    }

    private void initialize() {
        createMainFrame();
        setupBackground();
        addTitle();
        addNameField();
        addEmailField();
        addPasswordField();
        addPhoneField();
        addAddressField();
        addSignInButton();
        addLoginButton();
    }

    private void createMainFrame() {
        frame = new JFrame("Student Sign Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/background 3.png"));
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(255, 240, 245)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(null);
        frame.setContentPane(mainPanel);
    }

    private void setupBackground() {
        // Background handled in paintComponent
    }

    private void addTitle() {
    }

    private void addNameField() {
        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        lblName.setBounds(134, 170, 100, 25);
        mainPanel.add(lblName);

        textName = new JTextField();
        textName.setBounds(244, 170, 350, 30);
        mainPanel.add(textName);
    }

    private void addEmailField() {
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 16));
        lblEmail.setBounds(150, 226, 100, 25);
        mainPanel.add(lblEmail);

        textEmail = new JTextField();
        textEmail.setBounds(244, 226, 350, 30);
        mainPanel.add(textEmail);
    }

    private void addPasswordField() {
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setBounds(134, 284, 100, 25);
        mainPanel.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setEchoChar('•');
        passwordField.setBounds(244, 284, 350, 30);
        mainPanel.add(passwordField);

        JButton btnToggle = new JButton("Show");
        btnToggle.setBounds(625, 283, 80, 30);
        btnToggle.addActionListener(e -> togglePasswordVisibility(btnToggle));
        mainPanel.add(btnToggle);
    }

    private void togglePasswordVisibility(JButton btn) {
        isPasswordVisible = !isPasswordVisible;
        passwordField.setEchoChar(isPasswordVisible ? (char) 0 : '•');
        btn.setText(isPasswordVisible ? "Hide" : "Show");
    }

    private void addPhoneField() {
        JLabel lblPhone = new JLabel("Phone No:");
        lblPhone.setFont(new Font("Arial", Font.BOLD, 16));
        lblPhone.setBounds(134, 338, 100, 25);
        mainPanel.add(lblPhone);

        textPhone = new JTextField();
        textPhone.setBounds(244, 338, 350, 30);
        mainPanel.add(textPhone);
    }

    private void addAddressField() {
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(new Font("Arial", Font.BOLD, 16));
        lblAddress.setBounds(134, 393, 100, 25);
        mainPanel.add(lblAddress);

        textAddress = new JTextField();
        textAddress.setBounds(244, 393, 350, 30);
        mainPanel.add(textAddress);
    }

    private void addSignInButton() {
        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setFont(new Font("Arial", Font.BOLD, 16));
        btnSignUp.setBackground(new Color(70, 130, 180));
        btnSignUp.setForeground(Color.WHITE);
        btnSignUp.setBounds(312, 447, 150, 35);
        btnSignUp.addActionListener(e -> handleSignUp());
        mainPanel.add(btnSignUp);
    }

    private void handleSignUp() {
        String name = textName.getText().trim();
        String email = textEmail.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String phone = textPhone.getText().trim();
        String address = textAddress.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields");
            return;
        }

        new Thread(() -> {
            try {
                JSONObject response = sendSignUpRequest(name, email, password, phone, address);
                SwingUtilities.invokeLater(() -> processSignUpResponse(response));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage())
                );
            }
        }).start();
    }

    private JSONObject sendSignUpRequest(String name, String email, String password, 
                                       String phone, String address) throws Exception {
        URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/signup.php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        String params = String.format("name=%s&email=%s&password=%s&phone=%s&address=%s",
                URLEncoder.encode(name, "UTF-8"),
                URLEncoder.encode(email, "UTF-8"), 
                URLEncoder.encode(password, "UTF-8"),
                URLEncoder.encode(phone, "UTF-8"), 
                URLEncoder.encode(address, "UTF-8"));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes());
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    private void processSignUpResponse(JSONObject json) {
        try {
            if (json.getString("status").equalsIgnoreCase("success")) {
                int userId = json.getInt("user_id");
                JOptionPane.showMessageDialog(frame, "Registration successful!");
                frame.dispose(); 
                new login().showFrame(); 
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Error: " + json.optString("error", "Registration failed"));
            }
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(frame, "Invalid server response");
        }
    }

    private void addLoginButton() {
        JButton btnLogin = new JButton("Already have an account? Login");
        btnLogin.setFont(new Font("Arial", Font.ITALIC, 14));
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setForeground(Color.BLUE);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(237, 504, 300, 25);
        btnLogin.addActionListener(e -> {
            frame.dispose();
            new login().showFrame();
        });
        mainPanel.add(btnLogin);
    }
}