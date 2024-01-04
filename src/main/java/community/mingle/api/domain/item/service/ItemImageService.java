package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;
}
