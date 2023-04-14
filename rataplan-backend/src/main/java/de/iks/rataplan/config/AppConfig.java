package de.iks.rataplan.config;

import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.AppointmentRequestDTO;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.mapping.crypto.ToEncryptedStringConverter;
import io.swagger.models.Model;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.google.gson.Gson;

import de.iks.rataplan.mapping.DecisionConverter;

@Profile({"dev", "prod", "test", "integration"})
@Configuration
@PropertySource({ "classpath:/application.properties" })
@ComponentScan(basePackages = "de.iks.rataplan")
@EnableTransactionManagement
public class AppConfig {

	@Autowired
	private Environment environment;

	@Autowired
	private DecisionConverter decisionConverter;

	@Bean
	public DataSource dataSource() {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(environment.getProperty("JDBC_DATABASE_DRIVER"));
			dataSource.setUrl(environment.getProperty("JDBC_DATABASE_URL"));
			dataSource.setUsername(environment.getProperty("JDBC_DATABASE_USERNAME"));
			dataSource.setPassword(environment.getProperty("JDBC_DATABASE_PASSWORD"));
			return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setPackagesToScan(environment.getProperty("entitymanager.packagesToScan"));

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

		entityManagerFactory.setJpaProperties(additionalHibernateProperties());
		return entityManagerFactory;
	}

	@Bean
	public Properties additionalHibernateProperties() {
		Properties additionalProperties = new Properties();
		additionalProperties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
		additionalProperties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
		additionalProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
		return additionalProperties;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

	@Bean
	public BeanPostProcessor persistenceTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	/**
	 * ModelMapper for mapping between DAO and DTO beans
	 *
	 * toDAO and toDTO converters are just for mapping the AppointmentDecisions.
	 * The toDAO converter accesses the database to get the appointments
	 * according to the id's. Keep this in mind when writing tests!
	 *
	 * @return ModelMapper instance
	 */
	@Bean
	@Qualifier(value = "authModelMapper")
	public ModelMapper modelMapper(ToEncryptedStringConverter toEncryptedStringConverter, FromEncryptedStringConverter fromEncryptedStringConverter) {
		ModelMapper mapper = new ModelMapper();
		mapper.addConverter(decisionConverter.toDAO);
		mapper.addConverter(decisionConverter.toDTO);
		mapper.addConverter(toEncryptedStringConverter);
		mapper.addConverter(fromEncryptedStringConverter);
		return mapper;
	}

	@Bean
	@Qualifier(value = "participantModelMapper")
	public ModelMapper modelMapper (){
		ModelMapper modelMapper = new ModelMapper();
		TypeMap<AppointmentRequest, AppointmentRequestDTO> typeMap = modelMapper.createTypeMap(AppointmentRequest.class, AppointmentRequestDTO.class);
		typeMap.addMappings(mapper -> mapper.skip(AppointmentRequestDTO::setEditToken));
		return modelMapper;
	}

	@Bean
	public TemplateEngine emailTemplateEngine() {
		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(htmlTemplateResolver());
		return templateEngine;
	}

	private ITemplateResolver htmlTemplateResolver() {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setOrder(Integer.valueOf(2));
		templateResolver.setPrefix("/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(false);
		return templateResolver;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		DatabaseConfigBean dbConfig = new com.github.springtestdbunit.bean.DatabaseConfigBean();
		dbConfig.setDatatypeFactory(new org.dbunit.ext.h2.H2DataTypeFactory());
		return dbConfig;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean dbConnection = new com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean(
				dataSource());
		dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
		return dbConnection;
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
