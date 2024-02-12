import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main{
    public static void main(String[] args) {
        readLines("log.txt");
    }
    public static void readLines(String filename){
        String[] snapshot = new String[98];
        File file = new File(filename);
        double time = 0;
        float stft = 0;
        String stftColor = null;
        try {
            System.out.println("EGR Data from "+filename+": ");
            Scanner sc = new Scanner(file);
            while(sc.hasNext()){
                for (int i = 0; i < snapshot.length; i++) {
                    snapshot[i] = sc.next();
                  }
                if(Integer.parseInt(snapshot[33])>=128){
                    stft = Math.abs(100*(1-(Float.parseFloat(snapshot[33])/128)));
                }else if(Integer.parseInt(snapshot[33])<128){
                    stft = -100*(1-(Float.parseFloat(snapshot[33])/128));
                }
                String stftStr = String.format("%.2f", stft);
                if(-5<=stft && stft<=5){
                    stftColor= "\u001B[32m";
                }else if(stft<20 && stft>-20){
                    stftColor= "\u001B[33m";
                }else if (-20>=stft){
                    stftColor= "\u001B[34m";
                }else{
                    stftColor= "\u001B[31m";
                }
                if ((Double.parseDouble(snapshot[39])>0)&&(Double.parseDouble(snapshot[38])>36)){
                    if (Math.abs(Double.parseDouble(snapshot[0])-time)>2){
                        System.out.println();
                    }
                    System.out.println(snapshot[0] + "s, " + snapshot[40] + "F, " + snapshot[39] + "% EGR, "+snapshot[38]+" Degrees Advance, "+snapshot[30]+"kPa, "+snapshot[31]+"RPM, "+snapshot[32]+"% Throttle, "+stftColor+stftStr+"% STFT"+"\u001B[0m");
                    time = Double.parseDouble(snapshot[0]);
                }
            }
            sc.close();
            System.out.println("----------------------------------------------------------------------------------------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}