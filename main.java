import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main{
    public static void main(String[] args) {
        while(true){
            String logFile = Menu().substring(2);
            readLines(logFile);
        }

    }

    public static String Menu(){
        String fileName = null;
        int fileNum = 0;
        try {
            List<String> files = listFiles();
            drawFiles(files);
            System.out.println("Which file would you like to display?");
            Scanner kb = new Scanner(System.in);  // Create a Scanner object
            while (true)
            try {
                fileNum = Integer.parseInt(kb.nextLine())-1;
                break;
            } catch (NumberFormatException nfe) {
                System.out.print("Try again: ");
            }
            fileName=files.get(fileNum);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static void drawFiles(List<String> files){
        String s = "";
        int maxLength=0;
        int fLen = 0;
        for(int f=0;f<files.size();f++){
            fLen = String.valueOf(f).length();
            if(maxLength<(files.get(f).length()+fLen))
                maxLength=(files.get(f).length()+fLen);
        }
        System.out.print("+");
        for (int i = 0; i <= maxLength+1 ; i++) {
            System.out.print("-");
        }
        System.out.println("+");
        for(int i=0;i<files.size();i++){
            System.out.print("| ");
            s=(i+1)+": "+files.get(i).substring(2);
            System.out.print(s);

            for(int k=s.length();k<=maxLength;k++)
                System.out.print(" ");

            System.out.println("|");
        }
        System.out.print("+");
        for (int i = 0; i <= maxLength+1 ; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
    public static List<String> listFiles()
        throws IOException {

        List<String> result;
        try (Stream<Path> walk = Files.walk(Paths.get("."))) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("txt"))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static void readLines(String filename){
        String[] snapshot = new String[98];
        File file = new File(filename);
        double time = 0;
        float stft= 0;
        float avgstft = 0;
        float avgMAP = 0;
        int totalOccurs = 0;
        try {
            System.out.println("EGR Data from "+filename+": ");
            Scanner sc = new Scanner(file);
            while(sc.hasNext()){
                for (int i = 0; i < snapshot.length; i++) {
                    snapshot[i] = sc.next();
                }

                if ((Double.parseDouble(snapshot[39])>0)&&(Double.parseDouble(snapshot[38])>36)){
                    if(Integer.parseInt(snapshot[33])>=128){
                        stft = Math.abs(100*(1-(Float.parseFloat(snapshot[33])/128)));
                    }else if(Integer.parseInt(snapshot[33])<128){
                        stft = -100*(1-(Float.parseFloat(snapshot[33])/128));
                    }
                    String stftStr = colorize(stft);
                    if (Math.abs(Double.parseDouble(snapshot[0])-time)>2){
                        System.out.println();
                    }
                    System.out.println(snapshot[0] + "s, " + snapshot[40] + "F, " + snapshot[39] + "% EGR, "+snapshot[38]+" Degrees Advance, "+snapshot[30]+"kPa, "+snapshot[31]+"RPM, "+snapshot[32]+"% Throttle, "+stftStr+" STFT");
                    time = Double.parseDouble(snapshot[0]);
                    totalOccurs++;
                    avgstft = avgstft+stft;
                    avgMAP = avgMAP+Float.parseFloat(snapshot[30]);
                }
            }
            sc.close();
            avgMAP=avgMAP/totalOccurs;
            avgstft=avgstft/totalOccurs;
            String avgstftStr = colorize(avgstft);
            System.out.println("Average STFT: "+avgstftStr+" \nAverage MAP: "+String.format("%.2f",avgMAP)+"kPa");
            System.out.println("----------------------------------------------------------------------------------------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static String colorize(float percentage){
        String color;
        if(-5<=percentage && percentage<=5){
            color= "\u001B[32m";
        }else if(percentage<20 && percentage>-20){
            color= "\u001B[33m";
        }else if (-20>=percentage){
            color= "\u001B[34m";
        }else{
            color= "\u001B[31m";
        }
        return(color+String.format("%.2f", percentage)+"%"+"\u001B[0m");
    }

}