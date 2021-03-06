package cc.viridian.servicebatchconverter.writer;

import cc.viridian.servicebatchconverter.utils.CommonUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Userlog {

    private CommonUtils commonUtils;

    private PrintStream writer;

    private static Userlog userlog;

    private LocalDateTime printDateTime;

    public Userlog(final String path, final CommonUtils commonUtils) {
        this.commonUtils = commonUtils;
        this.writer = null;

        File pathFolder = new File(path);
        if (!pathFolder.isDirectory()) {
            System.out.println(commonUtils.red() + path + " is not a valid directory");
            return;
        }
        if (!pathFolder.canWrite()) {
            System.out.println(commonUtils.red() + pathFolder.getAbsolutePath() + " is not writable");
            return;
        }

        LocalDate localDate = LocalDate.now();
        File logFile = new File(pathFolder.getAbsolutePath() + "/" + localDate.toString() + ".log");
        String encoding = "UTF-8";

        if (logFile.exists()) {
            System.out.println("append to log file: " + logFile.getName());
        } else {
            System.out.println("creating log file: " + logFile.getName());
        }

        try {
            writer = new PrintStream(new FileOutputStream(logFile.getAbsolutePath(), true), true, encoding);
            //info("start writing log");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    public PrintStream getWriter() {
        return writer;
    }

    public static Userlog factoryUserlog(final String path, final CommonUtils commonUtils) {
        if (userlog == null) {
            userlog = new Userlog(path, commonUtils);
            return userlog;
        } else {
            return userlog;
        }
    }

    public static Userlog getUserlog() {
        return userlog;
    }

    //print the same message in the userlog and the console
    public void info(final String message) {
        printDateTime = LocalDateTime.now();
        writer.println(printDateTime + " - " + message);
        System.out.println(message);
    }

    //print the same message in the userlog and the console as error
    public void error(final String message) {
        printDateTime = LocalDateTime.now();
        writer.println(printDateTime + " - " + commonUtils.red() + message + commonUtils.black());
        System.out.println(message);
    }

    //print the same message in the userlog and the console as warning
    public void warn(final String message) {
        printDateTime = LocalDateTime.now();
        writer.println(printDateTime + " - " + commonUtils.green() + message + commonUtils.black());
        System.out.println(message);
    }

    public void closeLog() {
        if (writer != null) {
            writer.println("***************************************");
            writer.println();
            writer.close();
        }
    }
}
