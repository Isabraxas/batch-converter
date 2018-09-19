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

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        commonUtils.setTitle("BATCH CONVERTER");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        long init = System.currentTimeMillis();

        if (args.length > 0) {
            String message = "";
            try {
                firtsParamPathFile = args[0].split("=")[1];
                //firtsParamPathFile = "/home/isvar/Documents/statement/service-batch-converter" +
                //  "/src/main/resources/files/Statement_2.prn";
            } catch (Exception e) {
                log.error("Error format in file.path parameter");
                log.error(e.getMessage());
                e.printStackTrace();
            }
            if (checkParameters(args)) {
                try {
                    userlog = new Userlog();
                    String fileName = firtsParamPathFile.substring(firtsParamPathFile.lastIndexOf("/") + 1);
                    System.out.println("Reading file " + fileName + " ... ");
                    System.out.println("File path: " + firtsParamPathFile);
                    userlog.setProcessedFile(fileName);
                    commonUtils.expectedTime(firtsParamPathFile);
                    //Make hash file and check if exist
                    String hashCodeFile = HashCode.getCodigoHash(firtsParamPathFile);
                    Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);
                    if (!isSaved) {
                        userlog.info("The hash file not matching with any another processed file");
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
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.error("The file.path parameter is required");
        }

        long fin = System.currentTimeMillis();
        long time = (fin - init);
        userlog.info("Excecution time: " + time + " ms");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        userlog.closeLog();
    }

    private Boolean checkParameters(final String[] args) {
        Boolean isValid = false;
        try {
            String[] params = args[0].split("=");
            if (params[0].contains("file.path")) {
                isValid = true;
            } else {
                log.error("The first parameter is invalid");
                isValid = false;
            }
        } catch (Exception e) {
            log.error("The file.path parameter is required");
            log.error(e.getMessage());
            e.printStackTrace();
        }

        try {
            String[] params = args[1].split("=");
            if (params[0].contains("file.log.path")) {
                isValid = true;
            } else {
                log.error("The seccond parameter is invalid");
                isValid = false;
            }
        } catch (Exception e) {
            log.error("The file.log.path parameter is required");
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return isValid;
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
