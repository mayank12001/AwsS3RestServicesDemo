package com.s3demo.routes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.s3demo.process.ProcessUploads;
import com.s3demo.process.Uploads;
import com.s3demo.security.Authenticator;
import com.s3demo.utils.JsonUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

public class S3Routes {

    public static void main(String[] args) throws IOException {

        port(8080);

        get("/health", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                String healthy = "I am healthy";
                return JsonUtils.toJson(healthy);
            }
        });

        post("/root", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    AmazonS3 s3 = Authenticator.authenticate();
                    String bucketName = request.queryParams("name");
                    bucketName += "-" + UUID.randomUUID();
                    Bucket bucket = s3.createBucket(bucketName);
                    return JsonUtils.toJson(bucket.getName());
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });

        get("/root/:prefix", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    String prefix = request.params(":prefix");
                    AmazonS3 s3 = Authenticator.authenticate();
                    List<Bucket> buckets = s3.listBuckets();
                    if (prefix.equals("all")) {
                        return JsonUtils.toJson(buckets);
                    }
                    Iterator<Bucket> iterator = buckets.iterator();
                    while (iterator.hasNext()) {
                        Bucket next = iterator.next();
                        if (!next.getName().startsWith(prefix)) {
                            iterator.remove();
                        }
                    }
                    return JsonUtils.toJson(buckets);
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });

        post("/folder", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    AmazonS3 s3 = Authenticator.authenticate();
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(0);
                    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
                    String folderName = request.queryParams("folderName") + "/";
                    String bucketName = request.queryParams("bucketName");
                    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName,
                            emptyContent, metadata);
                    s3.putObject(putObjectRequest);
                    return "success";
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });

        post("/file", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    Uploads uploads = ProcessUploads.uploadFile(request.raw());
                    String bucketName = uploads.getBucketName();
                    String key = uploads.getFolderName() + "/" + uploads.getFile().getName();

                    AmazonS3 s3 = Authenticator.authenticate();
                    s3.putObject(new PutObjectRequest(bucketName, key, uploads.getFile()));

                    return "success";
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });

        get("/file/:bucket/:folderName/:fileName", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    String bucketName = request.params(":bucket");
                    String folderName = request.params(":folderName");
                    String fileName = request.params("fileName");
                    String key = folderName + "/" + fileName;
                    AmazonS3 s3 = Authenticator.authenticate();
                    S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, key));
                    return JsonUtils.toJson(s3Object.getObjectContent());
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });

        delete("/file/:bucket/:folderName/:fileName", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                try {
                    String bucket = request.params(":bucket");
                    String folderName = request.params(":folderName");
                    String fileName = request.params(":fileName");
                    String key = folderName + "/" + fileName;
                    AmazonS3 s3 = Authenticator.authenticate();
                    s3.deleteObject(bucket, key);
                    return "success";
                } catch (Exception e) {
                    return JsonUtils.toJson(e.getMessage());
                }
            }
        });
    }
}
