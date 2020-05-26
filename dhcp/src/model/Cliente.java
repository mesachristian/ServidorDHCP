package model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Cliente {

    public final static int PORT = 14000;

    public Cliente() {

        try {
            DatagramSocket client = new DatagramSocket(Cliente.PORT);
            
            DatagramPacket request = new DatagramPacket(TramaDHCP.MAGIC_COOKIE, TramaDHCP.MAGIC_COOKIE.length, 
                                                        InetAddress.getLoopbackAddress(), ServidorDHCP.PORT);
            client.send(request);
            client.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException{
        new Cliente();
    }
}