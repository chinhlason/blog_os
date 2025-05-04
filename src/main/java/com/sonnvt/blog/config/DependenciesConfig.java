package com.sonnvt.blog.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DependenciesConfig {
    @Value("${minio.bucket}")
    private String BUCKET;

    private final MinioClient minioClient;

    @PostConstruct
    public void init() {
        log.info("Initializing MinIO bucket: {} {}", BUCKET, minioClient);
        if (minioClient == null) {
            throw new IllegalStateException("MinioClient is not initialized!");
        }
        createBucket(BUCKET);
    }

    @SneakyThrows
    private void createBucket(String bucketName) {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(BUCKET)
                .build());

        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(BUCKET)
                            .build());

            String policy = """
                    {
                       "Version": "2012-10-17",
                       "Statement": [
                          {
                             "Effect": "Allow",
                             "Principal": "*",
                             "Action": "s3:GetObject",
                             "Resource": "arn:aws:s3:::%s/public/*"
                          }
                       ]
                    }
                    """.formatted(bucketName);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(BUCKET)
                    .config(policy)
                    .build());
        } else {
            log.warn("Bucket {} already exists", BUCKET);
        }
    }
}
