package de.iks.rataplan.config;

import lombok.Setter;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Necessary until flyway can be updated to version expected by spring 2.x.x
 */
@Configuration
@ConfigurationProperties("flyway")
@Setter
public class FlywayMigrationConfig {
    private String[] locations = new String[0];
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway ret = new Flyway();
        ret.setDataSource(dataSource);
        ret.setLocations(locations);
        return ret;
    }
    
    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway);
    }
    
    @Configuration
    public static class FlywayInitializerJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
        public FlywayInitializerJpaDependencyConfiguration() {
            super(FlywayMigrationInitializer.class);
        }
    }
}
