package de.iks.rataplan.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Profile("test")
@PropertySource("classpath:/test.properties")
public class TestConfig {

	@Autowired
	Environment environment;

	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.H2).build();
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
}
