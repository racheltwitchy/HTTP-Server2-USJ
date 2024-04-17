package http.server.usj;

import java.util.List;

public class HTTPHeaders {
    List<String> headers;
    String headerName;

    public HTTPHeaders(String headerName) {
        this.headerName = headerName;
    }

    public String getHeader() {
        return headerName;
    }

    public void addHeaderToHeaders(HTTPHeaders headerType, String headerValue) {
        String header = headerType.getHeader() + ": " + headerValue;
        this.headers.add(header);
    }

    public void addHeaderToHeaders(String headerType, String headerValue) {
        String header = headerType + ": " + headerValue;
        this.headers.add(header);
    }

    public String getValue(HTTPHeaders headerType) {
        for (String header : this.headers) {
            String[] parts = header.split(": ", 2);
            if (parts[0].equals(headerType.getHeader())) {
                return parts[1]; // Value of the header type found
            }
        }
        throw new IllegalArgumentException("The header " + headerType + " is not found in HttpHeaders: ");
    }

    public void setValue(HTTPHeaders headerType, String headerValue) {
        for (int i = 0; i < this.headers.size(); i++) {
            String[] parts = this.headers.get(i).split(": ", 2);
            if (parts[0].equals(headerType.toString())) {
                this.headers.set(i, headerType.toString() + ": " + headerValue);
                return;
            }
        }
        throw new IllegalArgumentException("The header " + headerType + " is not found in HttpHeaders: ");
    }

    public String toString() {
        StringBuilder headersString = new StringBuilder();
        for (String header : this.headers) {
            headersString.append(header).append("\r\n");
        }
        return headersString.toString();
    }
}