import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Response {

    public Response() {
        // TODO Auto-generated constructor stub
    }
    
    public String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }   

    public String getResponse() {
        String r = "";
        return r;
    }

}
