package cc.viridian.servicebatchconverter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class ServiceBatchConverterApplication {

    public static void main(final String[] args) {

        SpringApplication app = new SpringApplication(ServiceBatchConverterApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    /*@Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
        log.info("NonOptionArgs: {}", args.getNonOptionArgs());
        log.info("OptionNames: {}", args.getOptionNames());

        for (String name : args.getOptionNames()){
            log.info("arg-" + name + "=" + args.getOptionValues(name));
        }

        boolean containsOption = args.containsOption("person.name");
        log.info("Contains person.name: " + containsOption);
    }*/

   /* @Override
    public void run(final String... args) {

        log.info("EXECUTING : command line runner");
           log.info("EXECUTING : Batch converter menu");
        batchConverterMenu.ini(baseFilePath);
    }
    */
}
