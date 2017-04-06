
public class NormalResponse extends Response {
    
    private long length;
    private String mime;

    public NormalResponse(long length, String mime) {
        // TODO Auto-generated constructor stub
        this.length = length;
        this.mime = mime;
    }

    public String getResponse(String text) {
        String r = "HTTP/1.0 " + "200" + " " + "OK" + "\r\nServer: Simple/1.0\r\nDate: " + super.getTime()
                + "\r\nContent-Type: " + mime + "\r\nContent-Length: " + length + "\r\nConnection: close\r\n\r\n";
        if (text != null) {
            r += text;
        }
        // build body
        return r;
    }

}
