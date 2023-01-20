package com.alloc64.imageproxy.dao;

import com.alloc64.imageproxy.utils.HashUtils;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ImageUrlToKeyRepository extends S3Repostory<String> {
    public ImageUrlToKeyRepository(MinioClient minioClient,
                                   @Value("${image.proxy.bucket.cache:imgproxy-url2key}") String bucketName,
                                   @Value("${image.proxy.bucket.region}") String region) {
        super(minioClient, bucketName, region);
    }

    @Override
    public Optional<String> findById(String key) throws Exception {
        return super.findById(HashUtils.sha256Base62(key));
    }

    @Override
    public String save(String key, String entity) throws Exception {
        return super.save(HashUtils.sha256Base62(key), entity);
    }
}
