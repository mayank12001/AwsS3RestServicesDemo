package com.s3demo.security;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class Authenticator {

    public static AmazonS3 authenticate() {

        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2)
                .withForceGlobalBucketAccessEnabled(true).withCredentials(
                new AWSStaticCredentialsProvider(credentials)).build();

        return s3;
    }
}
