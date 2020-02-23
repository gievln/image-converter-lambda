package com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.lambda.utils.ImageConversionUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

public class ImageConverter implements RequestHandler<S3Event, Boolean> {

    private static final String TARGET_FILE_LOCATION = "src/main/resources/targetFile.png";
    private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
    private LambdaLogger logger;

    public Boolean handleRequest(S3Event s3Event, Context context) {
        logger = context.getLogger();

        String bucket = extractS3BucketName(s3Event);
        String path = extractFilePath(s3Event);

        logger.log(format("Downloading from S3 bucket: %s, path: %s", bucket, path));
        S3Object response = s3Client.getObject(new GetObjectRequest(bucket, path));

        String contentType = response.getObjectMetadata().getContentType();
        logger.log(format("Content type: %s", contentType));

        if (!"image/png".equals(contentType)) {
            return false;
        }
        File targetFile = prepareTargetFile(response);
        File modifiedFile = ImageConversionUtil.createThumbnail(targetFile, 30, 30);

        String modifiedFilePath = prepareModifiedFilePath(path);
        logger.log(format("Uploading file to S3 bucket: %s, path: %s", bucket, modifiedFilePath));
        s3Client.putObject(new PutObjectRequest(bucket, modifiedFilePath, modifiedFile));
        return true;
    }

    private String prepareModifiedFilePath(String path) {
        //TODO get path properly
        return "new/thumb.png";
    }

    private File prepareTargetFile(S3Object response) {
        try{
            logger.log(format("Saving target file into: %s", TARGET_FILE_LOCATION));
            InputStream inputStream = response.getObjectContent();
            FileUtils.copyInputStreamToFile(inputStream, new File(TARGET_FILE_LOCATION));
            return new File(TARGET_FILE_LOCATION);
        }catch (IOException e){
            throw new RuntimeException("Exception while preparing target file, e = " + e.getMessage());
        }
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
