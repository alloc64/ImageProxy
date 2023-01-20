package com.alloc64.imageproxy.dao;

import com.alloc64.imageproxy.dao.model.ImageCacheEntity;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ImageCacheRepository extends S3Repostory<ImageCacheEntity> {
    public ImageCacheRepository(MinioClient minioClient,
                                @Value("${image.proxy.bucket.cache:imgproxy-cache}") String bucketName,
                                @Value("${image.proxy.bucket.region}") String region) {
        super(minioClient, bucketName, region);
    }

    public ImageCacheEntity save(ImageCacheEntity entity) throws Exception {
        return save(entity.getKey(), entity);
    }
}
