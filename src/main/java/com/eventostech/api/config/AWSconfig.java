package com.eventostech.api.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSconfig {

    @Value("${aws.region}")
    private String awsRegion; // Região da AWS a ser configurada para o cliente S3

    /**
     * Cria uma instância do cliente Amazon S3 configurada com a região especificada.
     *
     * @return Instância configurada do cliente Amazon S3.
     */
    @Bean
    public AmazonS3 createS3Instance(){
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();
    }
}
