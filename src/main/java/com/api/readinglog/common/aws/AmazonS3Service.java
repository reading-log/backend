package com.api.readinglog.common.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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

    public String uploadFile(MultipartFile profileImg) {
        String fileName = generateFileName(profileImg.getOriginalFilename());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(profileImg.getSize());
            metadata.setContentType(profileImg.getContentType());

            uploadToS3(bucket, fileName, profileImg, metadata);
            return fileName;

        } catch (IOException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_UPLOAD_FAIL);
        }
    }

    // TODO: 회원, 책 이미지 업로드 시 공통으로 사용할 수 있게 리팩토링 필요함.
    public String uploadBookCover(MultipartFile profileImg) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = String.format("books/%s/%s", currentDate, profileImg.getOriginalFilename());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(profileImg.getSize());
            metadata.setContentType(profileImg.getContentType());

            uploadToS3(bucket, fileName, profileImg, metadata);
            return fileName;

        } catch (IOException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_UPLOAD_FAIL);
        }
    }

    public void deleteFile(String fileName) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.debug("삭제한 이미지 파일 이름: {}", fileName);
        } catch (AmazonServiceException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_DELETE_FAIL);
        }
    }

    public String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }

    private String generateFileName(String originalFileName) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("members/%s/%s", currentDate, originalFileName);
    }

    private void uploadToS3(String bucket, String fileName, MultipartFile file, ObjectMetadata metadata)
            throws IOException {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
    }

}
