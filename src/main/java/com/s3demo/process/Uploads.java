package com.s3demo.process;

import java.io.File;

public class Uploads {

    private File file;
    private static final String FIELD_BUCKET_NAME = "bucketName";
    private static final String FIELD_FOLDER_NAME = "folderName";
    private String bucketName;
    private String folderName;

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setItem(String fieldName, String value) {
        if (fieldName.equals(FIELD_BUCKET_NAME)) {
            this.bucketName = value;
        } else if (fieldName.equals(FIELD_FOLDER_NAME)) {
            this.folderName = value;
        } else {

        }
    }
}
