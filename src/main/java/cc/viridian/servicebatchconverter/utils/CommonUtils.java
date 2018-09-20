package cc.viridian.servicebatchconverter.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A common methods and utils for the data generator for the baku stadium example.
 *
 * @author feronti
 */

public class CommonUtils {
    public String operativSystem;
    static final float BYTES_FILE = (float) 370460.00;
    static final long EXECUTION_TIME_FILE = 5006;
    static final int STATEMENTS = 10;
    static long initTime = 0;
    static long currentRunTime = 0;
    Boolean currentProgress;
    String progressBar = "[";

    static Thread t = new Thread();

    ArrayList<HashMap<String, Object>> logicalReaders;

    public CommonUtils() {
    }

    public String moveTo(int x, int y) {
        if (operativSystem.equals("WINDOWS")) {
            return "";
        }
        String s = "\u001B[" + y + ";" + x + "H";
        return s;
    }

    private String getOperativSystem() {
        String str = System.getProperty("os.name").toUpperCase();
        String[] splited = str.split("\\s+");
        return splited[0].trim();
    }

    public String clear() {
        String code = "\u001B[2J";

        if (operativSystem.equals("WINDOWS")) {
            code = "\n\n";
        }
        return code;
    }

    public String black() {
        String code = "\u001B[30m";

        if (operativSystem.equals("WINDOWS")) {
            code = "";
        }
        return code;
    }

    public String red() {
        String code = "\u001B[31m";

        if (operativSystem.equals("WINDOWS")) {
            code = "";
        }
        return code;
    }

    public String green() {
        String code = "\u001B[32m";

        if (operativSystem.equals("WINDOWS")) {
            code = "";
        }
        return code;
    }

    public String blue() {
        String code = "\u001B[34m";

        if (operativSystem.equals("WINDOWS")) {
            code = "";
        }
        return code;
    }

    public void setTitle(final String title) {
        String version = "1.0";

        operativSystem = getOperativSystem();
        System.out.println(operativSystem);
        System.out.print(clear() + blue() + moveTo(0, 0));
        System.out.println(" ____    _  _____ ____ _   _ ");
        System.out.println("| __ )  / \\|_   _/ ___| | | |");
        System.out.println("|  _ \\ / _ \\ | || |   | |_| |");
        System.out.println("| |_) / ___ \\| || |___|  _  |");
        System.out.println("|____/_/   \\_\\_| \\____|_| |_|");
        System.out.println("  " + title + " " + version);
        System.out.println(black());

        System.out.print(moveTo(1, 9));
        //System.out.print( red() + moveTo(10, 10)+ percent);
    }

    public void expectedTime(final String filepath) {
        //TODO calcular el tamaño del archivo
        //Promedio de cuanto dura el proceso para 10 y para 100
        DecimalFormat df = new DecimalFormat("#");
        String myFile = filepath;

        float currentFileBytes = new File(myFile).length();

        System.out.println(myFile.substring(myFile.lastIndexOf("/") + 1)
                               + "  : " + df.format(currentFileBytes) + " bytes");

        float numRegs = currentFileBytes * (STATEMENTS / BYTES_FILE);
        long expectedTime = (long) ((Math.round(currentFileBytes) * EXECUTION_TIME_FILE) / BYTES_FILE);

        System.out.println("Expected Time " + expectedTime + "ms\n");
    }

    public Long getFileLines(final String filePath) throws IOException {
        FileReader fr = new FileReader(filePath);
        BufferedReader bf = new BufferedReader(fr);
        long lNumeroLineas = 0;

        while (bf.readLine() != null) {
            lNumeroLineas++;
        }
        return lNumeroLineas;
    }

    public void incrementProgressBar(final Long currentLine, final Long totalLines) {
        if (currentLine == totalLines) {
            currentProgress = true;
        }
        Long percentage = (currentLine * 100) / totalLines;
        progressBar = String.valueOf(percentage);
    }

    public String getProgressBar() {
        return progressBar;
    }

    public Boolean getEnd() {
        return currentProgress;
    }

    public static void showPercentageByBytes(final String filePath, final long myBytesRead) {
        File file = new File(filePath);
        long totalBytes = file.length();
        long bytesRead = myBytesRead;

        long percent = (bytesRead * 100L / totalBytes);

        //System.out.print("PERCENT: ");
        System.out.println(String.valueOf(percent) + "%");

        long expectedTime = ((getCurrentRunTime() * totalBytes) / myBytesRead) - getCurrentRunTime();

        System.out.println(expectedTime + " ms ");
    }

    public static long getInitTime() {
        initTime = System.currentTimeMillis();
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public static long getCurrentRunTime() {
        currentRunTime = System.currentTimeMillis() - initTime;
        return currentRunTime;
    }

    public void setEndTime(long endTime) {
        this.currentRunTime = endTime;
    }
}
