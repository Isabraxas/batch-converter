package cc.viridian.servicebatchconverter.writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.time.LocalDate;

public class Userlog {

    URL fileResource;
    private static PrintWriter writer;

    public Userlog() {
        fileResource = this.getClass().getResource("/user/base.txt");
        LocalDate localDate = LocalDate.now();
        String filePath;
        /*
        String fileName = "Statement" + ".txt";
        filePath = fileResource.getPath()
                                      .substring(0, fileResource.getPath().lastIndexOf("/") + 1);
        String[] filePathParts = filePath.split("/");
        int i=0;
        int f=0;
        String tmpFilePath = "";
        while (f < 4){
            if(filePathParts[f].length() > 1) {
                tmpFilePath = tmpFilePath + "/" + filePathParts[f];
            }
            f++;
        }
        filePath = tmpFilePath +"/"+ localDate + ".txt";
        */
        filePath = fileResource.getPath();
        String encoding = "UTF-8";

        try {
            writer = new PrintWriter(filePath, encoding);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
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
    }

    public void closeLog() throws IOException {
        writer.close();
    }

    public PrintWriter getPrintWriter() throws IOException {
        return writer;
    }
}
