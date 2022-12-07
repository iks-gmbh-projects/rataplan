package de.iks.rataplan.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.springframework.web.client.RestTemplate;

@Profile({"dev", "prod", "test", "integration"})
@Configuration
@PropertySource({ "classpath:/application.properties" })
@ComponentScan(basePackages="de.iks.rataplan")
@EnableTransactionManagement
public class AppConfig {
	
	@Autowired
	private Environment environment;
	
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
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    public RestTemplate restTemplate() {
       return new RestTemplate();
    }
}
