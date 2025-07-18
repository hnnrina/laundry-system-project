package Staff;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class staffSignin {

    public JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton toggleButton;
    private boolean passwordVisible = false;
    private JPanel contentPane; // Main content panel

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                staffSignin window = new staffSignin();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public staffSignin() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 744, 515);
        
        // Create layered structure for background and components
        contentPane = new JPanel();
        contentPane.setLayout(null); // Using null layout for absolute positioning
        frame.setContentPane(contentPane);

        // Add background image
        ImageIcon icon = new ImageIcon(getClass().getResource("/background 1.jpg"));
        Image img = icon.getImage().getScaledInstance(744, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        // Create a panel for all components (easier to move together)
        JPanel componentPanel = new JPanel();
        componentPanel.setLayout(null);
        componentPanel.setOpaque(false); // Make it transparent
        componentPanel.setBounds(0, 0, 744, 515);
        contentPane.add(componentPanel);

        // Now add all your components to componentPanel instead of directly to contentPane
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.BLACK);
        lblUsername.setFont(new Font("Ravie", Font.PLAIN, 16));
        lblUsername.setBounds(225, 276, 121, 31);
        componentPanel.add(lblUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setFont(new Font("Ravie", Font.PLAIN, 16));
        lblPassword.setBounds(225, 345, 121, 31);
        componentPanel.add(lblPassword);

        usernameField = new JTextField();
        usernameField.setBounds(356, 277, 212, 25); // Increased height for better visibility
        componentPanel.add(usernameField);
        usernameField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(356, 346, 212, 25); // Increased height for better visibility
        componentPanel.add(passwordField);

        toggleButton = new JButton("Show");
        toggleButton.setBounds(594, 343, 79, 30); // Adjusted to match new height
        componentPanel.add(toggleButton);

        toggleButton.addActionListener(e -> {
            if (passwordVisible) {
                passwordField.setEchoChar('●');
                toggleButton.setText("Show");
            } else {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("Hide");
            }
            passwordVisible = !passwordVisible;
        });

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        signUpButton.setBounds(516, 409, 96, 31);
        componentPanel.add(signUpButton);

        signUpButton.addActionListener(e -> registerStaff());

        JButton loginInsteadButton = new JButton("Login Instead?");
        loginInsteadButton.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        loginInsteadButton.setBounds(269, 409, 150, 31);
        componentPanel.add(loginInsteadButton);
        JLabel background = new JLabel(scaledIcon);
        background.setBounds(-10, 10, 744, 515);
        componentPanel.add(background);

        loginInsteadButton.addActionListener(e -> {
            frame.dispose();
            new staffLogin();
        });
    }

    private void registerStaff() {
        Thread registerThread = new Thread(() -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(frame, "Please fill in all fields.")
                    );
                    return;
                }

                String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

                URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/staff_signup.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                SwingUtilities.invokeLater(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        JOptionPane.showMessageDialog(frame, "Staff registered successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration failed. Error code: " + responseCode);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(frame, "An error occurred: " + e.getMessage())
                );
            }
        });

        registerThread.start();
    }
}