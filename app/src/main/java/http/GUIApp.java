package http;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import http.server.usj.Request;
import http.server.usj.ServerStatus;
import java.util.ArrayList;

public class GUIApp extends JFrame {
    // GUI components
    private JComboBox<String> methodComboBox;
    private JTextField serverField;
    private JTextField portField;
    private JTextField pathField;
    private JTextArea headersArea;
    private JTextArea bodyArea;
    private JTextArea responseArea;
    private JLabel bodyInstructionsLabel;

    // Constructor to set up the GUI
    public GUIApp() {
        super("HTTP Client GUI");

        // Set the layout of the main frame
        setLayout(new BorderLayout());

        // Create and configure the request panel
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));

        // Method selection panel
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodComboBox = new JComboBox<>(new String[] { "GET", "HEAD", "POST", "PUT", "DELETE" });
        methodPanel.add(new JLabel("Method:"));
        methodPanel.add(methodComboBox);
        requestPanel.add(methodPanel);

        // Server input panel
        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverField = new JTextField("localhost", 20);
        serverPanel.add(new JLabel("Server:"));
        serverPanel.add(serverField);
        requestPanel.add(serverPanel);

        // Port input panel
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portField = new JTextField("80", 20);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        requestPanel.add(portPanel);

        // Path input panel
        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathField = new JTextField("/", 20);
        pathPanel.add(new JLabel("Path:"));
        pathPanel.add(pathField);
        requestPanel.add(pathPanel);

        // Headers input panel
        JPanel headersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headersArea = new JTextArea(5, 20);
        headersPanel.add(new JLabel("Headers (name:value):"));
        headersPanel.add(new JScrollPane(headersArea));
        requestPanel.add(headersPanel);

        // Body input panel
        JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bodyArea = new JTextArea(5, 20);
        bodyPanel.add(new JLabel("Body:"));
        bodyPanel.add(new JScrollPane(bodyArea));
        requestPanel.add(bodyPanel);

        // Instructions for using the body field
        bodyInstructionsLabel = new JLabel(
                "<html>INSTRUCTIONS:<br>For POST provide the car details separated by commas (e.g., 'Toyota,Corolla,2015,20000').<br>"
                        + "For PUT provide car details to modify by an index separated by commas (e.g., '1,Toyota,Corolla,2015,20000').<br>"
                        + "For DELETE type the index of the car.<br>"
                        + "For GET & HEAD type body if it is necessary.</html>");
        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionsPanel.add(bodyInstructionsLabel);
        requestPanel.add(instructionsPanel);

        // Response area to display server responses
        responseArea = new JTextArea(10, 40);
        responseArea.setEditable(false);

        // Send button to trigger the request
        JButton sendButton = new JButton("Send Request");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest();
            }
        });

        // Add components to the main frame
        add(requestPanel, BorderLayout.NORTH);
        add(new JScrollPane(responseArea), BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);

        // Set default close operation and frame size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setVisible(true);
    }

    // Method to send the HTTP request
    private void sendRequest() {
        String method = (String) methodComboBox.getSelectedItem(); // Get selected HTTP method
        String server = serverField.getText(); // Get server address
        int port = Integer.parseInt(portField.getText()); // Get port number
        String path = pathField.getText(); // Get request path
        Map<String, String> headers = new HashMap<>(); // Create a map to store headers
        for (String line : headersArea.getText().split("\\n")) { // Split headers input by lines
            String[] parts = line.split(":");
            if (parts.length > 1) {
                headers.put(parts[0].trim(), parts[1].trim()); // Add headers to the map
            }
        }
        String body = bodyArea.getText(); // Get request body

        // Create and configure the request
        Request request = new Request(server, port);
        request.setMethod(method);
        request.setPath(path);

        // List of valid HTTP methods
        ArrayList<String> validMethods = new ArrayList<>();
        validMethods.add("GET");
        validMethods.add("HEAD");
        validMethods.add("EXIT");

        // Check if the method is valid for the given path
        if (path.contains("/static")) {
            System.out.println("Type the HTTP method you want to use (GET, HEAD, EXIT): ");
        } else {
            validMethods.add("PUT");
            validMethods.add("POST");
            validMethods.add("DELETE");
            System.out.println("Type the HTTP method you want to use (GET, HEAD, PUT, POST, DELETE, EXIT): ");
        }

        // Validate the selected method
        if (!validMethods.contains(method)) {
            responseArea.setText(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
            System.out.println(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
        } else {
            // Add headers to the request
            headers.forEach(request::addHeader);
            System.out.println("THE HEADERS ARE: " + request.getHeaders().toString());

            // Set the request body
            request.setBody(body);
            System.out.println("THE BODY IS: " + request.getBody());

            // Send the request and capture the response
            String response = request.send();
            responseArea.setText(response); // Display the response in the response area
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        new GUIApp(); // Create and display the GUI
    }
}
