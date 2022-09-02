package com.risky.gambling;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("com.risky")
public class Configurations {
	@Bean
    public static DataSource mysqlDataSource() 
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/chillie_ellie");
        dataSource.setUsername("Nhu");
        dataSource.setPassword("Nhu6789@");
        return dataSource;
    }

    @Bean
    public ModelMapper modelMapper() 
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

}

