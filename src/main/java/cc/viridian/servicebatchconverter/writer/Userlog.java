package cc.viridian.servicebatchconverter.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.time.LocalDate;

public class Userlog {

    String fileResource;
    private static PrintWriter writer;

    public Userlog() {
        fileResource = System.getProperty("user.dir");
        LocalDate localDate = LocalDate.now();
        String filePath;
        filePath = fileResource + "/" + localDate + ".txt";
        File userLogFile =new File(filePath);
        if(!userLogFile.exists()){
            try {
                userLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String encoding = "UTF-8";
        System.out.println("Working directory - save file: " + filePath);

        try {
            FileWriter fileWriter = new FileWriter(userLogFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            writer = new PrintWriter(bufferedWriter);
            writer.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Userlog(final String filePath) {
        LocalDate localDate = LocalDate.now();
        String encoding = "UTF-8";
        int i = 1;
        try {
            writer = new PrintWriter(filePath, encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setProcessedFile(final String fileName) throws IOException {
        writer.println(fileName);
    }

    public void info(final String message) throws IOException {
        LocalDate localDate = LocalDate.now();
        writer.println(localDate + " - " + message);
        System.out.println(message);
    }

    public void closeLog() throws IOException {
        writer.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        writer.close();
    }

    public PrintWriter getPrintWriter() throws IOException {
        return writer;
    }
}
