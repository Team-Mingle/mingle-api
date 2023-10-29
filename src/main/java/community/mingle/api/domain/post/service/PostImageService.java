package community.mingle.api.domain.post.service;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.domain.post.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final PostImageRepository postImageRepository;

    @Transactional
    public List<PostImage> createPostImage(Post post, List<MultipartFile> images) {
        List<PostImage> postImages = new ArrayList<>();

        //TODO image url 설정하기
        for (MultipartFile image : images) {
            PostImage postImage = PostImage.builder()
                                    .post(post)
                                    .url("images")
                                    .build();
            postImages.add(postImage);
        }

        return postImageRepository.saveAll(postImages);
    }

    @Transactional
    public void updatePostImage(Post post, List<Long> imageIdsToDelete, List<MultipartFile> imagesToAdd) {
        if (imageIdsToDelete != null) {
            postImageRepository.deleteAllById(imageIdsToDelete);
        }

        if (imagesToAdd != null) {
            createPostImage(post, imagesToAdd);
        }
    }

    @Transactional
    public void deletePostImage(Long postId) {
        List<PostImage> postImages = postImageRepository.findAllByPostId(postId);
        postImageRepository.deleteAll(postImages);
    }

}
