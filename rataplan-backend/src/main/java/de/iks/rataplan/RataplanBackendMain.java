package de.iks.rataplan;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RataplanBackendMain {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RataplanBackendMain.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

    @PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2")); 
	}
}
