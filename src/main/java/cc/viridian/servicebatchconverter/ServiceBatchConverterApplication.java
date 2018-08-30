package cc.viridian.servicebatchconverter;

import cc.viridian.servicebatchconverter.payload.StatementPayload;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ServiceBatchConverterApplication implements CommandLineRunner {

	@Value("${file.path}")
	private String baseFilePath;

	@Autowired
	private StatementHeaderService statementHeaderService;
	@Autowired
	private StatementDetailService statementDetailService;
	@Autowired
	private ParseStatementsFileService parseStatementsFileService;

	private static Logger log = LoggerFactory.getLogger(ServiceBatchConverterApplication.class);


	public static void main(String[] args) {

		SpringApplication.run(ServiceBatchConverterApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args[{}]: {}", i, args[i]);
		}

		log.info("EXECUTING : insert to database");
		List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();
		String filePath = baseFilePath;
		try {
			statementPayloadList = parseStatementsFileService.parseContent(filePath);

			statementHeaderService.insertToDatabase(statementPayloadList);
			statementDetailService.insertToDatabase(statementPayloadList);
			int rowsDeleted = statementDetailService.deleteByAccount("A00010002");
			log.info("EXECUTING : delete statements: " + rowsDeleted);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
