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
        while(true){ //Loop forever
            String logFile = Menu(); //Get the Filename to analyze
            if(!logFile.contains("Exit Program")){ //Close if logFile is "Exit Program"
                readLines(logFile); //Pass filename to readLines
            }else{
                System.out.println("Goodbye!");
                System.exit(0); //Kill the program
            }
        }
    }

    public static String Menu(){
        String fileName = null;
        int fileNum = 0;
        try {
            List<String> files = listFiles(); //Create Arraylist of files in directory
            files.add("..Exit Program");
            drawFiles(files); //Draw the file listing
            System.out.println("Which file would you like to display?");
            Scanner kb = new Scanner(System.in);  // Create a Scanner object
            while (true){ //Take in next integer from keyboard
                try {
                    fileNum = Integer.parseInt(kb.nextLine())-1;
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.print("Try again: ");
                }
            }
            fileName=files.get(fileNum); //Converts number to index in ArrayList

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName.substring(2); //Cut off the "./" from the beginning of the string, and send back the filename
    }

    public static void drawFiles(List<String> files){
        String s = "";
        int maxLength=0;
        int fLen = 0;
        for(int f=0;f<files.size();f++){ //Find the length of the longest filename+numbers for box
            fLen = String.valueOf(f).length();
            if(maxLength<(files.get(f).length()+fLen))
                maxLength=(files.get(f).length()+fLen);
        }
        //Print out the box itself, with filenames and numbers inside
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

    public static List<String> listFiles() throws IOException { //Use .walk to find files in current directory ending in .txt
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
        String[] snapshot = new String[98]; //Data from ALDL log is 98 data values per second
        File file = new File(filename);
        double time = 0;
        float stft= 0;
        float avgstft = 0;
        float avgMAP = 0;
        int totalOccurs = 0;
        try {
            System.out.println("EGR Data from "+filename+": ");
            Scanner sc = new Scanner(file); //Scan the filename provided
            while(sc.hasNext()){ //While there is a next line
                for (int i = 0; i < snapshot.length; i++) {
                    snapshot[i] = sc.next(); //Read all 98 values into the array
                }

                if ((Double.parseDouble(snapshot[39])>0)&&(Double.parseDouble(snapshot[38])>36)){ //If EGR is on, and Computer is not disabling the timing advance
                    if(Integer.parseInt(snapshot[33])>=128){//Formats positive STFT
                        stft = Math.abs(100*(1-(Float.parseFloat(snapshot[33])/128)));
                    }else if(Integer.parseInt(snapshot[33])<128){//Formats negative STFT
                        stft = -100*(1-(Float.parseFloat(snapshot[33])/128));
                    }
                    String stftStr = colorize(stft); //Colorize the STFT to be easier to read
                    if (Math.abs(Double.parseDouble(snapshot[0])-time)>2){ //If the time passed is longer than 2 second between EGR activations, skip a line
                        System.out.println();
                    }
                    System.out.println(Math.floor(Float.parseFloat(snapshot[0])) + "s, " + snapshot[40] + "F, " + snapshot[39] + "% EGR, "+snapshot[38]+" Degrees Advance, "+snapshot[30]+"kPa, "+snapshot[31]+"RPM, "+snapshot[32]+"% Throttle, "+stftStr+" STFT"); //Print out all of the information I feel is needed to diagnose EGR
                    time = Double.parseDouble(snapshot[0]); //Keep track of the current time from the snapshot
                    totalOccurs++; //Add to the number of EGR occurrences to calculate average
                    avgstft = avgstft+stft; //Add STFT to average
                    avgMAP = avgMAP+Float.parseFloat(snapshot[30]); //Add MAP to calculate average
                }
            }
            sc.close(); //When done, close the file
            avgMAP=avgMAP/totalOccurs; //Calculate average MAP to put at the end
            avgstft=avgstft/totalOccurs; //Calculate average STFT to put at the end
            String avgstftStr = colorize(avgstft); //Colorize the average STFT
            System.out.println("Average STFT: "+avgstftStr+" \nAverage MAP: "+String.format("%.2f",avgMAP)+"kPa");
            System.out.println("----------------------------------------------------------------------------------------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String colorize(float percentage){ //Sets the colors of the STFT, Blue: rich, Red: lean, Yellow: ok, Green: great
        String color;
        if(-5<=percentage && percentage<=5){
            color= "\u001B[32m"; //Color Green
        }else if(percentage<20 && percentage>-20){
            color= "\u001B[33m"; //Color Yellow
        }else if (-20>=percentage){
            color= "\u001B[34m"; //Color Blue
        }else{
            color= "\u001B[31m"; //Color Red
        }
        return(color+String.format("%.2f", percentage)+"%"+"\u001B[0m"); //Return colored text with percentage and Color end
    }

}