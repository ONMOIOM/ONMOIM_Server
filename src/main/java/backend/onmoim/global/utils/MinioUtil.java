package backend.onmoim.global.utils;

import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class MinioUtil {

    @Qualifier("internalMinioClient")
    private final MinioClient internalClient;  // 업로드용

    @Qualifier("publicMinioClient")
    private final MinioClient publicClient;    // presigned URL용

    @Value("${minio.bucket}")
    private String bucket;

    public MinioUtil(
            @Qualifier("internalMinioClient") MinioClient internalClient,
            @Qualifier("publicMinioClient") MinioClient publicClient
    ) {
        this.internalClient = internalClient;
        this.publicClient = publicClient;
    }

    public void uploadProfileImage(MultipartFile file, Long userId) {
        try {
            log.info("프로필 이미지 업로드 시작 - userId: {}, bucket: {}", userId, bucket);
            ensureBucketExists(internalClient);

            String filename = String.format("user/profile/%d/profile", userId);
            log.info("업로드 파일명: {}", filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            internalClient.putObject(putArgs);  // 내부 클라이언트로 업로드
            log.info("프로필 이미지 업로드 성공 - userId: {}", userId);

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void uploadEventImage(MultipartFile file, Long eventId) {
        try {
            log.info("행사 이미지 업로드 시작 - eventId: {}, bucket: {}", eventId, bucket);
            ensureBucketExists(internalClient);

            String filename = String.format("event/%d/thumbnail", eventId);
            log.info("업로드 파일명: {}", filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            internalClient.putObject(putArgs);  // 내부 클라이언트로 업로드
            log.info("행사 이미지 업로드 성공 - eventId: {}", eventId);

        } catch (Exception e) {
            log.error("행사 이미지 업로드 실패 - eventId: {}, error: {}", eventId, e.getMessage(), e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String getProfileImageUrl(Long userId) {
        String filename = String.format("user/profile/%d/profile", userId);

        try {
            String url = publicClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );

            log.info("Profile URL 생성 성공: {}", url.substring(0, 50) + "...");
            return url;

        } catch (Exception e) {
            log.warn("URL 생성 실패 (파일 없음 가능성): userId={}, filename={}", userId, filename);
            return null;
        }
    }

    public String getEventImageUrl(Long eventId) {
        try {
            String filename = String.format("event/%d/thumbnail", eventId);

            // 파일 존재 여부 확인 (내부 클라이언트)
            internalClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            // 공개 URL로 presigned URL 생성 (public 클라이언트)
            String url = publicClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );

            log.debug("행사 이미지 URL 생성 성공 - eventId: {}", eventId);
            return url;

        } catch (Exception e) {
            log.warn("행사 이미지 URL 생성 실패 - eventId: {}, error: {}", eventId, e.getMessage());
            return null;
        }
    }

    private void ensureBucketExists(MinioClient client) throws Exception {
        boolean exists = client.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        log.debug("버킷 존재 여부 - bucket: {}, exists: {}", bucket, exists);

        if (!exists) {
            log.info("버킷 생성 중 - bucket: {}", bucket);
            client.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
            log.info("버킷 생성 완료 - bucket: {}", bucket);
        }
    }
}