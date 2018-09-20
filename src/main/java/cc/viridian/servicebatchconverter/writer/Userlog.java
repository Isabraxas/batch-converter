package cc.viridian.servicebatchconverter.writer;

import cc.viridian.servicebatchconverter.utils.FormatUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Userlog {

    String fileResource;
    private static PrintWriter writer;

    public Userlog() {
        fileResource = System.getProperty("user.dir");
        LocalDate localDate = LocalDate.now();
        String filePath;
        filePath = fileResource + "/" + localDate + ".txt";
        File userLogFile = new File(filePath);
        if (!userLogFile.exists()) {
            try {
                userLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Default file path to save user log : " + filePath);

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
        File userLogFile = new File(filePath);
        if (!userLogFile.exists()) {
            try {
                userLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    public void setProcessedFile(final String fileName) throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now();
        String dateline = "Date: " + localDateTime;
        int colsize = 30;

        writer.println("  ------------------------------");
        writer.println("| " + String.valueOf(FormatUtil.returnDelimArray(dateline, colsize)) + " |");
        writer.println("  ------------------------------");
        writer.println("*** File name: " + fileName + " ***");
        writer.println();
    }

    public void info(final String message) throws IOException {
        writer.println(message);
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
