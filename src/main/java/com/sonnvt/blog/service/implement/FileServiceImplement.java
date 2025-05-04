package com.sonnvt.blog.service.implement;

import com.sonnvt.blog.exception.ex.UploadFileException;
import com.sonnvt.blog.properties.MinioProperties;
import com.sonnvt.blog.service.FileService;
import com.sonnvt.blog.utils.Utils;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImplement implements FileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @SneakyThrows
    @Override
    public String upload(MultipartFile file, boolean isPublic) {
        String fileName = file.getOriginalFilename();
        fileName = Utils.normalizeFileName(fileName);
        if (isPublic) {
            fileName = "public/" + fileName;
        }
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(fileName)
                    .contentType(Objects.isNull(file.getContentType()) ? "image/png; image/jpg;" : file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
        } catch (Exception e) {
            throw new UploadFileException("Error while uploading file");
        }
        return !isPublic ? minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .expiry(minioProperties.getPresignedUrlExpirationTime())
                        .object(fileName)
                        .method(Method.GET)
                        .build()
        ) : String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucket(), fileName);
    }
}
