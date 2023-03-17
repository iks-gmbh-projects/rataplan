package de.iks.rataplan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class Main 
{
    public static void main(String[] args) {
        log.info(SpringVersion.getVersion());
        SpringApplication app = new SpringApplication(Main.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
