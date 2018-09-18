package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.service.ReadStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
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

    private String firtsParamPathFile = "";
    FileInfoResponse fileInfoResponse;

    @Override
    public void run(final String... args) throws Exception {
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
                    String fileName = firtsParamPathFile.substring(firtsParamPathFile.lastIndexOf("/") + 1);
                    System.out.println("Reading file " + fileName + " ... ");
                    System.out.println("File path: " + firtsParamPathFile);
                    //Make hash file and check if exist
                    String hashCodeFile = HashCode.getCodigoHash(firtsParamPathFile);
                    Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);
                    if (!isSaved) {
                        fileInfoResponse = this.readStatementsFileService.readContent(firtsParamPathFile);
                        message = "The hash file not matching with any another record \n"
                            + "but there are " + fileInfoResponse.getDuplicatedHeaders() + " duplicate headers,"
                            + " " + fileInfoResponse.getReplacedHeaders() + " inserts headers \n"
                            + " with " + fileInfoResponse.getDuplicatedDetails() + " duplicate details,"
                            + " " + fileInfoResponse.getReplacedDetails() + " inserts details \n";
                    } else {
                        //TODO hacer la funcion para logs del usuario en la app
                        log.warn("The hash file matching with another file alredy prosecced hash --> " + hashCodeFile);
                        message = "The hash file matching with another file alredy prosecced";
                    }

                    System.out.println(message);
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
        System.out.println("Excecution time: " + time + " ms");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
    }

    private Boolean checkParameters(final String[] args) {
        Boolean isValid = false;
        try {
            String[] params = args[0].split("=");
            if (params[0].contains("file.path")) {
                isValid = true;
            } else {
                log.error("The parameter is invalid");
                isValid = false;
            }
        } catch (Exception e) {
            log.error("The file.path parameter is required");
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return isValid;
    }
}
