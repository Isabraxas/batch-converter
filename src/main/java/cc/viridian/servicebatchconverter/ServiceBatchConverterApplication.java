package cc.viridian.servicebatchconverter;

import cc.viridian.servicebatchconverter.run.BatchConverterMenu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ServiceBatchConverterApplication implements CommandLineRunner {

    @Value("${file.path}")
    private String baseFilePath;

    @Autowired
    private BatchConverterMenu batchConverterMenu;
    public static void main(final String[] args) {

        SpringApplication.run(ServiceBatchConverterApplication.class, args);
    }

    @Override
    public void run(final String... args) {

        log.info("EXECUTING : command line runner");
        //baseFilePath = ServiceBatchConverterApplication.class.getResource(
        //    "/files/Statement_1998-01-01_2017-12-31.prn").getPath();
        log.info("EXECUTING : Batch converter menu");
        batchConverterMenu.ini(baseFilePath);
    }
}
