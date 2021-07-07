package com.deali.adtech.infrastructure.util.support;

import com.deali.adtech.infrastructure.exception.ImageUploadFailureException;
import com.deali.adtech.infrastructure.exception.InvalidImageTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class LocalFileUploadSupport implements FileUploadSupport{
    @Override
    public void uploadImage(byte[] fileBytes, String filePath) {
        if(fileBytes == null || fileBytes.length == 0) {
            throw new ImageUploadFailureException();
        }

        try(FileOutputStream outputStream =
                    new FileOutputStream(filePath)){

            outputStream.write(fileBytes);

        } catch (IOException exception) {
            throw new ImageUploadFailureException();
        }
    }

    @Override
    public void uploadMultipartFileImage(MultipartFile multipartFile, String filePath) {
        if(!multipartFileValidation(multipartFile)) {
            throw new InvalidImageTypeException();
        }

        try {

            byte[] fileBytes = multipartFile.getBytes();
             uploadImage(fileBytes, filePath);

        } catch (IOException exception) {

            throw new ImageUploadFailureException();

        }
    }

    @Override
    public void exchangeImage(byte[] fileBytes, String oldFilePath, String newFilePath) {
        if(!removeImage(oldFilePath)) {
            throw new ImageUploadFailureException();
        }

        uploadImage(fileBytes, newFilePath);
    }

    @Override
    public void exchangeMultipartFileImage(MultipartFile multipartFile, String oldFilePath, String newFilePath) {
        if(!multipartFileValidation(multipartFile)) {
            throw new InvalidImageTypeException();
        }

        try {
            byte[] fileBytes = multipartFile.getBytes();
            exchangeImage(fileBytes, oldFilePath, newFilePath);
        } catch (IOException exception) {
            throw new ImageUploadFailureException();
        }
    }

    @Override
    public boolean removeImage(String filePath) {
        File file = new File(filePath);

        if(!file.exists()) {
            throw new ImageUploadFailureException();
        }

        return file.delete();
    }

    @Override
    public boolean multipartFileValidation(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            return false;
        }

        String contentType = multipartFile.getContentType();

        if(contentType != null) {
            String type = contentType.split("/")[0];
            String extension = contentType.split("/")[1];

            for(AllowedExtension extensionType : AllowedExtension.values()) {
                if(extensionType.getExtension().equals(extension)) return true;
            }
        }

        String originalFileName = multipartFile.getOriginalFilename();
        int lastDotIndex = originalFileName.lastIndexOf(".");

        String extension = originalFileName.substring(lastDotIndex+1);

        for(AllowedExtension extensionType : AllowedExtension.values()) {
            if(extensionType.getExtension().equals(extension)) return true;
        }

        return false;
    }
}
