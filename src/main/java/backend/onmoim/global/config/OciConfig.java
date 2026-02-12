package backend.onmoim.global.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OciConfig {

    @Value("${oci.endpoint}")
    private String endpoint;

    @Value("${oci.region:ap-osaka-1}")
    private String region;

    @Value("${oci.access-key}")
    private String accessKey;

    @Value("${oci.secret-key}")
    private String secretKey;

    @Bean
    @Primary
    public MinioClient ociClient() {
        return MinioClient.builder()
                .endpoint(endpoint)  // OCI S3 endpoint
                .credentials(accessKey, secretKey)
                .region(region)
                .build();
    }
}
