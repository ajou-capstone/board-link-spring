package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Item;
import LinkerBell.campus_market_spring.domain.ItemPhotos;
import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.*;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.ItemPhotosRepository;
import LinkerBell.campus_market_spring.repository.ItemRepository;
import LinkerBell.campus_market_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemPhotosRepository itemPhotosRepository;

    @Transactional(readOnly = true)
    public SliceResponse<ItemSearchResponseDto> itemSearch(Long userId, ItemSearchRequestDto itemSearchRequestDto) {
        User user = getUserWithCampus(userId);

        return itemRepository.itemSearch(user.getCampus().getCampusId(), itemSearchRequestDto.getName(),
                itemSearchRequestDto.getCategory(), itemSearchRequestDto.getMinPrice(),
                itemSearchRequestDto.getMaxPrice(), itemSearchRequestDto.getPageable());
    }

    public ItemRegisterResponseDto itemRegister(Long userId, ItemRegisterRequestDto itemRegisterRequestDto) {
        User user = getUserWithCampus(userId);

        Item savedItem = itemRegisterDtoToItem(itemRegisterRequestDto, user);
        itemRepository.save(savedItem);

        if (itemRegisterRequestDto.getImages() != null) {
            List<ItemPhotos> itemPhotos = imagesToItemPhotos(itemRegisterRequestDto, savedItem);
            itemPhotosRepository.saveAll(itemPhotos);
        }

        return new ItemRegisterResponseDto(savedItem.getItemId());

    }

    private User getUserWithCampus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getCampus() == null) {
            throw new CustomException(ErrorCode.CAMPUS_NOT_FOUND);
        }
        return user;
    }

    private List<ItemPhotos> imagesToItemPhotos(ItemRegisterRequestDto itemRegisterRequestDto, Item savedItem) {
        return itemRegisterRequestDto
                .getImages()
                .stream()
                .map(photo -> {
                    ItemPhotos itemPhoto = new ItemPhotos();
                    itemPhoto.registerItemPhotos(savedItem, photo);
                    return itemPhoto;
                }).toList();
    }

    private Item itemRegisterDtoToItem(ItemRegisterRequestDto itemRegisterRequestDto, User user) {
        return Item.builder()
                .user(user)
                .campus(user.getCampus())
                .title(itemRegisterRequestDto.getTitle())
                .category(itemRegisterRequestDto.getCategory())
                .description(itemRegisterRequestDto.getDescription())
                .price(itemRegisterRequestDto.getPrice())
                .thumbnail(itemRegisterRequestDto.getThumbnail())
                .build();
    }
}
