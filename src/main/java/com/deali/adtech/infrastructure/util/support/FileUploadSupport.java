package com.deali.adtech.infrastructure.util.support;

import com.deali.adtech.domain.AdvertisementImage;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadSupport {
    void uploadImage(byte[] fileBytes, String filePath);
    void uploadMultipartFileImage(MultipartFile multipartFile, String filePath);
    void exchangeImage(byte[] fileBytes, String oldFilePath, String newFilePath);
    void exchangeMultipartFileImage(MultipartFile multipartFile, String oldFilePath, String newFilePath);
    boolean removeImage(String filePath);
}
