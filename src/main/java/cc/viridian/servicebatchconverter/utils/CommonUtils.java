package cc.viridian.servicebatchconverter.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A common methods and utils for the data generator for the baku stadium example.
 *
 * @author feronti
 */

public class CommonUtils {
    public String operativSystem;
    static long initTime = 0;
    static long currentRunTime = 0;

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
        //System.out.println(operativSystem);
        System.out.print(clear());
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.print(blue() + moveTo(0, 0));
        System.out.println(" ____    _  _____ ____ _   _ ");
        System.out.println("| __ )  / \\|_   _/ ___| | | |");
        System.out.println("|  _ \\ / _ \\ | || |   | |_| |");
        System.out.println("| |_) / ___ \\| || |___|  _  |");
        System.out.println("|____/_/   \\_\\_| \\____|_| |_|");
        System.out.println("  " + title + " " + version);
        System.out.println(black());
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        System.out.print(moveTo(1, 9));
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

    public static long getCurrentRunTime() {
        currentRunTime = System.currentTimeMillis() - initTime;
        return currentRunTime;
    }
}
