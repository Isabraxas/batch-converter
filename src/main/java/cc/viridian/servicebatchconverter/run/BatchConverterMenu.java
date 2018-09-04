package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.service.ParseStatementsFileService;
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

    private List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();

    public void ini(String baseFilePath) {

        Scanner sn = new Scanner(System.in);
        boolean salir = false;
        int opcion; //Guardaremos la opcion del usuario
        String filePath = baseFilePath;
        //List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();

        while (!salir) {

            System.out.println("\n******************************************");
            System.out.println("1. Parsear archivo un local");
            System.out.println("2. Almacenar los statement headers");
            System.out.println("3. Almacenar los statement details");
            System.out.println("4. Almacenar toda la lista de statements");
            System.out.println("5. Usar archivo de prueba para almacenar los statements en la base de datos");
            System.out.println("6. Probar funcion hashde resumen");
            System.out.println("7. Salir");
            System.out.println("******************************************");

            try {

                System.out.println("Escribe una de las opciones");
                opcion = sn.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.println("Has seleccionado la opcion 1");

                        System.out.print("Escribe la ruta del archivo: ");
                        filePath = sn.next();
                        statementPayloadList = parseStatementsFileService.parseContent(filePath);
                        break;

                    case 2:
                        System.out.println("Has seleccionado la opcion 2");
                        statementPayloadList = parseStatementsFileService.parseContent(filePath);
                        statementHeaderService.insertToDatabase(statementPayloadList);
                        System.out.println("Headers almacenados");
                        break;

                    case 3:
                        System.out.println("Has seleccionado la opcion 3");
                        statementPayloadList = parseStatementsFileService.parseContent(filePath);
                        statementDetailService.insertToDatabase(statementPayloadList);
                        System.out.println("Details almacenados");
                        break;

                    case 4:
                        System.out.println("Has seleccionado la opcion 4");
                        statementPayloadList = parseStatementsFileService.parseContent(filePath);
                        statementHeaderService.insertToDatabase(statementPayloadList);
                        statementDetailService.insertToDatabase(statementPayloadList);
                        System.out.println("Statemets almacenados");
                        break;

                    case 5:
                        System.out.println("Has seleccionado la opcion 5");

                        useTestFile(baseFilePath);
                        //int rowsDeleted = statementDetailService.deleteByAccount("A00010002");
                        //log.info("EXECUTING : delete statements: " + rowsDeleted);
                        break;

                    case 6:
                        System.out.println("Has seleccionado la opcion 5");
                        String filePathLocalPrn= "/home/isvar/Documents/statement/service-batch-converter" +
                            "/src/main/resources/files/Statement_1998-01-01_2017-12-31.prn";
                        String filePathExternalPrn= "/home/isvar/Documents/Fix-dummy-bank/vdbanco_viridian" +
                            "/src/main/resources/Files/Statement_Is_test.prn";
                        String filePathExternalPrn2= "/home/isvar/Documents/Fix-dummy-bank/vdbanco_viridian" +
                            "/src/main/resources/Files/Statement_1998-01-01_2017-12-31.prn";

                        try {
                            String hashLocal= HashCode.getCodigoHash(filePathLocalPrn);
                            System.out.println("Hash MD5 de archivo local: "+ hashLocal);
                            System.out.println("Hash MD5 de archivo externo1: "+ HashCode
                                .getCodigoHash(filePathExternalPrn));
                            System.out.println("Hash MD5 de archivo externo2: "+ HashCode
                                .getCodigoHash(filePathExternalPrn2) + "\n");

                            System.out.println("Comparando archivo externo1 contra hash: "+ HashCode
                                .compareFileWithHash(filePathExternalPrn, hashLocal));
                            System.out.println("Comparando archivo externo1 contra archivo local: "+ HashCode
                                .compareFileWithFile(filePathExternalPrn, filePathLocalPrn));

                            System.out.println("Comparando archivo externo2 contra hash: "+ HashCode
                                .compareFileWithHash(filePathExternalPrn2, hashLocal));
                            System.out.println("Comparando archivo externo2 contra archivo local: "+ HashCode
                                .compareFileWithFile(filePathExternalPrn2, filePathLocalPrn));

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        break;

                    case 7:
                        salir = true;
                        break;

                    default:
                        System.out.println("\nSolo números entre 1 y 6\n");
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

    public void useTestFile(String baseFilePath) throws IOException {
        //baseFilePath = "/home/isvar/Documents/statement/service-batch-converter/target/classes/files/Statement_1998-01-01_2017-12-31.prn";
        //TODO Fix try catch here
        try {
            statementPayloadList = parseStatementsFileService.parseContent(baseFilePath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        statementHeaderService.insertToDatabase(statementPayloadList);
        statementDetailService.insertToDatabase(statementPayloadList);
    }
}
