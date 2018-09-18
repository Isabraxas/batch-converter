package cc.viridian.servicebatchconverter.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

public class Userlog {
    private static PrintWriter writer;

    public Userlog() {
        LocalDate localDate = LocalDate.now();
        String fileName = "Statement" + ".txt";
        String filePath = "/home/isvar/Documents/statement/service-batch-converter/LOG_PATH_IS_UNDEFINED/" + localDate + ".txt";
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

    public Userlog(final String fileName) {
        LocalDate localDate = LocalDate.now();
        String name = fileName.split(".")[0];
        String filePath = "/home/isvar/Documents/statement/service-batch-converter/LOG_PATH_IS_UNDEFINED/"
            +name+"-"+ localDate + ".txt";
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
    }

    public void closeLog() throws IOException {
        writer.close();
    }

    public PrintWriter getPrintWriter() throws IOException {
        return writer;
    }
}
