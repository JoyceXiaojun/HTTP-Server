/**
 * @file: Simple.java
 * 
 * @author: Xiaojun Li 
 * 
 * @date: Feb 28, 2016 1:13:37 AM EST
 * 
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Simple {
    private static ServerSocket srvSock;

    public static void main(String args[]) {
        String buffer = null;
        int port = 8080;
        BufferedReader inStream = null;
        DataOutputStream outStream = null;

        /* Parse parameter and do args checking */
        if (args.length < 2) {
            System.err.println("Usage: java Simple Server <port_number> <www_path>");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.println("Usage: java Simple Server <port_number> <www_path>");
            System.exit(1);
        }

        if (port > 65535 || port < 1024) {
            System.err.println("Port number must be in between 1024 and 65535");
            System.exit(1);
        }

        if (!(new File(args[1])).isDirectory()) {
            System.err.println("The second arg must be a path of folder");
            System.exit(1);
        }
        try {
            /*
             * Create a socket to accept() client connections. This combines
             * socket(), bind() and listen() into one call. Any connection
             * attempts before this are terminated with RST.
             */
            //int host = 1;
            srvSock = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Unable to listen on port " + port);
            System.exit(1);
        }

        while (true) {
            Socket clientSock;
            try {
                /*
                 * Get a sock for further communication with the client. This
                 * socket is sure for this client. Further connections are still
                 * accepted on srvSock
                 */
                clientSock = srvSock.accept();
                System.out.println(
                        "Accpeted new connection from " + clientSock.getInetAddress() + ":" + clientSock.getPort());
            } catch (IOException e) {
                continue;
            }
            try {
                inStream = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                outStream = new DataOutputStream(clientSock.getOutputStream());
                /* Read the data send by the client */
                ArrayList<String> httpRequest = new ArrayList<String>();
                buffer = inStream.readLine();
                while (buffer != null) {
                    if (buffer.length() == 0)
                        break;
                    httpRequest.add(buffer);
                    buffer = inStream.readLine();
                }
                System.out.println(
                        "Read from client " + clientSock.getInetAddress() + ":" + clientSock.getPort() + " " + buffer);

                // Parse the data in request
                String[] firstLine = httpRequest.get(0).split(" ");
                String method = firstLine[0];
                String inpath = firstLine[1];
                String[] versions = (firstLine[2].split("/"))[1].split("\\.");
                boolean flag = versions[0].equals("1") && versions[1].equals("0");
                String response;
                // check the version of HTTP
                if (!flag) {
                    response = doErr(505, null);
                } else {

                    String full_Path = args[1] + inpath;
                    File file = new File(full_Path);
                    if (file.isDirectory()) {
                        file = new File(file, "index.html").getAbsoluteFile();
                    }

                    String fileName = file.getName();
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    String mime = null;
                    try {
                        mime = GetMime.getMimeType(extension.toLowerCase());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (mime == null) {
                        mime = "application/octet-stream";
                    }

                    if (method.equals("GET")) {
                        try {
                            response = doGet(mime, file);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            response = doErr(404, null);
                        }
                    } else if (method.equals("HEAD")) {
                        response = doHead(mime, file);
                    } else {
                        response = doErr(501, method);
                    }

                }
                /*
                 * \ Echo the data back and flush the stream to make sure that
                 * the data is sent immediately
                 */
                outStream.writeBytes(response);
                outStream.flush();
                /* Interaction with this client complete, close() the socket */
                clientSock.close();
            } catch (IOException e) {
                clientSock = null;
                continue;
            }
        }
    }

    private static String doErr(int code, String str) {
        // TODO Auto-generated method stub
        ErrResponse response = new ErrResponse(code, str);
        return response.getResponse();
    }

    private static String doHead(String mime, File file) {
        // TODO Auto-generated method stub
        NormalResponse response = new NormalResponse(file.length(), mime);
        return response.getResponse(null);
    }

    private static String doGet(String mime, File file) throws FileNotFoundException {
        // TODO Auto-generated method stub
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (tempString != null) {
                sb.append(tempString);
                sb.append("\r\n");
                tempString = reader.readLine();
            }
            reader.close();
            NormalResponse response = new NormalResponse(file.length(), mime);
            return response.getResponse(sb.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return doErr(500, null);
        }
    }
}
