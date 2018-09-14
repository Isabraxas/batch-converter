package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.service.ParseStatementsFileService;
import cc.viridian.servicebatchconverter.service.ReadStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementDetailService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Service
public class BatchConverterMenu {

    @Autowired
    private StatementHeaderService statementHeaderService;
    @Autowired
    private StatementDetailService statementDetailService;
    @Autowired
    private ParseStatementsFileService parseStatementsFileService;
    @Autowired
    private ReadStatementsFileService readStatementsFileService;

    private List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();

    public void ini(final String baseFilePath) {

        Scanner sn = new Scanner(System.in);
        boolean salir = false;
        int opcion; //Guardaremos la opcion del usuario
        String filePath = baseFilePath;
        String message = "";
        //List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();

        while (!salir) {

            System.out.println("\n******************************************");
            System.out.println("1. Leer un nuevo archivo un local");
            System.out.println("2. Almacenar toda la lista de statements");
            System.out.println("3. Usar archivo de prueba para almacenar los statements en la base de datos");
            System.out.println("4. Probar funcion hash de resumen");
            System.out.println("5. Salir");
            System.out.println(message);
            System.out.println("******************************************");

            try {

                System.out.println("Escribe una de las opciones");
                opcion = sn.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.println("Has seleccionado la opcion 1");

                        System.out.print("Escribe la ruta del archivo: ");
                        filePath = sn.next();
                        System.out.print("Leyendo archivo ...");
                        FileInfoResponse fileInfoResponse = this.readStatementsFileService.readContent(filePath);
                        if (!fileInfoResponse.getHashExist()) {
                            message = "El hash del archivo no coincide con ningun registro almacenado \n"
                                + "pero exiten " + fileInfoResponse.getDuplicatedHeaders() + " headers duplicados\n"
                                + " con " + fileInfoResponse.getDuplicatedDetails() + " details duplicados\n";
                        } else {
                            message = "El hash del archivo coincide con un registro ya almacenado \n";
                        }

                        break;

                    case 2:
                        message = "";
                        System.out.println("Has seleccionado la opcion 2");
                        fileInfoResponse = parseStatementsFileService.parseContent(filePath);
                        message = "Se almacenaron " + fileInfoResponse.getReplacedHeaders() + " headers\n"
                            + " con " + fileInfoResponse.getReplacedDetails() + " details \n";
                        //statementHeaderService.insertToDatabase(statementPayloadList);
                        System.out.println("Statemets almacenados");
                        break;

                    case 3:
                        System.out.println("Has seleccionado la opcion 3");

                        useTestFile(baseFilePath);
                        //int rowsDeleted = statementDetailService.deleteByAccount("A00010002");
                        //log.info("EXECUTING : delete statements: " + rowsDeleted);
                        break;

                    case 4:
                        System.out.println("Has seleccionado la opcion 4");
                        String filePathLocalPrn = "/home/isvar/Documents/statement/service-batch-converter"
                            + "/src/main/resources/files/Statement_1998-01-01_2017-12-31.prn";
                        String filePathExternalPrn = "/home/isvar/Documents/Fix-dummy-bank/vdbanco_viridian"
                            + "/src/main/resources/Files/Statement_Is_test.prn";
                        String filePathExternalPrn2 = "/home/isvar/Documents/Fix-dummy-bank/vdbanco_viridian"
                            + "/src/main/resources/Files/Statement_1998-01-01_2017-12-31.prn";

                        try {
                            String hashLocal = HashCode.getCodigoHash(filePathLocalPrn);
                            System.out.println("Hash MD5 de archivo local: " + hashLocal);
                            System.out.println("Hash MD5 de archivo externo1: " + HashCode
                                .getCodigoHash(filePathExternalPrn));
                            System.out.println("Hash MD5 de archivo externo2: " + HashCode
                                .getCodigoHash(filePathExternalPrn2) + "\n");

                            System.out.println("Comparando archivo externo1 contra hash: " + HashCode
                                .areEqualsFileAndHash(filePathExternalPrn, hashLocal));
                            System.out.println("Comparando archivo externo1 contra archivo local: " + HashCode
                                .compareFileWithFile(filePathExternalPrn, filePathLocalPrn));

                            System.out.println("Comparando archivo externo2 contra hash: " + HashCode
                                .areEqualsFileAndHash(filePathExternalPrn2, hashLocal));
                            System.out.println("Comparando archivo externo2 contra archivo local: " + HashCode
                                .compareFileWithFile(filePathExternalPrn2, filePathLocalPrn));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        break;

                    case 5:
                        salir = true;
                        break;

                    default:
                        System.out.println("\nSolo números entre 1 y 5\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nDebes insertar un número\n");
                sn.next();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public void useTestFile(final String baseFilePath) throws IOException {

        //TODO Fix try catch here
        try {
            FileInfoResponse fileInfoResponse = parseStatementsFileService.parseContent(baseFilePath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        statementHeaderService.insertToDatabase(statementPayloadList);
    }
}
