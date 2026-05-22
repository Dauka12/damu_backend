package com.AFM.AML.Minio.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket:aml}")
    private String bucket;

    @Value("${minio.public-url}")
    private String publicUrl;

    public ResponseEntity<?> uploadFile(MultipartFile file,String objectName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("originalFilename", objectName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .userMetadata(metadata)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return ResponseEntity.ok("File uploaded successfully");
    }

    public String getFileUrl(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException, InvalidExpiresRangeException {
        return buildPublicUrl(fileName);
    }

    public String buildPublicUrl(String fileName) {
        String objectName = extractObjectName(fileName);
        if (objectName == null || objectName.isBlank()) {
            return "";
        }
        return publicUrl.replaceAll("/+$", "") + "/" + objectName.replaceAll("^/+", "");
    }

    public String extractObjectName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }

        String value = fileName.trim();
        int mediaIndex = value.indexOf("/api/media/");
        if (mediaIndex >= 0) {
            return value.substring(mediaIndex + "/api/media/".length());
        }

        int bucketIndex = value.indexOf("/" + bucket + "/");
        if (bucketIndex >= 0) {
            return value.substring(bucketIndex + bucket.length() + 2);
        }

        return value.replaceAll("^/+", "");
    }

    public InputStream getObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(extractObjectName(objectName))
                        .build()
        );
    }

    public ObjectStat statObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(extractObjectName(objectName))
                        .build()
        );
    }
}

