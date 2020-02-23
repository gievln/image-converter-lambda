package com.lambda.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageConversionUtilTest {
    @Test
    void should_TransformImageSize_When_NewDimensionsAreApplied(){
        File image = loadFileFromResources();
        File transformedFile = ImageConversionUtil.createThumbnail(image, 10, 10);
        assertThat(transformedFile).isNotNull();
    }

    private File loadFileFromResources() {
        String path = "src/test/resources/com/lambda/images/rick.png";
        return new File(path);
    }
}
