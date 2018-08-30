package cc.viridian.servicebatchconverter.run;

import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.service.ParseStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementDetailService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public void ini(String baseFilePath){

        Scanner sn = new Scanner(System.in);
        boolean salir = false;
        int opcion; //Guardaremos la opcion del usuario
        String filePath = baseFilePath;
        List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();

        while (!salir) {

            System.out.println("1. Parsear archivo local");
            System.out.println("2. Alamacenar los statement headers");
            System.out.println("3. Alamacenar los statement details");
            System.out.println("4. Alamacenar toda la lista de statements");
            System.out.println("5. Usar archivo de prueba para almacenar los statements en la base de datos");
            System.out.println("6. Salir");

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

                            statementPayloadList = parseStatementsFileService.parseContent(baseFilePath);
                            statementHeaderService.insertToDatabase(statementPayloadList);
                            statementDetailService.insertToDatabase(statementPayloadList);
                            //int rowsDeleted = statementDetailService.deleteByAccount("A00010002");
                            //log.info("EXECUTING : delete statements: " + rowsDeleted);
                        break;

                    case 6:
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
            }
        }


    }



}
