package com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.lambda.utils.ImageConversionUtil;
import java.io.InputStream;

import static java.lang.String.format;
import static java.time.Instant.now;

public class ImageConverter implements RequestHandler<S3Event, Boolean> {

    private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
    private LambdaLogger logger;

    public Boolean handleRequest(S3Event s3Event, Context context) {
        try {
            logger = context.getLogger();

            String bucket = extractS3BucketName(s3Event);
            String path = extractFilePath(s3Event);

            logger.log(format("Downloading from S3 bucket: %s, path: %s", bucket, path));
            S3Object response = s3Client.getObject(new GetObjectRequest(bucket, path));

            String contentType = response.getObjectMetadata().getContentType();
            logger.log(format("Content type: %s", contentType));
            if (!"image/jpeg".equals(contentType)) {
                logger.log("Wrong content type!. We are only accepting jep.");
                return false;
            }

            logger.log("Creating a thumbnail.");
            InputStream fileInputStream = response.getObjectContent();
            InputStream modifiedFileInputStream = ImageConversionUtil.createThumbnail(fileInputStream, 30, 30);

            String modifiedFilePath = prepareModifiedFilePath(path);
            logger.log(format("Uploading file to S3 bucket: %s, path: %s", bucket, modifiedFilePath));
            s3Client.putObject(new PutObjectRequest(bucket, modifiedFilePath, modifiedFileInputStream, new ObjectMetadata()));
            return true;
        } catch (Exception e) {
            logger.log(format("Exception while converting image, e = %s", e.getMessage()));
            return false;
        }
    }

    private String prepareModifiedFilePath(String path) {
        String timeStamp = String.valueOf(now().toEpochMilli());
        return format("thumbnails/thumb_%s.jpg", timeStamp);
    }

    private String extractS3BucketName(S3Event s3Event) {
        logger.log("Extracting s3 bucket name");
        return s3Event.getRecords().get(0).getS3().getBucket().getName();
    }

    private String extractFilePath(S3Event s3Event) {
        logger.log("Extracting file path");
        return s3Event.getRecords().get(0).getS3().getObject().getKey();
    }
}
