package backend.onmoim.global.utils;

import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.public-url}")
    private String publicUrl;

    public void uploadProfileImage(MultipartFile file, Long userId) {
        try {
            log.info("프로필 이미지 업로드 시작 - userId: {}, bucket: {}", userId, bucket);
            ensureBucketExists();

            String filename = String.format("user/profile/%d/profile", userId);
            log.info("업로드 파일명: {}", filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            minioClient.putObject(putArgs);
            log.info("프로필 이미지 업로드 성공 - userId: {}", userId);

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void uploadEventImage(MultipartFile file, Long eventId) {
        try {
            log.info("행사 이미지 업로드 시작 - eventId: {}, bucket: {}", eventId, bucket);
            ensureBucketExists();

            String filename = String.format("event/%d/thumbnail", eventId);
            log.info("업로드 파일명: {}", filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            minioClient.putObject(putArgs);
            log.info("행사 이미지 업로드 성공 - eventId: {}", eventId);

        } catch (Exception e) {
            log.error("행사 이미지 업로드 실패 - eventId: {}, error: {}", eventId, e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String getProfileImageUrl(Long userId) {
        try {
            ensureBucketExists();
            
            String filename = String.format("user/profile/%d/profile", userId);

            // 파일 존재 여부 확인
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
            log.debug("프로필 이미지 URL 생성 성공 - userId: {}", userId);
            return url.replace("http://168.138.41.19:9000", publicUrl);
        } catch (Exception e) {
            log.warn("프로필 이미지 URL 생성 실패 - userId: {}, error: {}", userId, e.getMessage());
            return null;
        }
    }

    public String getEventImageUrl(Long eventId) {
        try {
            ensureBucketExists();
            
            String filename = String.format("event/%d/thumbnail", eventId);

            // 파일 존재 여부 확인
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
            log.debug("행사 이미지 URL 생성 성공 - eventId: {}", eventId);
            return url.replace("http://168.138.41.19:9000", publicUrl);
        } catch (Exception e) {
            log.warn("행사 이미지 URL 생성 실패 - eventId: {}, error: {}", eventId, e.getMessage());
            return null;
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        log.debug("버킷 존재 여부 - bucket: {}, exists: {}", bucket, exists);
        
        if (!exists) {
            log.info("버킷 생성 중 - bucket: {}", bucket);
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
            log.info("버킷 생성 완료 - bucket: {}", bucket);
        }
    }

}
