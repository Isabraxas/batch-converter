package cc.viridian.servicebatchconverter;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceBatchConverterApplication {

    public static void main(final String[] args) {

        SpringApplication app = new SpringApplication(ServiceBatchConverterApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
