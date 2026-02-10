package backend.onmoim.global.utils;

import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public void uploadProfileImage(MultipartFile file, Long userId) {
        try {
            ensureBucketExists();

            String filename = String.format("user/profile/%d/profile", userId); // 사용자마다 가상 폴더 생성

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            minioClient.putObject(putArgs);

        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void uploadEventImage(MultipartFile file, Long eventId) {
        try {
            ensureBucketExists();

            String filename = String.format("event/%d/thumbnail", eventId); // 행사마다 가상 폴더 생성

            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), 10 * 1024 * 1024)
                    .build();

            minioClient.putObject(putArgs);

        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public String getProfileImageUrl(Long userId) {
        try {
            String filename = String.format("user/profile/%d/profile", userId);

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE);
        }
    }

    public String getEventImageUrl(Long eventId) {
        try {
            String filename = String.format("event/%d/thumbnail", eventId);

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(filename)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INVALID_IMAGE);
        }
    }

    private void ensureBucketExists() throws Exception {
        if (!minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

}
