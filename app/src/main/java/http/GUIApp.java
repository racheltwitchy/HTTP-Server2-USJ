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

    public GUIApp() {
        super("HTTP Client GUI");

        // Layout setup
        setLayout(new BorderLayout());

        // Request configuration panel
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new GridLayout(6, 2));
        methodComboBox = new JComboBox<>(new String[] { "GET", "HEAD", "POST", "PUT", "DELETE" });
        serverField = new JTextField("localhost");
        portField = new JTextField("80");
        pathField = new JTextField("/");
        headersArea = new JTextArea(5, 20);
        bodyArea = new JTextArea(5, 20);
        requestPanel.add(new JLabel("Method:"));
        requestPanel.add(methodComboBox);
        requestPanel.add(new JLabel("Server:"));
        requestPanel.add(serverField);
        requestPanel.add(new JLabel("Port:"));
        requestPanel.add(portField);
        requestPanel.add(new JLabel("Path:"));
        requestPanel.add(pathField);
        requestPanel.add(new JLabel("Headers (name:value):"));
        requestPanel.add(new JScrollPane(headersArea));
        requestPanel.add(new JLabel("Body:"));
        requestPanel.add(new JScrollPane(bodyArea));

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
        setSize(400, 600);
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
        //System.out.println("LOS HEADERS SON: "+headers.toString());
        String body = bodyArea.getText();
        //System.out.println("EL BODY ES: "+body);

        // Create and configure the request
        Request request = new Request(server, port);
        
        request.setMethod(method);
        request.setPath(path);

        ArrayList<String> validMethods = new ArrayList<String>();
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
                
            }else{  
                request.addHeader(headers);
                System.out.println("LOS HEADERS SON: "+request.getHeaders().toString());
                request.setBody(body);
                System.out.println("EL BODY ES: "+request.getBody()) ;
                 // Send the request and capture the response
                String response = request.send();
                responseArea.setText(response);
            }    
    }

    public static void main(String[] args) {
        new GUIApp();
    }
}
