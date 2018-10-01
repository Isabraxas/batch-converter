package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.service.ReadStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
import cc.viridian.servicebatchconverter.utils.CommonUtils;
import cc.viridian.servicebatchconverter.writer.Userlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.HashMap;

@Slf4j
@Service
public class BatchConverterRun implements CommandLineRunner {

    HashMap<String, Object> appParams = new HashMap<>();
    @Autowired
    private ReadStatementsFileService readStatementsFileService;
    @Autowired
    private StatementHeaderService statementHeaderService;
    private CommonUtils commonUtils = new CommonUtils();
    private File prnFile = null;
    private String prnFilename = null;
    private String userlogPath = null;
    private FileInfoResponse fileInfoResponse;
    private Userlog userlog;

    @Override
    public void run(final String... args) throws Exception {
        commonUtils.setTitle("BATCH CONVERTER");

        userlogPath = System.getProperty("user.dir");

        //check params are present and exit program if there is an error
        if (!processArgs(args)) {
            return;
        }

        userlog = new Userlog(userlogPath, commonUtils);

        if (userlog.getWriter() == null) {
            return;
        }

        //clean tables
        if (args[0].contains("--clear-tables") ) {
            statementHeaderService.deleteAll();
            userlog.info("Database clean headers and details");
            userlog.closeLog();
            return;
        }

        //verify if the prn file already exists and can read it
        prnFile = verifyPrnFile(prnFilename);
        if (prnFile == null) {
            userlog.info("prnFile error");
            userlog.closeLog();
            return;
        }

        //commonUtils.expectedTime(firtsParamPathFile);
        commonUtils.getInitTime();

        //verify hash
        String hashCodeFile = HashCode.getCodigoHash(prnFilename);
        Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);

        if (isSaved) {
            userlog.info("hash already exists in database.  Exiting");
            userlog.closeLog();
            return;
        }
        userlog.info("The hash file not matching with any another record");

        //process file and report results
        fileInfoResponse = this.readStatementsFileService.readContent(prnFilename);

        String message = "There are " + fileInfoResponse.getDuplicatedHeaders() + " duplicate headers,"
            + " " + fileInfoResponse.getErrorsHeaders() + " errors headers, "
            + " " + fileInfoResponse.getInsertedHeaders() + " inserts headers \n"
            + " with " + fileInfoResponse.getDuplicatedDetails() + " duplicate details,"
            + " " + fileInfoResponse.getErrorsDetails() + " errors details, "
            + " " + fileInfoResponse.getInsertedDetails() + " inserts details \n";
        userlog.info(message);

        long time = commonUtils.getCurrentRunTime();
        userlog.info("Excecution time: " + time + " ms");
        userlog.closeLog();

    }

    private void displayCommandUsage() {
        System.out.println("usage:  java -jar service-batch-converter-0.1.29.jar "
                               + "[--log=logfile] "
                               + "<file.prn ");

        System.out.println("");
        System.out.println("example:  java -jar target/service-batch-converter-0.1.9999.jar "
                               + "--log=miLog.txt "
                               + "src/main/resources/files/Statement_2.prn ");
    }

    private Boolean processArgs(final String[] args) {
        if (args.length == 0 || args.length > 2) {
            displayCommandUsage();
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                String[] params = args[i].split("=");
                if (params.length > 0 && params[0].equalsIgnoreCase("--log")) {
                    userlogPath = params[1];
                } else {
                    prnFilename = args[i];
                }
            }
        }

        if (prnFilename == null) {
            System.out.println(".prn file was not defined");
        }
        return (userlogPath != null && prnFilename != null);
    }

    private File verifyPrnFile(final String prnFilename) {
        File prnFile = new File(prnFilename);
        if (prnFile.exists()) {
            if (prnFile.isFile() && prnFile.canRead()) {
                //userlog.info("Reading file " + commonUtils.blue() + prnFile.getName() + commonUtils.black());
                userlog.info("Reading file : " + commonUtils.green() + prnFile.getAbsolutePath() + commonUtils.black());
            } else {
                userlog.error(prnFilename + " is not a file or can't read it");
                return null;
            }
        } else {
            log.error("File: " + prnFilename + " does not exist");
            return null;
        }
        return prnFile;
    }

    /*
    public void run2(final String... args) throws Exception {
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        commonUtils.setTitle("BATCH CONVERTER");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        long init = CommonUtils.getInitTime();
        userlog = new Userlog();
        appParams = checkParameters(args);

        if (appParams.size() > 0) {
            String message = "";
            try {
                firtsParamPathFile = (String) appParams.get("file.path");

                String fileName = firtsParamPathFile.substring(firtsParamPathFile.lastIndexOf("/") + 1);
                System.out.println("\nReading file " + fileName + " ... ");
                System.out.println("File path: " + firtsParamPathFile);
                userlog.setProcessedFile(fileName);
                //Make hash file and check if exist
                String hashCodeFile = HashCode.getCodigoHash(firtsParamPathFile);
                Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);

                userlog.info("REPORT:");
                if (!isSaved) {
                    userlog.info("The hash file not matching with any another processed file");
                    //CommonUtils.processing();
                    fileInfoResponse = this.readStatementsFileService.readContent(firtsParamPathFile);
                    message = this.getReportStatus(fileInfoResponse);
                    userlog.info(message);
                } else {
                    //TODO hacer la funcion para logs del usuario en la app
                    message = "The hash file matching with another file alredy processed hash --> " + hashCodeFile;
                    log.warn(message);
                    userlog.info(message);
                }
            } catch (FileNotFoundException e) {
                log.error("File not found " + firtsParamPathFile);
                userlog.info("File not found " + firtsParamPathFile);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            log.error("The file.path parameter is required");
        }

        long time = CommonUtils.getCurrentRunTime();
        userlog.info("Excecution time: " + time + " ms");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        userlog.closeLog();
    }
*/
}
