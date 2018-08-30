package cc.viridian.servicebatchconverter;


import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.run.BatchConverterMenu;
import cc.viridian.servicebatchconverter.service.ParseStatementsFileService;
import cc.viridian.servicebatchconverter.service.StatementDetailService;
import cc.viridian.servicebatchconverter.service.StatementHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ServiceBatchConverterApplication implements CommandLineRunner {

	@Value("${file.path}")
	private String baseFilePath;

	@Autowired
	private BatchConverterMenu batchConverterMenu;

	private static Logger log = LoggerFactory.getLogger(ServiceBatchConverterApplication.class);


	public static void main(String[] args) {

		SpringApplication.run(ServiceBatchConverterApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("EXECUTING : command line runner");

		log.info("EXECUTING : Batch converter menu");
		batchConverterMenu.ini(baseFilePath);

	}
}
