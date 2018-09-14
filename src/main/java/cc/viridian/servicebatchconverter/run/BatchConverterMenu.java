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
            System.out.println("FILE_PATH: "+ baseFilePath);
            System.out.println("1. Leer un nuevo archivo un local");
            System.out.println("2. Usar archivo de prueba para almacenar los statements en la base de datos");
            System.out.println("3. Salir");
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
                        System.out.println("Has seleccionado la opcion 3");
                        useTestFile(baseFilePath);
                        break;

                    case 3:
                        salir = true;
                        break;

                    default:
                        System.out.println("\nSolo números entre 1 y 3\n");
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
            FileInfoResponse fileInfoResponse = readStatementsFileService.readContent(baseFilePath);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        statementHeaderService.insertToDatabase(statementPayloadList);
    }
}
