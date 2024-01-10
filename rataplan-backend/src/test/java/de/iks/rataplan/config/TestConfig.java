package de.iks.rataplan.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.TimeZone;

@TestConfiguration
public class TestConfig {
	@Bean
	public HttpServletResponse httpServletResponse() {
		return new MockHttpServletResponse();
	}
	
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
   
    @PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2")); 
	}
    
}
