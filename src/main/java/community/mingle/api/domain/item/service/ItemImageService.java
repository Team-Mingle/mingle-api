package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemImage;
import community.mingle.api.domain.item.repository.ItemImageRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostImage;
import community.mingle.api.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemImageService {

    private static final String DIR_NAME = "item";

    private final ItemImageRepository itemImageRepository;
    private final S3Service s3Service;

    @Transactional
    public void createItemImage(Item item, List<MultipartFile> multipartFiles) {
        List<String> imgUrls = s3Service.uploadFile(multipartFiles, DIR_NAME);
        List<ItemImage> itemImages = imgUrls.stream()
                .map(imgUrl -> ItemImage.builder()
                        .item(item)
                        .url(imgUrl)
                        .build()
                ).toList();
        itemImageRepository.saveAll(itemImages);
    }
}
