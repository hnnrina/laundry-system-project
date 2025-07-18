import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;
import org.json.*;

public class booking {

    private JFrame frame;
    private JTextField laundryWeightTextField;
    private JComboBox<String> serviceComboBox;
    private JComboBox<String> timeSlotComboBox;
    private JTextArea priceTextArea;
    private HashMap<String, Integer> serviceIds = new HashMap<>();
    private JTextField notesTextField;
    private int userId;
    private JPanel mainPanel;

    public void showFrame() {
        frame.setVisible(true);
    }
    
    public booking(int userId) {
        this.userId = userId;
        initialize();
    }

    private void initialize() {
        createMainFrame();
        setupBackground();
        addTitle();
        addServiceSelection();
        addWeightInput();
        addTimeSlotSelection();
        addPriceDisplay();
        addNotesField();
        addActionButtons();
        fetchServicesFromBackend();
    }

    private void createMainFrame() {
        frame = new JFrame("Laundry Booking");
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
                    g.setColor(new Color(255, 240, 245)); // Fallback color
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

    private void addServiceSelection() {
        JLabel lblService = new JLabel("Service:");
        lblService.setFont(new Font("Arial", Font.BOLD, 16));
        lblService.setBounds(202, 194, 100, 25);
        mainPanel.add(lblService);

        serviceComboBox = new JComboBox<>();
        serviceComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        serviceComboBox.setBounds(325, 192, 300, 30);
        mainPanel.add(serviceComboBox);
    }

    private void addWeightInput() {
        JLabel lblWeight = new JLabel("Laundry Weight (kg):");
        lblWeight.setFont(new Font("Arial", Font.BOLD, 16));
        lblWeight.setBounds(109, 247, 193, 25);
        mainPanel.add(lblWeight);

        laundryWeightTextField = new JTextField();
        laundryWeightTextField.setBounds(325, 247, 150, 30);
        laundryWeightTextField.addActionListener(e -> calculatePrice());
        mainPanel.add(laundryWeightTextField);
    }

    private void addTimeSlotSelection() {
        JLabel lblTimeSlot = new JLabel("Pickup Time Slot:");
        lblTimeSlot.setFont(new Font("Arial", Font.BOLD, 16));
        lblTimeSlot.setBounds(135, 303, 150, 25);
        mainPanel.add(lblTimeSlot);

        timeSlotComboBox = new JComboBox<>(new String[] {
            "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM",
            "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM"
        });
        timeSlotComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        timeSlotComboBox.setBounds(325, 301, 150, 30);
        mainPanel.add(timeSlotComboBox);
    }

    private void addPriceDisplay() {
        JLabel lblPrice = new JLabel("Total Price:");
        lblPrice.setFont(new Font("Arial", Font.BOLD, 16));
        lblPrice.setBounds(177, 350, 100, 25);
        mainPanel.add(lblPrice);

        priceTextArea = new JTextArea();
        priceTextArea.setBounds(325, 353, 150, 30);
        priceTextArea.setEditable(false);
        priceTextArea.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(priceTextArea);
    }

    private void addNotesField() {
        JLabel lblNotes = new JLabel("Additional Notes:");
        lblNotes.setFont(new Font("Arial", Font.BOLD, 16));
        lblNotes.setBounds(150, 407, 150, 25);
        mainPanel.add(lblNotes);

        notesTextField = new JTextField();
        notesTextField.setBounds(325, 407, 300, 30);
        mainPanel.add(notesTextField);
    }

    private void addActionButtons() {
        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBounds(135, 483, 120, 35);
        btnBack.addActionListener(e -> {
            frame.dispose();
            new login().showFrame();
        });
        mainPanel.add(btnBack);

        JButton btnConfirm = new JButton("Confirm Booking");
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(70, 130, 180));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setBounds(325, 483, 183, 35);
        btnConfirm.addActionListener(e -> confirmBooking());
        mainPanel.add(btnConfirm);
        
        JButton btnView = new JButton("View Booking");
        btnView.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frame.dispose(); 
        	    new ViewBookingStatus(userId); 
        	}
        });
        btnView.setForeground(Color.BLACK);
        btnView.setFont(new Font("Arial", Font.BOLD, 14));
        btnView.setBackground(Color.PINK);
        btnView.setBounds(559, 483, 183, 35);
        mainPanel.add(btnView);
    }

    private void confirmBooking() {
        String service = (String) serviceComboBox.getSelectedItem();
        String weight = laundryWeightTextField.getText();
        String time = (String) timeSlotComboBox.getSelectedItem();
        String notes = notesTextField.getText();

        if (service == null || weight.isEmpty() || time == null) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields");
            return;
        }

        new Thread(() -> {
            try {
                JSONObject response = createBooking(service, weight, time, notes);
                SwingUtilities.invokeLater(() -> processBookingResponse(response));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage())
                );
            }
        }).start();
    }

    private JSONObject createBooking(String service, String weight, String time, String notes) throws Exception {
        if (!serviceIds.containsKey(service)) {
            throw new Exception("Invalid service selected");
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            throw new Exception("Please enter a valid weight");
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime parsedTime = LocalTime.parse(time.trim(), inputFormatter);
        String formattedTime = parsedTime.format(outputFormatter);
        String timeSlot = LocalDate.now().toString() + " " + formattedTime;

        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("service_id", serviceIds.get(service));
        json.put("weight_kg", weightValue);
        json.put("time_slot", timeSlot);
        json.put("notes", notes);

        URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/create_order.php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + SessionManager.getJwtToken());
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.toString().getBytes());
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

    private void processBookingResponse(JSONObject response) {
        try {
            if (response.getBoolean("success")) {
                JOptionPane.showMessageDialog(frame, "Booking confirmed successfully!");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Error: " + response.optString("error", "Booking failed"));
            }
        } catch (JSONException e) {
            JOptionPane.showMessageDialog(frame, "Invalid server response");
        }
    }

    private void fetchServicesFromBackend() {
        new Thread(() -> {
            try {
                URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/getservice.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + SessionManager.getJwtToken());

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray services = new JSONArray(response.toString());
                    SwingUtilities.invokeLater(() -> {
                        serviceComboBox.removeAllItems();
                        serviceIds.clear();
                        for (int i = 0; i < services.length(); i++) {
                            JSONObject service = null;
							try {
								service = services.getJSONObject(i);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            String name = null;
							try {
								name = service.getString("name");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            int id = 0;
							try {
								id = service.getInt("id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            serviceComboBox.addItem(name);
                            serviceIds.put(name, id);
                        }
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(frame, "Failed to load services: " + e.getMessage())
                );
            }
        }).start();
    }

    private void calculatePrice() {
        String service = (String) serviceComboBox.getSelectedItem();
        String weightText = laundryWeightTextField.getText();

        if (service == null || !serviceIds.containsKey(service) || weightText.isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                double weight = Double.parseDouble(weightText);
                double pricePerKg = fetchPriceFromServer(serviceIds.get(service));
                double totalPrice = weight * pricePerKg;

                SwingUtilities.invokeLater(() -> 
                    priceTextArea.setText(String.format("RM %.2f", totalPrice))
                );
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(frame, "Error calculating price: " + e.getMessage())
                );
            }
        }).start();
    }

    private double fetchPriceFromServer(int serviceId) throws Exception {
        URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/get_price.php?id=" + serviceId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + SessionManager.getJwtToken());


        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject obj = new JSONObject(response.toString());
            if (!obj.has("price_per_kg")) {
                throw new Exception("Price information not found");
            }
            return obj.getDouble("price_per_kg");
        }
    }

    private void resetForm() {
        laundryWeightTextField.setText("");
        notesTextField.setText("");
        priceTextArea.setText("");
        timeSlotComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new booking(1).frame.setVisible(true); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
