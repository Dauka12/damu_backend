package com.AFM.AML.Minio.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;
    public ResponseEntity<?> uploadFile(MultipartFile file,String objectName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("originalFilename", objectName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("aml")
                        .object(objectName)
                        .userMetadata(metadata)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        return ResponseEntity.ok("File uploaded successfully");
    }

    public String getFileUrl(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidBucketNameException, InvalidExpiresRangeException {
        return minioClient.getPresignedObjectUrl(new GetPresignedObjectUrlArgs().builder().bucket("aml").object(fileName).method(Method.GET).build());
    }
}

