package com.alloc64.imageproxy.dao;

import com.alloc64.imageproxy.dao.model.ImageEntity;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepository extends S3Repostory<ImageEntity> {
    public ImageRepository(MinioClient minioClient,
                           @Value("${image.proxy.bucket.cache:imgproxy-original}") String bucketName,
                           @Value("${image.proxy.bucket.region}") String region) {
        super(minioClient, bucketName, region);
    }

    public ImageEntity save(ImageEntity entity) throws Exception {
        return save(entity.getKey(), entity);
    }
}



