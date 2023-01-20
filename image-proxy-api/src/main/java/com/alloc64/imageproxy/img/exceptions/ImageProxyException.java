package com.alloc64.imageproxy.img.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ImageProxyException extends ResponseStatusException {

    public ImageProxyException(HttpStatus status) {
        super(status);
    }

    public ImageProxyException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ImageProxyException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public ImageProxyException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
