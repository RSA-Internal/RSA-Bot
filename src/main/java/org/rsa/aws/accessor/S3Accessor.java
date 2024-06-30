package org.rsa.aws.accessor;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

@Slf4j
public class S3Accessor {

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info(bucketName + " is ready.");
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public static void deleteResourceByKey(S3Client s3Client, String bucketName, String key) {
        try {
            log.info("Attempting to delete " + key + " in bucket: " + bucketName);
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
            s3Client.deleteObject(request);
            log.info("Successfully deleted resource " + key + " in bucket: " + bucketName);
        } catch (S3Exception e) {
            log.error("Failed to delete " + key + " in bucket: " + bucketName);
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public static void deleteBucket(S3Client s3Client, String bucketName) {
        try {
            log.info("Attempting to delete bucket: " + bucketName);
            DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(request);
            log.info("Successfully deleted bucket: " + bucketName);
        } catch (S3Exception e) {
            log.error("Failed to delete bucket: " + bucketName);
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public static void createResource(S3Client s3Client, String bucketName, String key, String path) {
        try {
            log.info("Attempting to create resource: " + key + " in bucket: " + bucketName);
            if (!doesBucketExist(s3Client, bucketName)) {
                createBucket(s3Client, bucketName);
            }
            if (doesResourceExist(s3Client, bucketName, key)) {
                log.info("Resource: " + key + " already exists in bucket: " + bucketName);
                return;
            }
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            s3Client.putObject(request, RequestBody.fromFile(new File(path)));
            log.info("Successfully created resource: " + key + " in bucket: " + bucketName);
        } catch (S3Exception e) {
            log.error("Failed to create resource with key: " + key + " in bucket: " + bucketName);
            log.error(e.awsErrorDetails().errorMessage());
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
