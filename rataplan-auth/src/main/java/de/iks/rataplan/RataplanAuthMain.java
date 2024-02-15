package de.iks.rataplan;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@Slf4j
@EnableWebMvc
@EnableTransactionManagement
public class RataplanAuthMain
{
    public static void main(String[] args) {
        log.info(SpringVersion.getVersion());
        SpringApplication app = new SpringApplication(RataplanAuthMain.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
