package com.api.farmingsoon.common.s3.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.S3Exception;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Getter
    @Value("${cloud.aws.default-image}")
    private String defaultProfileImg;

    public String upload(MultipartFile profileImg, String kind) {

        // 파일 확장자 분리 (.png, .jpg, .gif)
        String ext = Optional.ofNullable(profileImg.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.split("\\.")[1])
                .orElse("");

        // 파일 이름은 UUID.ext 형식으로 업로드
        String fileName = "%s.%s".formatted(UUID.randomUUID(), ext);
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String s3location = bucket + "/" + kind + "/" + formatDate; // 버킷 내 폴더

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(profileImg.getSize());
        metadata.setContentType(profileImg.getContentType());

        try {
            log.debug("uploading aws s3.. upload path: {}", s3location + fileName);
            amazonS3.putObject(s3location, fileName, profileImg.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            throw new S3Exception(ErrorCode.FAIL_UPLOAD_S3);
        } catch(SdkClientException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(amazonS3.getUrl(s3location, fileName));
    }

    // TODO: 회원 정보 수정 구현시 필요
    public void delete(String prevProfileImgUrl) {
        String key = prevProfileImgUrl.split(bucket + "/")[1];
        amazonS3.deleteObject(bucket, key);
    }

}
