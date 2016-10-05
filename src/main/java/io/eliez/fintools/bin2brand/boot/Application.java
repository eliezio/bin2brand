package io.eliez.fintools.bin2brand.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "io.eliez.fintools.bootext",
        "io.eliez.fintools.bin2brand.boot"
})
public class Application {  //NOSONAR

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);     //NOSONAR
    }
}
