package com.alloc64.imageproxy.img;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static okhttp3.CacheControl.Builder;

@Component
@Slf4j
public class FileDownloader {
    private record CacheInterceptor(int maxAge) implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request()).newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", new Builder()
                            .maxAge(maxAge, TimeUnit.SECONDS)
                            .build().toString())
                    .build();
        }
    }

    private final OkHttpClient httpClient;

    public FileDownloader(@Value("${image.proxy.useragent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36}") String userAgent,
                          @Value("${image.proxy.cacheSize:52508800}") int cacheSize,
                          @Value("${image.proxy.cacheDirectory:imgcache}") File cacheDirectory,
                          @Value("${image.proxy.cacheMaxAge:86400}") int cacheMaxAge) {
        if (!cacheDirectory.exists())
            cacheDirectory.mkdirs();

        this.httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new CacheInterceptor(cacheMaxAge))
                .addInterceptor(chain ->
                        chain.proceed(chain.request().newBuilder()
                                .header("User-Agent", userAgent)
                                .build()))
                .cache(new Cache(cacheDirectory, cacheSize))
                .build();
    }

    public InputStream downloadAsStream(String url) {
        if (!StringUtils.hasLength(url))
            return null;

        try {
            Response response = this.httpClient.newCall(new Request.Builder()
                            .url(url)
                            .build())
                    .execute();

            if (response.isSuccessful()) {
                var body = response.body();

                if (body != null)
                    return body.byteStream();
            }
        } catch (IOException e) {
            log.error("Unable to download file {}", url, e);
        }

        return null;
    }
}
