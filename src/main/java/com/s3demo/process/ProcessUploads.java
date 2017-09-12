package com.s3demo.process;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Iterator;
import java.util.List;

public class ProcessUploads {

    private static final String TEMP_PATH = "src/main/resources";
    private static final File UPLOAD;

    static {
        UPLOAD = new File(TEMP_PATH);
        if (!UPLOAD.exists() && !UPLOAD.mkdirs()) {
            throw new RuntimeException("Failed to create directory " + UPLOAD.getAbsolutePath());
        }
    }

    public static Uploads uploadFile(HttpServletRequest request) throws IOException, FileUploadException {

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(UPLOAD);
        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        List<FileItem> items = fileUpload.parseRequest(request);

        Iterator<FileItem> iterator = items.iterator();
        File target = null;

        Uploads uploads = new Uploads();
        while (iterator.hasNext()) {
            FileItem item = iterator.next();
            if (item.isFormField()) {
                uploads.setItem(item.getFieldName(), item.getString());
            } else {
                InputStream inputStream = item.getInputStream();
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                target = new File(TEMP_PATH + item.getName());
                OutputStream outputStream = new FileOutputStream(target);
                outputStream.write(buffer);
            }
        }
        if (target != null) {
            uploads.setFile(target);
            return uploads;
        } else {
            throw new FileUploadException("Failed to upload the file.");
        }
    }
}
