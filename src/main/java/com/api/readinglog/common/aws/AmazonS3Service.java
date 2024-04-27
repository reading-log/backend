package com.api.readinglog.common.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.AwsS3Exception;
import com.api.readinglog.common.image.ImageUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Getter;
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

    @Getter
    @Value("${cloud.aws.s3.default.profile.image}")
    private String defaultProfileImg;

    public String uploadFile(MultipartFile file, DomainType type) {
      
        String ext = ImageUtil.getExt(file.getOriginalFilename()); // 확장자
        String fileName = UUID.randomUUID() + "." + ext; // 파일 이름 + 확장자
        String imageFilePath = generateFilePath(fileName, type); // 타입/날짜/파일이름

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            uploadToS3(bucket, imageFilePath, file, metadata);
            return getFileUrl(imageFilePath);

        } catch (IOException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_UPLOAD_FAIL);
        }
    }

    private void uploadToS3(String bucket, String filePath, MultipartFile file, ObjectMetadata metadata)
            throws IOException {
        s3Client.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), metadata));
    }

    public void deleteFile(String filePath) {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
            log.debug("삭제한 이미지 파일의 경로: {}", filePath);
        } catch (AmazonServiceException e) {
            throw new AwsS3Exception(ErrorCode.AWS_S3_FILE_DELETE_FAIL);
        }
    }

    // [type]/[yyyy_MM_dd]/[파일 이름] 형식의 경로 반환 메서드
    private String generateFilePath(String fileName, DomainType type) {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        return String.format("%s/%s/%s", type.getType(), currentDate, fileName);
    }

    // 전체 이미지 주소 반환 메서드
    public String getFileUrl(String fileName) {
        return s3Client.getUrl(bucket, fileName).toString();
    }

}
