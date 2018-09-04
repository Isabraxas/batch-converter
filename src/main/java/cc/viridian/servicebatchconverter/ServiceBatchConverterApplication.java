package cc.viridian.servicebatchconverter;


import cc.viridian.servicebatchconverter.run.BatchConverterMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


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
		baseFilePath= ServiceBatchConverterApplication.class.getResource("/files/Statement_1998-01-01_2017-12-31.prn").getPath();
		log.info("EXECUTING : Batch converter menu");
		batchConverterMenu.ini(baseFilePath);

	}
}
