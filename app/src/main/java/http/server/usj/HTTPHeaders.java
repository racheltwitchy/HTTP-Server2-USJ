package http.server.usj;

import java.util.List;

public class HTTPHeaders {
    // List to store the headers as strings
    List<String> headers;
    // Name of the header
    String headerName;

    // Constructor to initialize the HTTPHeaders object with a header name
    public HTTPHeaders(String headerName) {
        this.headerName = headerName;
    }

    // Method to get the name of the header
    public String getHeader() {
        return headerName;
    }

    // Method to add a header to the headers list using an HTTPHeaders object and a
    // header value
    public void addHeaderToHeaders(HTTPHeaders headerType, String headerValue) {
        // Combine the header type and value into a single string
        String header = headerType.getHeader() + ": " + headerValue;
        // Add the header to the list
        this.headers.add(header);
    }

    // Method to add a header to the headers list using a header type string and a
    // header value
    public void addHeaderToHeaders(String headerType, String headerValue) {
        // Combine the header type and value into a single string
        String header = headerType + ": " + headerValue;
        // Add the header to the list
        this.headers.add(header);
    }

    // Method to get the value of a specific header
    public String getValue(HTTPHeaders headerType) {
        // Iterate over the list of headers
        for (String header : this.headers) {
            // Split the header into type and value parts
            String[] parts = header.split(": ", 2);
            // Check if the header type matches the requested type
            if (parts[0].equals(headerType.getHeader())) {
                return parts[1]; // Return the value of the header
            }
        }
        // Throw an exception if the header is not found
        throw new IllegalArgumentException("The header " + headerType + " is not found in HttpHeaders: ");
    }

    // Method to set the value of a specific header
    public void setValue(HTTPHeaders headerType, String headerValue) {
        // Iterate over the list of headers
        for (int i = 0; i < this.headers.size(); i++) {
            // Split the header into type and value parts
            String[] parts = this.headers.get(i).split(": ", 2);
            // Check if the header type matches the requested type
            if (parts[0].equals(headerType.toString())) {
                // Set the new value for the header
                this.headers.set(i, headerType.toString() + ": " + headerValue);
                return;
            }
        }
        // Throw an exception if the header is not found
        throw new IllegalArgumentException("The header " + headerType + " is not found in HttpHeaders: ");
    }

    // Override the toString method to provide a string representation of the
    // headers
    @Override
    public String toString() {
        // Use a StringBuilder to build the string representation of the headers
        StringBuilder headersString = new StringBuilder();
        // Append each header to the string with a newline
        for (String header : this.headers) {
            headersString.append(header).append("\r\n");
        }
        // Return the final string
        return headersString.toString();
    }
}
