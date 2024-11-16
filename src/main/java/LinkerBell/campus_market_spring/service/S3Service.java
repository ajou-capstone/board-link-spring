package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.dto.S3ResponseDto;
import LinkerBell.campus_market_spring.global.config.AwsProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public S3ResponseDto createPreSignedPutUrl(String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String encodedTime = getEncodedTime(now).substring(0, 6);

        fileName = fileName.trim()
            .replaceAll("\\s", "-")
            .replaceAll("_", "-");

        String keyName = String.format("%s/%s%s%s",
            awsProperties.folder(), encodedTime, date, fileName);

        log.info("keyName: [{}]", keyName);
        
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .key(keyName)
            .bucket(awsProperties.bucket()).build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10L))
            .putObjectRequest(objectRequest).build();

        PresignedPutObjectRequest presignedPutObjectRequest =
            s3Presigner.presignPutObject(presignRequest);

        String presignedUrl = presignedPutObjectRequest.url().toExternalForm();
        String s3url = getS3Url(keyName);

        return S3ResponseDto.builder()
            .presignedUrl(presignedUrl)
            .s3url(s3url).build();
    }

    public void deleteS3File(String s3Url) {
        String bucket = awsProperties.bucket();
        String keyName = getKeyName(s3Url);
        DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(keyName)
            .build();

        s3Client.deleteObject(objectRequest);
    }

    private String getKeyName(String s3Url) {
        String scheme_domain = String.format("https://%s.s3.%s.amazonaws.com/",
            awsProperties.bucket(), awsProperties.region());

        return s3Url.substring(scheme_domain.length());
    }

    private String getS3Url(String keyName) {
        String bucket = awsProperties.bucket();
        String region = awsProperties.region();
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            bucket, region, keyName);
    }

    private String getEncodedTime(LocalDateTime now) {
        try {
            String timeString = now.toString();
            byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(timeString.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot use MessageDigest");
            String timeString = now.toString();
            return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(timeString.getBytes(StandardCharsets.UTF_8));
        }
    }
}
