import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ViewBookingStatus {

    private JFrame frame;
    private JPanel mainPanel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private int userId;

    public ViewBookingStatus(int userId) {
        this.userId = userId;
        createMainFrame();
        addComponents();
        loadBookingStatus("All");
        frame.setVisible(true);
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
                    g.setColor(new Color(255, 240, 245)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(null);
        frame.setContentPane(mainPanel);
    }

    private void addComponents() {

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setBounds(153, 114, 120, 25);
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(filterLabel);

        filterComboBox = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "On Delivery", "Rejected"});
        filterComboBox.setBounds(290, 115, 180, 25);
        filterComboBox.addActionListener(e -> loadBookingStatus(filterComboBox.getSelectedItem().toString()));
        mainPanel.add(filterComboBox);

        tableModel = new DefaultTableModel(new String[]{"Service", "Weight (kg)", "Time Slot", "Notes", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(101, 149, 600, 315);
        mainPanel.add(scrollPane);

        JButton backButton = new JButton("Back");
        backButton.setBounds(220, 488, 100, 30);
        backButton.addActionListener(e -> {
            frame.dispose();
            booking b = new booking(userId);
            b.showFrame(); 
        });
        mainPanel.add(backButton);
        
        JButton btnLogout = new JButton("Log Out");
        btnLogout.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		frame.dispose();
        	    new signin();
        	}
        });
        btnLogout.setBackground(SystemColor.activeCaption);
        btnLogout.setBounds(494, 488, 100, 30);
        mainPanel.add(btnLogout);
    }

    private void loadBookingStatus(String statusFilter) {
        try {
            URL url = new URL("https://3528573ec504.ngrok-free.app/laundry_system/get_booking_status.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", "Bearer " + SessionManager.getJwtToken());
            conn.setDoOutput(true);

            String data = "user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8") +
                    "&status=" + URLEncoder.encode(statusFilter, "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            
            if (jsonResponse.getBoolean("success")) {
                tableModel.setRowCount(0);

                
                JSONArray bookings = jsonResponse.getJSONArray("orders");

                for (int i = 0; i < bookings.length(); i++) {
                    JSONObject booking = bookings.getJSONObject(i);
                    Vector<String> row = new Vector<>();

                    row.add(booking.optString("name", "-"));       
                    row.add(booking.optString("weight_kg", "-"));
                    row.add(booking.optString("time_slot", "-"));
                    row.add(booking.optString("notes", "-"));
                    row.add(booking.optString("status", "-"));

                    tableModel.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No bookings found.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
