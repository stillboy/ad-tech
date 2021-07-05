package com.deali.adtech.infrastructure.util.support;

import com.deali.adtech.infrastructure.exception.ImageUploadFailureException;
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
}
