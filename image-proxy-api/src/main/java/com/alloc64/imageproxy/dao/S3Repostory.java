package com.alloc64.imageproxy.dao;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Optional;

public abstract class S3Repostory<T extends Serializable> {
    private final MinioClient minioClient;
    private final String bucketName;
    private final String region;

    public S3Repostory(MinioClient minioClient,
                       String bucketName,
                       String region) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.region = region;

        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .region(region)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(String key) throws Exception {
        try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .build())) {
            return Optional.ofNullable(SerializationUtils.deserialize(is));
        } catch (ErrorResponseException e) {
            return Optional.empty();
        }
    }

    public T save(String key, T entity) throws Exception {
        byte[] data = SerializationUtils.serialize(entity);
        InputStream is = new ByteArrayInputStream(data);

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .stream(is, data.length, -1)
                .contentType("application/json")
                .build());

        return entity;
    }
}
