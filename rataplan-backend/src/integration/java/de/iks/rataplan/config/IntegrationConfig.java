package de.iks.rataplan.config;

import de.iks.rataplan.mapping.DecisionConverter;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.mapping.crypto.ToEncryptedStringConverter;
import de.iks.rataplan.service.MockCryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.TimeZone;

@Profile("integration")
@PropertySource("classpath:integration.properties")
@RequiredArgsConstructor
@TestConfiguration
public class IntegrationConfig {
    private final MockCryptoService mockCryptoService = new MockCryptoService();
    private final FromEncryptedStringConverter fromEncryptedStringConverter = new FromEncryptedStringConverter(mockCryptoService);
    private final ToEncryptedStringConverter toEncryptedStringConverter = new ToEncryptedStringConverter(mockCryptoService);

	private final Environment environment;

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
        additionalProperties.put("hibernate.show_sql", "true");
        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
        return additionalProperties;
    }
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
	}
}
