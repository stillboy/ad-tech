package com.deali.adtech.infrastructure.util.support;

import lombok.Getter;

@Getter
public enum AllowedExtension {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif")
    ;

    private final String extension;

     AllowedExtension(String extension) {
        this.extension = extension;
    }
}
