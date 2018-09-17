package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.service.ReadStatementsFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
public class BatchConverterRun implements CommandLineRunner {

    @Autowired
    private ReadStatementsFileService readStatementsFileService;

    private String firtsParamPathFile = "";

    @Override
    public void run(final String... args) throws Exception {
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        long init = System.currentTimeMillis();

        System.out.println("ARGS = " + args[0].toString());
        if (args.length > 0) {
            String message = "";
            firtsParamPathFile = args[0].split("=")[1];
            if (checkParameters(args)) {
                try {
                    System.out.print("Reading file ...");
                    FileInfoResponse fileInfoResponse = this.readStatementsFileService.readContent(firtsParamPathFile);
                    if (!fileInfoResponse.getHashExist()) {
                        message = "The hash file not matching with any another record \n"
                            + "but there are " + fileInfoResponse.getDuplicatedHeaders() + " duplicate headers\n"
                            + " with " + fileInfoResponse.getDuplicatedDetails() + " duplicate details\n";
                    } else {
                        message = "The hash file matching with one record \n";
                    }

                    System.out.print(message);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }

        long fin = System.currentTimeMillis();
        long time = (fin - init);
        System.out.println("Excecution time: " + time + " ml");
        System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
    }

    private Boolean checkParameters(final String[] args) {
        String[] params = args[0].split("=");
        Boolean isValid = false;
        if (params[0].contains("file.path")) {
            isValid = true;
        }
        return isValid;
    }
}
