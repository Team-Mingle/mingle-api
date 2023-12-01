package community.mingle.api.global.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static community.mingle.api.global.exception.ErrorCode.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 amazonS3;
    private final SecretsManagerService secretsManagerService;

    private static final String CLOUDFRONT_DOMAIN = "https://d2xbxo9g2f57e.cloudfront.net/";

    private static final List<String> VALID_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG", ".heic", ".HEIC", ".webp");

    public List<String> uploadFile(List<MultipartFile> multipartFile, String dirName) {
        String bucketName = secretsManagerService.getS3BucketName();
        List<String> fileNameList = new ArrayList<>();

        for (MultipartFile file : multipartFile) {
            String fileName = buildFilePath(dirName, file.getOriginalFilename());
            ObjectMetadata objectMetadata = createObjectMetadata(file);
            try (InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                fileNameList.add(CLOUDFRONT_DOMAIN + fileName);
            } catch (Exception e) {
                deleteFile(fileName, dirName);
                throw new CustomException(UPLOAD_FAIL_IMAGE);
            }
        }
        return fileNameList;
    }

    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }

    private String buildFilePath(String dirName, String originalFilename) {
        validateFileExtension(originalFilename);
        return dirName + "/" + createFileName(originalFilename);
    }

    private void validateFileExtension(String fileName) {
        if (fileName.isEmpty() || !VALID_EXTENSIONS.contains(getFileExtension(fileName))) {
            throw new CustomException(INVALID_IMAGE_FORMAT);
        }
    }
    private String createFileName(String fileName) { //  파일명을 난수화
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public void deleteFile(String fileName, String dirName) {
        String BUCKET_NAME = secretsManagerService.getS3BucketName();
        String fileRename = dirName + "/" + fileName;
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, fileRename));
        } catch (AmazonClientException e) {
            throw new CustomException(DELETE_FAIL_IMAGE);
        }
    }

}
