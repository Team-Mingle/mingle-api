package community.mingle.api.domain.item.facade;

import community.mingle.api.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemFacade {

    private final ItemService itemService;
}
