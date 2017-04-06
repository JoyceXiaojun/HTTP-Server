import java.io.StringReader;

public class Chunked {


    public Chunked() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String args[]) {
        
        long length = 0;

        // testsuit
        String test = "5\r\nabcde\r\n1f\r\naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n0\r\n";
        StringReader sr = new StringReader(test);
        char cbuf[] = test.toCharArray();

        System.out.println("HTTP/1.1 200 OK");
        System.out.println("Content-Type: text/plain");
        System.out.println("Transfer-Encoding: chunked");
        System.out.println();
        length = chunkedDecoding(cbuf);
        System.out.println();
        System.out.println("0");
        System.out.println();
        System.out.println();
        System.out.println(length);

    }

    private static long chunkedDecoding(char[] data) {
        long length = 0;
        long chunksize = 1;
        String content;
        boolean flag = true;
        int i = 0;
        StringBuilder sizesb = new StringBuilder();
        StringBuilder contentsb = new StringBuilder();
        while (chunksize > 0) {
            
            if (data[i] == '\r') {
                //System.out.println(flag);
                if (flag) {
                    chunksize = Long.valueOf(sizesb.toString(), 16);
                    contentsb = new StringBuilder();
                    flag = false;
                }
                else if (!flag) {
                    content = contentsb.toString();
                    flag = true;
                    sizesb = new StringBuilder();
                    System.out.println(chunksize);
                    System.out.println(content);
                    length = length + chunksize;
                }
                i = i + 2;
                continue;
            }
            if (flag) {
                sizesb.append(data[i]);
            }
            if (!flag) {
                contentsb.append(data[i]);
            }
            i++;
        }

        return length;
    }

}
