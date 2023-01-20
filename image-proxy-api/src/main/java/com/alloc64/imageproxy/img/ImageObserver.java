package com.alloc64.imageproxy.img;

import com.alloc64.imageproxy.dao.ImageCacheRepository;
import com.alloc64.imageproxy.dao.ImageRepository;
import com.alloc64.imageproxy.dao.ImageUrlToKeyRepository;
import com.alloc64.imageproxy.dao.model.BaseMediaEntity;
import com.alloc64.imageproxy.dao.model.ImageCacheEntity;
import com.alloc64.imageproxy.dao.model.ImageEntity;
import com.alloc64.imageproxy.img.exceptions.ImageProxyException;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.nio.JpegWriter;
import com.sksamuel.scrimage.nio.PngWriter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
public class ImageObserver {
    private final FileDownloader fileDownloader;
    private final ImageRepository imageRepository;
    private final ImageCacheRepository imageCacheRepository;
    private final ImageUrlToKeyRepository imageUrlToKeyRepository;
    private final int maxImageSize;

    @Autowired
    public ImageObserver(FileDownloader fileDownloader,
                         ImageRepository imageRepository,
                         ImageCacheRepository imageCacheRepository,
                         ImageUrlToKeyRepository imageUrlToKeyRepository,
                         @Value("${image.proxy.maximagesize:4096}") int maxImageSize) {
        this.fileDownloader = fileDownloader;
        this.imageRepository = imageRepository;
        this.imageCacheRepository = imageCacheRepository;
        this.imageUrlToKeyRepository = imageUrlToKeyRepository;
        this.maxImageSize = maxImageSize;
    }

    public ImageEntity create(String url) throws ImageProxyException {
        try (InputStream is = fileDownloader.downloadAsStream(url)) {
            ImmutableImage image = ImmutableImage
                    .loader()
                    .fromStream(is);

            int width = image.width;
            int height = image.height;

            if (width > maxImageSize)
                width = maxImageSize;
            else if (height > maxImageSize)
                height = maxImageSize;

            ImmutableImage cappedImage = image.max(width, height);

            ImageBytes bytes = asBytes(cappedImage);
            ImageEntity result = imageRepository.save(ImageEntity.builder()
                    .width(cappedImage.width)
                    .height(cappedImage.height)
                    .bytes(bytes.getBytes())
                    .format(bytes.getFormat())
                    .build()
                    .createKey());

            imageUrlToKeyRepository.save(url, result.getKey());
            return result;
        } catch (Exception e) {
            throw new ImageProxyException(HttpStatus.BAD_REQUEST, "Unable to create image.", e);
        }
    }

    public Optional<? extends BaseMediaEntity> resizedByUrl(String url, Integer targetWidth)
            throws ImageProxyException {
        try {
            Optional<String> existingKey = imageUrlToKeyRepository.findById(url);

            if (existingKey.isPresent())
                return resizedImageByKey(existingKey.orElseThrow(), targetWidth);

            return Optional.ofNullable(create(url));
        } catch (NoSuchElementException e) {
            throw new ImageProxyException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ImageProxyException(HttpStatus.BAD_REQUEST, "Unable to resize image from URL.", e);
        }
    }

    public Optional<? extends BaseMediaEntity> resizedImageByKey(String key, Integer targetWidth)
            throws ImageProxyException {
        try {
            if (targetWidth == null)
                return imageRepository.findById(key);

            String cachedImageKey = key + "-w=" + targetWidth;
            ImageCacheEntity e = imageCacheRepository.findById(cachedImageKey).orElse(null);

            if (e == null)
                e = resize(imageRepository.findById(key).orElseThrow(), cachedImageKey, targetWidth);

            return Optional.ofNullable(e);
        } catch (NoSuchElementException e) {
            throw new ImageProxyException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ImageProxyException(HttpStatus.BAD_REQUEST, "Unable to determine image by key.", e);
        }
    }

    private ImageCacheEntity resize(ImageEntity originalImage, String key, Integer targetWidth) throws Exception {
        ImageBytes result = asBytes(ImmutableImage
                .loader()
                .fromBytes(originalImage.getBytes())
                .max(targetWidth, originalImage.getHeight()));

        return imageCacheRepository.save(ImageCacheEntity.builder()
                .bytes(result.getBytes())
                .format(result.getFormat())
                .key(key)
                .build());
    }

    private ImageBytes asBytes(ImmutableImage image) throws Exception {
        if (image == null)
            return null;

        ImageWriter writer;
        String format;
        if (image.hasTransparency()) {
            writer = new PngWriter();
            format = "png";
        } else {
            writer = new JpegWriter();
            format = "jpg";
        }

        return ImageBytes.builder()
                .bytes(image.bytes(writer))
                .format(format)
                .build();
    }

    @Data
    @Builder
    private static class ImageBytes {
        private byte[] bytes;
        private String format;
    }
}
