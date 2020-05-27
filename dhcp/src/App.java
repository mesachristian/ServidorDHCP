

public class App {
    public static void main(String[] args) throws Exception {
        int i=0;

        for(int j=0; j < 4; j++){
            String s = String.format("%d",++i);
            System.out.println(s);
        }
    }

    
}
