package com.lambda.utils;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class ImageConversionUtilTest {
    @Test
    void should_TransformImageSize_When_NewDimensionsAreApplied() throws IOException {
        InputStream imageStream = loadFileFromResources();
        InputStream transformedFileStream = ImageConversionUtil.createThumbnail(imageStream, 10, 10);
        assertThat(transformedFileStream).isNotNull();
    }

    private InputStream loadFileFromResources() throws FileNotFoundException {
        String path = "src/test/resources/com/lambda/images/rick.png";
        return new FileInputStream(new File(path));
    }
}
