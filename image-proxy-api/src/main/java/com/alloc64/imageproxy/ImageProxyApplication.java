package com.alloc64.imageproxy;

import io.minio.MinioClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
@SpringBootApplication
public class ImageProxyApplication {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Bean
	MinioClient getMinioClient(@Value("${image.proxy.s3.endpoint}") String endpoint,
							   @Value("${image.proxy.s3.accesskey}") String accessKey,
							   @Value("${image.proxy.s3.secretkey}") String secretKey) {
		return MinioClient.builder()
				.endpoint(endpoint)
				.credentials(accessKey, secretKey)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ImageProxyApplication.class, args);
	}

}
