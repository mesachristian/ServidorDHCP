package model;

import java.util.ArrayList;

public class DHCPOption { // Opciones definidas en el RFC 1533
 
    private int code;
    private int len;
    private byte[] body;

    public DHCPOption(byte code, byte len, byte[] body){
        this.code = code & 0xFF;
        this.len = len & 0xFF;
        this.body = new byte[this.len];
        for(int i=0; i < len; i++){
            this.body[i] = body[i];
        }     
    }

    public static ArrayList<DHCPOption> getOptions(byte[] data){

        ArrayList<DHCPOption> options = new ArrayList<>();
        int pos = 0;

        while( data[pos] != new Integer(0xFF).byteValue() ){ // Endmark : 255
            DHCPOption option = new DHCPOption( data[pos], data[pos + 1], getSliceOfArray(data,pos+2,data.length - 1));
            pos += 2 + option.len;
            options.add(option);
        }

        return options;
    }

    public static byte[] getSliceOfArray(byte[] arr, int start, int end) 
    { 
        // Get the slice of the Array 
        byte[] slice = new byte[end - start]; 
  
        // Copy elements of arr to slice 
        for (int i = 0; i < slice.length; i++) { 
            slice[i] = arr[start + i]; 
        } 
  
        // return the slice 
        return slice; 
    } 

    public void printBody(){
        for(int i=0; i < this.len; i++){
            String s = String.format("%x ", body[i]);
            System.out.print(s);
            if(i%5 == 0){
                System.out.println("");
            }
        }
    }

    public int getCode() {
        return this.code;
    }

    public int getLen() {
        return this.len;
    }

    public byte[] getBody() {
        return this.body;
    }

}