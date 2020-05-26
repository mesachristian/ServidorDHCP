package model;

public class DireccionIPv4 {
    
    private byte[] direccion;

    public DireccionIPv4(byte[] dir){
        direccion = dir;
    }

    public DireccionIPv4(String dir) {
        direccion = new byte[4];
        final String[] octetos = dir.split(":");
        for (int i = 0; i < 4; i++) {
            final Integer integer = Integer.valueOf(octetos[i]);
            direccion[i] = integer.byteValue();
        }
    }

    public DireccionIPv4(int[] dir){
        direccion = new byte[4];
        for(int i=0; i < 4; i++){
            this.direccion[i] = Integer.valueOf(dir[i]).byteValue();
        }
    }

    public int[] direccionEnteros(){
        int[] dir = new int[4];
        for(int i=0; i < 4; i++){
            dir[i] = direccion[i] & 0xFF;
        }
        return dir;
    }

    public byte[] getDireccion() {
        return this.direccion;
    }

    @Override
    public String toString(){
        return String.format("%d.%d.%d.%d", direccion[0] & 0xFF, direccion[1] & 0xFF, direccion[2] & 0xFF, direccion[3] & 0xFF);
    }
}