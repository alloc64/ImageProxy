package com.alloc64.imageproxy.controller;

import com.alloc64.imageproxy.dao.model.BaseMediaEntity;
import com.alloc64.imageproxy.img.ImageObserver;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Duration;

@RestController
public class DynamicImageController {
    private static final String FROM_URL_PATH = "/img/from-url/";
    private final ImageObserver imageObserver;
    private final CacheControl publicCacheControl;

    public DynamicImageController(ImageObserver imageObserver) {
        this.imageObserver = imageObserver;
        this.publicCacheControl = CacheControl.maxAge(Duration.ofDays(1))
                .noTransform()
                .cachePublic();
    }

    @GetMapping("/img/{key}")
    public ResponseEntity<byte[]> imageByHash(@PathVariable("key") String key,
                                              @RequestParam(value = "w", required = false) Integer targetWidth) throws Exception {
        return asBytes(imageObserver.resizedImageByKey(key, targetWidth).orElseThrow());
    }

    @GetMapping(FROM_URL_PATH + "**")
    public ResponseEntity<byte[]> imageFromUrl(HttpServletRequest request,
                                               @RequestParam(value = "w", required = false) Integer targetWidth) throws Exception {
        String url = URI.create(request.getRequestURI()).getPath().substring(FROM_URL_PATH.length());
        return asBytes(imageObserver.resizedByUrl(url, targetWidth).orElseThrow());
    }

    private ResponseEntity<byte[]> asBytes(BaseMediaEntity image) {
        return ResponseEntity.ok()
                .contentType(new MediaType("image", image.getFormat()))
                .header("Pragma", "public")
                .cacheControl(publicCacheControl)
                .body(image.getBytes());
    }
}
