package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ServidorDHCP implements Runnable {

    public final static int PORT = 67;
    public final static int CLIENT_PORT = 68;
    private final static String PATH = "H:\\Escritorio\\8_Semestre\\Comunicacion_y_redes\\DHCP\\proyecto\\dhcp\\src\\model\\";
    private DatagramSocket server;
    private ArrayList<Subred> subredes;

    public ServidorDHCP(){
        subredes = new ArrayList<>();
    }

    @Override
    public void run() {

        while (true) {
            server = null;

            try {
                server = new DatagramSocket(PORT,InetAddress.getByName("192.168.0.25"));

                byte[] inputBuffer = new byte[512];
                DatagramPacket request = new DatagramPacket(inputBuffer, inputBuffer.length);

                server.receive(request);
                
                if(request.getLength() >= 236 && request.getLength() <= 300){ // Podria ser una trama DHCP
                    
                    // Revisar las posiciones 44 a 47 que tienen el Magic Cookie
                    boolean mensajeDHCP = true;
                    for(int i=0; i < 4;i++){
                        if(TramaDHCP.MAGIC_COOKIE[i] != inputBuffer[236+i]){
                            mensajeDHCP = false;
                        }
                    }

                    if( mensajeDHCP ){
                        System.out.println("Se recibe un DHCP");
                        
                        byte[] optionsData = DHCPOption.getSliceOfArray(inputBuffer, 240, request.getLength() - 1 );
                        ArrayList<DHCPOption> options = DHCPOption.getOptions(optionsData);
                        
                        for(DHCPOption o : options){
                            if( o.getCode() == 53 ){ // Tipo del mensaje DHCP
                                byte type = o.getBody()[0];
                                switch ( (int)(type & 0xFF) ){
                                    case 1: // DISCOVER
                                        System.out.println("Mensaje DISCOVER");
                                        crearOffer(inputBuffer,options);
                                        break;

                                    case 3: // REQUEST
                                        System.out.println("Mensaje REQUEST");
                                        break;

                                    case 4: // DECLINE
                                        System.out.println("Mensaje DECLINE");
                                        break;

                                    case 7: // RELEASE
                                        System.out.println("Mensaje RELEASE");
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            } catch (SocketException e1) {
                e1.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            finally{
                server.close();
            }
        }
    } 
    
    private static void crearOffer(byte[] encabezadoDHCP, List<DHCPOption> opciones){
        TramaDHCP tramaDHCP = TramaDHCP.crearTramaDHCP(encabezadoDHCP);

        // A la trama DISCOVER hay que hacerle los siguiente cambios para convertirla en una OFFER
        // 1. Cambiar el OP al correspondiente
        tramaDHCP.setOp(new Integer(2).byteValue());
        tramaDHCP.imprimirTramaDHCP();
        System.out.println("========================================================");
    }

    public void configuracionInicial(){
        try {
            File configuracion = new File(PATH + "configuracion.txt");
            Scanner myReader = new Scanner(configuracion);
            boolean readStats = true;
            int direcciones = 0;
            int idx = 0;
            Subred subred = null;
            HashMap<DireccionIPv4,Integer> tiemposArrendamiento = null;

            while (myReader.hasNextLine()) {
              String line = myReader.nextLine();
              if(readStats){
                String[] parameters = line.split(";");
                subred = construirSubred(parameters);
                direcciones = Integer.parseInt(parameters[1]);
                idx = 0;
                tiemposArrendamiento = new HashMap<DireccionIPv4,Integer>();
                readStats = false;
              }else{
                if(idx < direcciones){
                    tiemposArrendamiento.put(subred.getDirecciones().get(idx), Integer.valueOf(line));
                    idx++;
                    if(idx == direcciones){
                        /*for(DireccionIPv4 dir : tiemposArrendamiento.keySet()){
                            System.out.println("Llave: " + dir.toString() + ", tiempo: " + String.valueOf(tiemposArrendamiento.get(dir)));
                        }
                        System.out.println("============================");*/
                        subred.setTiemposArrendamiento(tiemposArrendamiento);
                        subredes.add(subred);
                        subred = null;
                        tiemposArrendamiento = null;
                        readStats = true;
                    }
                }
              }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encuentra configuracion.txt!");
            e.printStackTrace();
        }
    }

    private Subred construirSubred(String[] parameters){
        Subred subred;
        String[] inicial_final = parameters[0].split(",");
        int totalDirecciones = Integer.parseInt(parameters[1]);
        ArrayList<DireccionIPv4> direcciones = new ArrayList<DireccionIPv4>();

        int[] direccion = new DireccionIPv4(inicial_final[0]).direccionEnteros();
        int direccionInicial = direccion[3];
        for(int i=0; i < totalDirecciones; i++){
            direccion[3] = direccionInicial + i;
            direcciones.add(new DireccionIPv4(direccion));
        }
        DireccionIPv4 mascara = new DireccionIPv4(parameters[2]);
        DireccionIPv4 gateway = new DireccionIPv4(parameters[3]);
        DireccionIPv4 dns = new DireccionIPv4(parameters[4]);
        
        subred = new Subred(direcciones, mascara, gateway, dns);
        
        /*System.out.println("Direcciones:");
        for(DireccionIPv4 dir : direcciones){
            System.err.println(dir.toString());
        }*/
        return subred;
    }

    public static void main(String[] args) {
        ServidorDHCP servidor = new ServidorDHCP();
        servidor.configuracionInicial();
        Thread escucha = new Thread(servidor);
        escucha.start();
    }
}