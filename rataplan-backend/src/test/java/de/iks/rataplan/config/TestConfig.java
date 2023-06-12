package de.iks.rataplan.config;

import de.iks.rataplan.mapping.DecisionConverter;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.mapping.crypto.ToEncryptedStringConverter;
import de.iks.rataplan.service.MockCryptoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.TimeZone;

@Profile("test")
@PropertySource("classpath:/test.properties")
@RequiredArgsConstructor
public class TestConfig {

	private final Environment environment;

    private final MockCryptoService mockCryptoService = new MockCryptoService();
    private final FromEncryptedStringConverter fromEncryptedStringConverter = new FromEncryptedStringConverter(mockCryptoService);
    private final ToEncryptedStringConverter toEncryptedStringConverter = new ToEncryptedStringConverter(mockCryptoService);

    @Bean
    public ModelMapper modelMapper(DecisionConverter decisionConverter) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(decisionConverter.toDAO);
        mapper.addConverter(decisionConverter.toDTO);
        mapper.addConverter(toEncryptedStringConverter);
        mapper.addConverter(fromEncryptedStringConverter);
        return mapper;
    }

	@Bean
	public HttpServletResponse httpServletResponse() {
		return new MockHttpServletResponse();
	}
	
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.H2)
                .build();
        return db;
    }
    
    @Bean 
    public Properties additionalHibernateProperties() {
    	Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        additionalProperties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
        return additionalProperties;
    }
   
    @PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2")); 
	}
    
}
