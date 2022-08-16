package de.iks.rataplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main 
{
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
