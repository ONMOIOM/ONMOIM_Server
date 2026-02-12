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

// AI로 마이그레이션
@Slf4j
@Service
@RequiredArgsConstructor  // 단일 클라이언트로 단순화!
public class MinioUtil {  // 이름 변경!

    private final MinioClient ociClient;  // 단일 OCI 클라이언트

    @Value("${oci.bucket}")
    private String bucket;

    public void uploadProfileImage(MultipartFile file, Long userId) {
        try {
            log.info("OCI 프로필 이미지 업로드 - userId: {}, bucket: {}", userId, bucket);

            String filename = String.format("user/profile/%d/profile", userId);
            log.info("업로드 경로: {}", filename);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)  // OCI 최적화
                    .build();

            ociClient.putObject(putArgs);
            log.info("✅ OCI 업로드 성공 - userId: {}", userId);

        } catch (Exception e) {
            log.error("OCI 업로드 실패 - userId: {}", userId, e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void uploadEventImage(MultipartFile file, Long eventId) {
        try {
            log.info("OCI 행사 이미지 업로드 - eventId: {}", eventId);

            String filename = String.format("event/%d/thumbnail", eventId);

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build();

            ociClient.putObject(putArgs);
            log.info("✅ OCI 업로드 성공 - eventId: {}", eventId);

        } catch (Exception e) {
            log.error("OCI 업로드 실패 - eventId: {}", eventId, e);
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String getProfileImageUrl(Long userId) {
        String filename = String.format("user/profile/%d/profile", userId);
        return getPresignedUrl(filename, userId, "profile");
    }

    public String getEventImageUrl(Long eventId) {
        String filename = String.format("event/%d/thumbnail", eventId);
        return getPresignedUrl(filename, eventId, "event");
    }

    // 공통 presigned URL 생성 (statObject 제거 - OCI 최적화)
    private String getPresignedUrl(String filename, Long id, String type) {
        try {
            String url = ociClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)  // 7일
                            .build()
            );
            log.info("{} URL 생성 성공 (ID: {}): {}", type, id, url.substring(0, 50) + "...");
            return url;
        } catch (Exception e) {
            log.warn("{} 이미지 없음 (ID: {}): {}", type, id, filename);
            return null;  // 파일 없으면 null 정상
        }
    }

    // 버킷 자동 생성 (OCI에서 1회 실행)
    private void ensureBucketExists() {
        try {
            if (!ociClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                log.info("OCI 버킷 생성: {}", bucket);
                ociClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            log.warn("버킷 확인/생성 스킵 (이미 존재): {}", e.getMessage());
        }
    }
}
