package Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.json.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class staffBooking {

    public JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private int staffId;
    private JComboBox<String> statusFilterComboBox;
    private JPanel mainPanel;
    private JPanel contentPanel;

    public staffBooking(int staffId) {
        this.staffId = staffId;
        initialize();
        loadBookings("All");
    }

    private void initialize() {
        createMainFrame();
        setupBackground();
        setupContentPanel();
        addTitle();
        addFilterComponents();
        addTable();
        addActionButtons();
    }

    private void createMainFrame() {
        frame = new JFrame("LaundryGo - Staff Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null); // Center the window
    }

    private void setupBackground() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/background 2.jpg"));
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(new Color(240, 248, 255)); // Fallback color
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        frame.setContentPane(mainPanel);
    }

    private void setupContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false); // Transparent
        contentPanel.setBounds(0, 0, 900, 600);
        mainPanel.add(contentPanel);
    }

    private void addTitle() {
    }

    private void addFilterComponents() {
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setForeground(Color.BLACK);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setBounds(173, 97, 166, 25);
        contentPanel.add(filterLabel);

        statusFilterComboBox = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Completed","On Delivery", "Rejected"});
        statusFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        statusFilterComboBox.setBounds(319, 94, 150, 30);
        statusFilterComboBox.setBackground(Color.WHITE);
        contentPanel.add(statusFilterComboBox);

        statusFilterComboBox.addActionListener(e -> {
            String selectedStatus = (String) statusFilterComboBox.getSelectedItem();
            loadBookings(selectedStatus);
        });
    }

    private void addTable() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(50, 140, 800, 300);
        contentPanel.add(scrollPane);

        tableModel = new DefaultTableModel(
            new String[]{"Order ID", "User ID", "Service ID", "Weight", "Time Slot", "Notes", "Status", "Address"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        scrollPane.setViewportView(table);
    }

    private void addActionButtons() {
        JButton updateStatusBtn = createButton("Update Status", 143, 461, 160, 30);
        JButton openMapBtn = createButton("Open in Google Maps", 361, 461, 200, 30);
        JButton logoutBtn = createButton("Logout", 590, 461, 160, 30);
        JButton viewStatusHistoryBtn = createButton("View Status History", 143, 510, 200, 30);
        viewStatusHistoryBtn.addActionListener(e -> showStatusChangeDetails());
        updateStatusBtn.addActionListener(e -> updateSelectedBookingStatus());
        openMapBtn.addActionListener(e -> openGoogleMaps());
        logoutBtn.addActionListener(e -> logout());
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(70, 130, 180)); // SteelBlue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        contentPanel.add(button);
        return button;
    }

    private void loadBookings(String statusFilter) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/get_order.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        System.out.println("Raw response: " + response.toString());
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getBoolean("success")) {
                            JSONArray bookings = jsonResponse.getJSONArray("orders");
                            updateTable(bookings, statusFilter);
                        } else {
                            showError("Failed to load bookings: " + 
                                jsonResponse.optString("message", "Unknown error"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Failed to load bookings.");
                }
                return null;
            }
        };
        worker.execute();
    }

    private void updateTable(JSONArray bookings, String statusFilter) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (int i = 0; i < bookings.length(); i++) {
                try {
                    JSONObject obj = bookings.getJSONObject(i);
                    String status = obj.getString("status");
                    if (statusFilter.equals("All") || status.equalsIgnoreCase(statusFilter)) {
                        tableModel.addRow(new Object[]{
                            obj.getInt("id"),
                            obj.getInt("user_id"),
                            obj.getInt("service_id"),
                            obj.getDouble("weight_kg"),
                            obj.getString("time_slot"),
                            obj.getString("notes"),
                            status,
                            obj.getString("address")
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateSelectedBookingStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a booking first.");
            return;
        }

        int orderId = (int) table.getValueAt(selectedRow, 0);
        String[] statuses = {"Pending", "In Progress", "Completed", "On Delivery", "Rejected"};
        String currentStatus = table.getValueAt(selectedRow, 6).toString();
        
        String selectedStatus = (String) JOptionPane.showInputDialog(
            frame,
            "Select new status:",
            "Update Booking Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            currentStatus
        );

        if (selectedStatus != null && !selectedStatus.equals(currentStatus)) {
            updateStatusInDatabase(orderId, selectedStatus);
        }
    }

    private void updateStatusInDatabase(int orderId, String newStatus) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/update_status.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("id", orderId);
                    json.put("status", newStatus);
                    json.put("staff_id", staffId); // Send staff ID to PHP backend

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

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getBoolean("success")) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(frame, "Status updated successfully!");
                                loadBookings((String) statusFilterComboBox.getSelectedItem());
                            });
                        } else {
                            showError("Failed to update status.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Error occurred while updating status.");
                }
                return null;
            }
        };
        worker.execute();
    }


    private void openGoogleMaps() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a booking first.");
            return;
        }

        String address = table.getValueAt(selectedRow, 7).toString();
        try {
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            Desktop.getDesktop().browse(new URI("https://www.google.com/maps/search/?api=1&query=" + encodedAddress));
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open Google Maps.");
        }
    }
    
    private void showStatusChangeDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an order to view status history.");
            return;
        }

        int orderId = (int) table.getValueAt(selectedRow, 0);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            JSONArray statusChanges;

            @Override
            protected Void doInBackground() {
                try {
                    URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/get_status_change.php?order_id=" + orderId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        statusChanges = jsonResponse.getJSONArray("changes");
                    } else {
                        showError("Failed to retrieve status change history.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error retrieving status change history.");
                }
                return null;
            }

            @Override
            protected void done() {
                if (statusChanges == null || statusChanges.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "No status change history found.");
                    return;
                }

                StringBuilder message = new StringBuilder("Status Change History:\n\n");
                for (int i = 0; i < statusChanges.length(); i++) {
                    try {
                        JSONObject change = statusChanges.getJSONObject(i);
                        message.append(" Status Changed To: ").append(change.getString("new_status"))
                               .append(" | At: ").append(change.getString("changed_at"))
                               .append(" | By: ").append(change.getString("staff_name"))
                               .append("\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));

                JOptionPane.showMessageDialog(frame, scrollPane, "Status Change Details", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }


    private void logout() {
        frame.dispose();
        new staffLogin();
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, message));
    }
}