package com.api.readinglog.common.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.AwsS3Exception;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile profileImage) {
        String fileName = generateFileName(profileImage.getOriginalFilename());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(profileImage.getSize());
            metadata.setContentType(profileImage.getContentType());

            uploadToS3(bucket, fileName, profileImage, metadata);
            return getFileUrl(fileName);

        } catch (IOException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_UPLOAD_FAIL);
        }
    }

    private String generateFileName(String originalFileName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("members/%s/%s", currentDate, originalFileName);
    }

    private void uploadToS3(String bucket, String fileName, MultipartFile file, ObjectMetadata metadata)
            throws IOException {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
    }

    private String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }

}
