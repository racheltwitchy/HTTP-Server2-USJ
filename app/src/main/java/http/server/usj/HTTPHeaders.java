package http.server.usj;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

public class HTTPHeaders {
    List<String> headers;

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

    protected String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    public String toString() {
        StringBuilder headersString = new StringBuilder();
        for (String header : this.headers) {
            headersString.append(header).append("\r\n");
        }
        return headersString.toString();
    }
}
