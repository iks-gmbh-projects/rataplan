package iks.surveytool.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@ConditionalOnProperty("spring.datasource.password-file")
@ConfigurationProperties("spring.datasource")
@RequiredArgsConstructor
@Data
public class DataSourceConfig {
    private String url;
    private String driverClassName;
    private String username;
    private Path passwordFile;
    
    @Bean
    public DataSource dataSource() throws IOException {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        if(driverClassName != null) builder.driverClassName(driverClassName);
        return builder
            .url(url)
            .username(username)
            .password(Files.readString(passwordFile))
            .build();
    }
}