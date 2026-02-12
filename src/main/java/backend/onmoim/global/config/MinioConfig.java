package backend.onmoim.global.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioConfig {

    @Value("${minio.internal-url:http://onmoim-minio:9000}")  // Docker 내부용
    private String internalUrl;

    @Value("${minio.public-url:https://minio.onmoim.site:9000}")  // 프론트엔드용
    private String publicUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    @Primary  // 업로드/일반 작업용 (내부 네트워크)
    public MinioClient internalMinioClient() {
        return MinioClient.builder()
                .endpoint(internalUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean  // presigned URL 생성용 (공개 URL)
    public MinioClient publicMinioClient() {
        return MinioClient.builder()
                .endpoint(publicUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}