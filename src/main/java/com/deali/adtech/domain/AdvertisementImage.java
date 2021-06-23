package com.deali.adtech.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name="ADVERTISEMENT_IMAGE")
public class AdvertisementImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="NAME", nullable = false)
    private String name;

    @Column(name="EXTENSION", nullable = false)
    private String extension;

    @Column(name="SIZE", nullable = false)
    private Long size;

    @Column(name="PATH", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADVERTISEMENT_ID", nullable = false)
    private Advertisement advertisement;

    @Builder
    public AdvertisementImage(String name, Long size, String path) {
        splitNameAndExtension(name);
        this.size = size;
        this.path = path;
    }

    public void bindAdvertisement(Advertisement advertisement) {
        if(this.advertisement != null) return;
        this.advertisement = advertisement;
    }

    public void editNameAndExtension(String fileName) {
        if(fileName == null || fileName.trim().length() == 0) return;
        splitNameAndExtension(fileName);
    }

    public void changeSize(Long size) {
        if(size == null || size <= 0) return;
        this.size = size;
    }

    public String getFullPathName() {
        return path + name + "." + extension;
    }

    private void splitNameAndExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');

        this.name = UUID.randomUUID().toString();
        this.extension = fileName.substring(lastDot+1, fileName.length());
    }

    public void uploadImageFile(byte[] fileBytes) {
        if(path == null || fileBytes.length <= 0) return;

        try(FileOutputStream fileOutputStream =
                    new FileOutputStream(path+name+"."+extension)) {
            fileOutputStream.write(fileBytes);
        } catch(IOException exception) {
            throw new RuntimeException();
        }
    }

    public void exchangeImage(String fileName, Long newSize, byte[] newFileBytes) {
        //TODO::익셉션 정의 필요, 파일 삭제 실패 예외
        if(!removeImageFile()) {
            throw new RuntimeException();
        }

        editNameAndExtension(fileName);
        changeSize(newSize);

        uploadImageFile(newFileBytes);
    }

    private boolean removeImageFile() {
        File file = new File(getFullPathName());

        //TODO::익셉션 정의 필요, 파일이 존재하지 않을 시
        if(!file.exists()) {
            throw new RuntimeException();
        }

        return file.delete();
    }
}
