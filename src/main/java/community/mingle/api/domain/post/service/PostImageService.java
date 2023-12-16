package community.mingle.api.domain.post.service;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.domain.post.repository.PostImageRepository;
import community.mingle.api.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;

    @Transactional
    public void createPostImage(Post post, List<MultipartFile> multipartFiles) {
        String dirName = post.getBoardType().name().toLowerCase();
        List<String> imgUrls = s3Service.uploadFile(multipartFiles, dirName);
        List<PostImage> postImages = imgUrls.stream()
                .map(imgUrl -> PostImage.builder()
                        .post(post)
                        .url(imgUrl)
                        .build()
                ).collect(Collectors.toList());
        postImageRepository.saveAll(postImages);
    }

    @Transactional
    public void updatePostImage(Post post, List<String> imageUrlsToDelete, List<MultipartFile> imagesToAdd) {
        if (imageUrlsToDelete != null)
            deleteImages(post, imageUrlsToDelete);
        if (imagesToAdd != null)
            createPostImage(post, imagesToAdd);
    }

    private void deleteImages(Post post, List<String> imageUrlsToDelete) {
        List<PostImage> postImageList = post.getPostImageList();
        postImageList.stream()
                .filter(postImage -> imageUrlsToDelete.contains(postImage.getUrl()))
                .forEach(postImageRepository::delete);
    }

    @Transactional
    public void deletePostImage(Long postId) {
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        postImageRepository.deleteAll(postImages);
    }

}
