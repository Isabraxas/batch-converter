package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.service.ReadStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
import cc.viridian.servicebatchconverter.utils.CommonUtils;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
import cc.viridian.servicebatchconverter.writer.Userlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@Slf4j
@Service
public class BatchConverterRun implements CommandLineRunner {

    @Autowired
    private ReadStatementsFileService readStatementsFileService;
    @Autowired
    private StatementHeaderService statementHeaderService;

    private CommonUtils commonUtils = new CommonUtils();
    private String firtsParamPathFile = "";
    private FileInfoResponse fileInfoResponse;
    private Userlog userlog;
    HashMap<String, Object> appParams = new HashMap<>();

    @Override
    public void run(final String... args) throws Exception {
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

    private HashMap<String, Object> checkParameters(final String[] args) throws IOException {

        HashMap<String, Object> hasParams = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String[] params = args[i].split("=");
            switch (params[0]) {
                case "--file.log.path":
                    try {
                        if (params[0].contains("--file.log.path")) {
                            hasParams.put("file.log.path", params[1]);
                            userlog = new Userlog((String) hasParams.get("file.log.path"));
                            userlog.info("New file path to save user log : " + (String) hasParams.get("file.log.path"));
                        } else {
                            log.error("The seccond parameter is invalid");
                            userlog.info("The seccond parameter is invalid");
                        }
                    } catch (Exception e) {
                        log.error("The file.log.path parameter is required");
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    try {
                        if (params[0].contains("--file.path") && i == 0) {
                            hasParams.put("file.path", params[1]);
                        } else {
                            log.error("The first parameter is invalid");
                            userlog.info("The first parameter is invalid");
                        }
                    } catch (Exception e) {
                        log.error("The file.path parameter is required");
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return hasParams;
    }

    private String getReportStatus(final FileInfoResponse response) {
        String message = "";
        Integer newsHeaders = response.getInsertedHeaders() - response.getDuplicatedHeaders();
        Integer newsDetails = response.getInsertedDetails() - response.getDuplicatedDetails();
        int statemetCol = 20, headersCol = 20, detailsCol = 20;

        message += String.valueOf(FormatUtil.returnDelimArray("Statement", statemetCol));
        message += String.valueOf(FormatUtil.returnDelimArray("Headers", headersCol));
        message += String.valueOf(FormatUtil.returnDelimArray("Details", detailsCol)) + "\n";

        message += String.valueOf(FormatUtil.returnDelimArray("News", statemetCol));
        message += String.valueOf(FormatUtil.returnDelimArray(newsHeaders.toString(), headersCol));
        message += String.valueOf(FormatUtil.returnDelimArray(newsDetails.toString(), detailsCol)) + "\n";

        message += String.valueOf(FormatUtil.returnDelimArray("Duplicates", statemetCol));
        message += String.valueOf(FormatUtil.returnDelimArray(response.getDuplicatedHeaders().toString(), headersCol));
        message += String.valueOf(
            FormatUtil.returnDelimArray(response.getDuplicatedDetails().toString(), detailsCol)) + "\n";

        message += "---------------------------------------------------------\n";

        message += String.valueOf(FormatUtil.returnDelimArray("TOTAL", statemetCol));
        message += String.valueOf(FormatUtil.returnDelimArray(response.getInsertedHeaders().toString(), headersCol));
        message += String.valueOf(
            FormatUtil.returnDelimArray(response.getInsertedDetails().toString(), detailsCol)) + "\n";

        message += this.getReportErros(response);

        return message;
    }

    private String getReportErros(final FileInfoResponse response) {
        String message = "";
        int statemetCol = 20, headersCol = 20, detailsCol = 20;

        message += "\n";
        message += String.valueOf(FormatUtil.returnDelimArray("Errros", statemetCol));
        message += String.valueOf(FormatUtil.returnDelimArray(response.getErrorsHeaders().toString(), headersCol));
        message += String.valueOf(
            FormatUtil.returnDelimArray(response.getErrorsDetails().toString(), detailsCol)) + "\n";

        return message;
    }
}
