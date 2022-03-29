import tcpclient.TCPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConcHTTPAsk {

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        ServerSocket sockmyserver = new ServerSocket(port);
        while (true){
            Socket sockme = sockmyserver.accept();
            Runnable multi = new Myrunnable(sockme);
            new Thread(multi).start();
        }
    }

    public static class Myrunnable implements Runnable {

        Socket multi_socket;
        public Myrunnable(Socket parameter){
           this.multi_socket = parameter;
       }


        @Override
        public void run() {
            try {

                byte[] test = new byte[1024];
                boolean error404;

                InputStream in = multi_socket.getInputStream();
                OutputStream out = multi_socket.getOutputStream();

                in.read(test);
                String wasgood = new String(test, StandardCharsets.UTF_8);
                error404 = false;
                if(!wasgood.contains("HTTP/1.1") || !wasgood.contains("GET")){
                    String endOfLine = "\r\n";

                    String response = "HTTP/1.1 400 Bad Request" + endOfLine + endOfLine;
                    out.write(response.getBytes());
                }
                else{

                    //System.out.println(wasgood);
                    String[] something = wasgood.split("HTTP/1.1");

                    if(!something[0].contains("/ask?") || !something[0].contains("hostname=") || !something[0].contains("port="))
                        error404 = true;
                    if (error404)
                    {
                        String endOfLine = "\r\n";

                        String response = "HTTP/1.1 404 Not Found" + endOfLine + endOfLine;
                        out.write(response.getBytes());
                    }
                    if(!error404) {
                        String[] string1 = something[0].split("[?]");
                        String[] string2 = string1[1].split("&");

                        Integer port = null;
                        Integer limit = null;
                        String hostname = null;
                        String string = null;
                        Integer timeout = null;
                        boolean shutdown = false;

                        for (int i = 0; i < string2.length; i++){

                            if(string2[i].contains("hostname")){
                                String[] host = string2[i].split("=");
                                //System.out.println("This is the hostname: " + host[1]);
                                hostname = host[1].trim();
                            }
                            if (string2[i].contains("limit")){
                                String[] lim = string2[i].split("=");
                                //System.out.println("This is the limit: " + lim[1]);
                                limit = Integer.parseInt(lim[1].trim());
                            }
                            if(string2[i].contains("port")){
                                String[] p = string2[i].split("=");
                                //System.out.println("This is the port: " + p[1]);
                                port = Integer.parseInt(p[1].trim());
                            }
                            if(string2[i].contains("string")){
                                String[] s = string2[i].split("=");
                                //System.out.println("This is the string: " + s[1]);
                                string = s[1].trim();
                            }
                            if (string2[i].contains("shutdown")){
                                String[] shut = string2[i].split("=");
                                //System.out.println("This is the shutdown: " + shut[1]);
                                shutdown = Boolean.parseBoolean(shut[1].trim());
                            }
                            if (string2[i].contains("timeout")){
                                String[] time = string2[i].split("=");
                                //System.out.println("This is the timeout: " + time[1]);
                                timeout = Integer.parseInt(time[1].trim());
                            }
                        }
                        byte[] stringArray;

                        if(string != null)
                            stringArray = (string.trim() + "\n").getBytes();
                        else
                            stringArray = new byte[0];

                        byte[] result;
                        TCPClient tcp = new TCPClient(shutdown, timeout, limit);
                        result = tcp.askServer(hostname, port, stringArray);

                        String writeMe = new String(result, StandardCharsets.UTF_8);

                        String endOfLine = "\r\n";

                        String response = "HTTP/1.1 200 OK" + endOfLine + endOfLine + writeMe;
                        out.write(response.getBytes());
                    }
                }
                multi_socket.close();
            }
            catch (Exception e){
                System.out.println("Det gick fel");
            }
        }
    }
}
