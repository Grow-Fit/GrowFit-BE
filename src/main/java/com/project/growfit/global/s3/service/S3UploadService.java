package com.project.growfit.global.s3.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile, String filePath) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = extractExtension(originalFilename);
            String uniqueFileName = filePath + UUID.randomUUID() + extension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            metadata.addUserMetadata("Content-Disposition", "attachment; filename=\"" + uniqueFileName + "\"");

            amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return predictResizedImageUrl(amazonS3.getUrl(bucket, uniqueFileName).toString());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            // S3 URL에서 파일명 추출
            String objectKey = extractObjectKeyFromUrl(fileUrl);

            // S3에서 파일 삭제
            amazonS3.deleteObject(bucket, objectKey);
        } catch (SdkClientException | URISyntaxException e) {
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public String predictResizedImageUrl(String originalImageUrl) {
        String s3BaseUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/";

        // s3 키 추출 ex) post/abc123.png
        String originalKey = originalImageUrl.substring(originalImageUrl.indexOf(s3BaseUrl) + s3BaseUrl.length());

        // 확장자 제거 ex) post/abc123
        int extensionIndex = originalKey.lastIndexOf(".");
        String withoutExtension = (extensionIndex != -1) ? originalKey.substring(0, extensionIndex) : originalKey;

        // 리사이징된 경로 구성 ex) resized-post/abc123.webp
        String resizedKey = "resized-" + withoutExtension + ".webp";

        return s3BaseUrl + resizedKey;
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String extractObjectKeyFromUrl(String fileUrl) throws URISyntaxException {
        URI uri = new URI(fileUrl);
        return uri.getPath().substring(1);
    }
}