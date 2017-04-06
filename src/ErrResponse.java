
public class ErrResponse extends Response {

    private int code;
    private String ms1;
    private String ms2;
    private String mime = "text/html";

    public ErrResponse(int code, String str) {
        // TODO Auto-generated constructor stub
        this.code = code;
        if (code == 404) {
            ms1 = "Not Found.";
            ms2 = "404 = Nothing matches the given URI.";
        } else if (code == 500) {
            ms1 = "Internal Server Error.";
            ms2 = "500 = Server got itself in trouble.";
        } else if (code == 501) {
            ms1 = "Unsupported method ('" + str + "').";
            ms2 = "501 = Server does not support this operation.";
        } else if (code == 505) {
            ms1 = "HTTP Version not supported.";
            ms2 = "505 = Cannot fulfill request..";
        }
    }

    public String getResponse() {
        String r = "HTTP/1.0 " + code + " " + ms1 + "\r\nServer: Simple/1.0\r\nDate: " + super.getTime()
                + "\r\nContent-Type: " + mime
                + "\r\nConnection: close\r\n\r\n<head>\n<title>Error response</title>\n</head>\n<body>\n<h1>Error response</h1>\n<p>Error code "
                + Integer.toString(code) + ".\n<p>Message: " + ms1 + "\n<p>Error code explanation: " + ms2
                + "\n</body>\n";
        // build body
        return r;
    }

}
