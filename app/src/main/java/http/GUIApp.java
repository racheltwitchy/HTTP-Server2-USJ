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
    private JComboBox<String> methodComboBox;
    private JTextField serverField;
    private JTextField portField;
    private JTextField pathField;
    private JTextArea headersArea;
    private JTextArea bodyArea;
    private JTextArea responseArea;
    private JLabel bodyInstructionsLabel;

    public GUIApp() {
        super("HTTP Client GUI");

        // Layout setup
        setLayout(new BorderLayout());

        // Request configuration panel
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));

        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodComboBox = new JComboBox<>(new String[] { "GET", "HEAD", "POST", "PUT", "DELETE" });
        methodPanel.add(new JLabel("Method:"));
        methodPanel.add(methodComboBox);
        requestPanel.add(methodPanel);

        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serverField = new JTextField("localhost", 20);
        serverPanel.add(new JLabel("Server:"));
        serverPanel.add(serverField);
        requestPanel.add(serverPanel);

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portField = new JTextField("80", 20);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        requestPanel.add(portPanel);

        JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pathField = new JTextField("/", 20);
        pathPanel.add(new JLabel("Path:"));
        pathPanel.add(pathField);
        requestPanel.add(pathPanel);

        JPanel headersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headersArea = new JTextArea(5, 20);
        headersPanel.add(new JLabel("Headers (name:value):"));
        headersPanel.add(new JScrollPane(headersArea));
        requestPanel.add(headersPanel);

        JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bodyArea = new JTextArea(5, 20);
        bodyPanel.add(new JLabel("Body:"));
        bodyPanel.add(new JScrollPane(bodyArea));
        requestPanel.add(bodyPanel);

        bodyInstructionsLabel = new JLabel(
                "<html>INSTRUCTIONS:<br>For POST provide the car details separated by commas (e.g., 'Toyota,Corolla,2015,20000').<br>"
                        +
                        "For PUT provide car details to modify by an index separated by commas (e.g., '1,Toyota,Corolla,2015,20000').<br>"
                        +
                        "For DELETE type the index of the car.<br>" +
                        "For GET & HEAD type body if it is necessary.</html>");
        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionsPanel.add(bodyInstructionsLabel);
        requestPanel.add(instructionsPanel);

        // Response area
        responseArea = new JTextArea(10, 40);
        responseArea.setEditable(false);

        // Send button
        JButton sendButton = new JButton("Send Request");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest();
            }
        });

        add(requestPanel, BorderLayout.NORTH);
        add(new JScrollPane(responseArea), BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setVisible(true);
    }

    private void sendRequest() {
        String method = (String) methodComboBox.getSelectedItem();
        String server = serverField.getText();
        int port = Integer.parseInt(portField.getText());
        String path = pathField.getText();
        Map<String, String> headers = new HashMap<>();
        for (String line : headersArea.getText().split("\\n")) {
            String[] parts = line.split(":");
            if (parts.length > 1) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }
        String body = " " + bodyArea.getText();

        // Create and configure the request
        Request request = new Request(server, port);
        request.setMethod(method);
        request.setPath(path);

        ArrayList<String> validMethods = new ArrayList<>();
        validMethods.add("GET");
        validMethods.add("HEAD");
        validMethods.add("EXIT");
        // Check if the method is valid
        if (path.contains("/static")) {
            System.out.println("Type the HTTP method you want to use (GET, HEAD, EXIT): ");
        } else {
            validMethods.add("PUT");
            validMethods.add("POST");
            validMethods.add("DELETE");
            System.out.println("Type the HTTP method you want to use (GET, HEAD, PUT, POST, DELETE, EXIT): ");
        }

        if (!validMethods.contains(method)) {
            responseArea.setText(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
            System.out.println(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
        } else {
            headers.forEach(request::addHeader);
            System.out.println("LOS HEADERS SON: " + request.getHeaders().toString());
            request.setBody(body);
            System.out.println("EL BODY ES: " + request.getBody());
            // Send the request and capture the response
            String response = request.send();
            responseArea.setText(response);
        }
    }

    public static void main(String[] args) {
        new GUIApp();
    }
}
