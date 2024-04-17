package org.rsa.aws.accessor;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

public class S3Accessor {

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
            System.out.println(bucketName + " is ready.");
        } catch (S3Exception e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void deleteResourceByKey(S3Client s3Client, String bucketName, String key) {
        try {
            System.out.println("Attempting to delete " + key + " in bucket: " + bucketName);
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            System.out.println("Failed to delete " + key + " in bucket: " + bucketName);
            System.out.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void deleteBucket(S3Client s3Client, String bucketName) {
        try {
            System.out.println("Attempting to delete bucket: " + bucketName);
            DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(request);
        } catch (S3Exception e) {
            System.out.println("Failed to delete bucket: " + bucketName);
            System.out.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void createResource(S3Client s3Client, String bucketName, String key, String path) {
        try {
            if (!doesBucketExist(s3Client, bucketName)) {
                createBucket(s3Client, bucketName);
            }
            if (doesResourceExist(s3Client, bucketName, key)) {
                System.out.println("Resource: " + key + " already exists in bucket: " + bucketName);
                return;
            }
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            s3Client.putObject(request, RequestBody.fromFile(new File(path)));
        } catch (S3Exception e) {
            System.out.println("Failed to create resource with key: " + key + " in bucket: " + bucketName);
            System.out.println(e.awsErrorDetails().errorMessage());
        }
    }

    private static boolean doesResourceExist(S3Client s3Client, String bucketName, String key) {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
        try {
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    private static boolean doesBucketExist(S3Client s3Client, String bucketName) {
        HeadBucketRequest request = HeadBucketRequest.builder().bucket(bucketName).build();
        try {
            s3Client.headBucket(request);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
}
