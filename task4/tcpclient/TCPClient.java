package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    boolean shut;
    Integer time;
    Integer lim;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        shut = shutdown;
        time = timeout;
        lim = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException, SocketTimeoutException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Socket sockme = new Socket(hostname, port);
        try{
            Integer max = Integer.MAX_VALUE;
            if(this.lim == null)
                lim = max;
            byte[] userbuf = new byte[1024];
            output = new ByteArrayOutputStream();
            if (shut == true){
                sockme.shutdownOutput();
                return toServerBytes;
            }

            sockme.getOutputStream().write(toServerBytes, 0, toServerBytes.length);
            if(time != null){
                sockme.setSoTimeout(time);
            }
            int num;
            while((num = sockme.getInputStream().read(userbuf)) != -1){
                if(lim < num)
                {
                    output.write(userbuf, 0, lim);
                    sockme.close();
                    return output.toByteArray();
                }

                output.write(userbuf, 0, num);
                lim = lim - num;
            }
            sockme.close();
            return output.toByteArray();
        }
        catch (SocketTimeoutException wag1){
            System.out.println("SocketTimeException bram");
            sockme.close();
            return output.toByteArray();
        }
        catch (IOException ex){
            System.out.println("IOException: your input is not valid");
            throw new IOException();
        }
    }
}
