package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    public static int size = 1024;

    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        try{
            Socket sockme = new Socket(hostname, port);
            byte[] userbuf = new byte[size];
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            sockme.getOutputStream().write(toServerBytes, 0, toServerBytes.length);

            int num;
            while((num = sockme.getInputStream().read(userbuf)) != -1){
                output.write(userbuf, 0, num);
            }

            sockme.close();

            return output.toByteArray();
        }
        catch (IOException ex){
            System.out.println("IOException: your input is not valid");
            throw new IOException();
        }
    }
}
