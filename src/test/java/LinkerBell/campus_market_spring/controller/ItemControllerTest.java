package LinkerBell.campus_market_spring.controller;

import static LinkerBell.campus_market_spring.global.error.ErrorCode.DUPLICATE_ITEM_PHOTOS;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_CATEGORY;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_DESCRIPTION;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_ITEM_ID;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_ITEM_PHOTOS;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_ITEM_PHOTOS_COUNT;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_PRICE;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_SORT;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_THUMBNAIL;
import static LinkerBell.campus_market_spring.global.error.ErrorCode.INVALID_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import LinkerBell.campus_market_spring.domain.Category;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.ItemRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.ItemRegisterResponseDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    private MockLoginArgumentResolver mockLoginArgumentResolver = new MockLoginArgumentResolver();

    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setControllerAdvice(GlobalExceptionHandler.class)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                mockLoginArgumentResolver)
            .build();
    }

    @Test
    @DisplayName("아무 값도 안들어 왔을 때")
    public void defaultValueTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName() == null &&
                dto.getCategory() == null &&
                dto.getMinPrice() == null &&
                dto.getMaxPrice() == null &&
                pageable.getPageNumber() == 0 &&
                pageable.getPageSize() == 10 &&
                pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "createdDate"));
        }));
    }

    @Test
    @DisplayName("모든값이 잘 들어 왔을 때")
    public void correctValueTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("name", "exampleItem")
                .param("category", "ELECTRONICS_IT")
                .param("minPrice", "100")
                .param("maxPrice", "1000")
                .param("page", "1")
                .param("size", "5")
                .param("sort", "price,asc")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName().equals("exampleItem") &&
                dto.getCategory() != null &&
                dto.getCategory().name().equals("ELECTRONICS_IT") &&
                dto.getMinPrice() == 100 &&
                dto.getMaxPrice() == 1000 &&
                pageable.getPageNumber() == 1 &&
                pageable.getPageSize() == 5 &&
                pageable.getSort().equals(Sort.by(Sort.Direction.ASC, "price"));
        }));
    }

    @Test
    @DisplayName("price에 String 들어 올때")
    public void InvalidPriceTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", "sss")
                .param("maxPrice", "kkk")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentTypeMismatchException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("minPrice에 음수 들어 올때")
    public void InvalidMinusMinPriceTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", "-1")
                .param("maxPrice", "100")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("maxPrice에 음수 들어 올때")
    public void InvalidMinusMaxPriceTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", "0")
                .param("maxPrice", "-1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("minPrice에 Integer.max + 1 들어올때")
    public void InvalidOverflowMinPriceTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", Integer.MAX_VALUE + 1 + "")
                .param("maxPrice", "100")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("maxPrice에 Integer.max + 1 들어올때")
    public void InvalidOverflowMaxPriceTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", "0")
                .param("maxPrice", Integer.MAX_VALUE + 1 + "")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("minValue보다 maxValue가 더 클 때")
    public void MinValueBiggerMaxValue() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("minPrice", "1")
                .param("maxPrice", "0")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("category에 category해당하지 않는 값이 들어 올때")
    public void invalidCategoryTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("category", "sss")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentTypeMismatchException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_CATEGORY.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("최신순으로 정렬하는 테스트")
    public void createdDateSortingTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("sort", "createdDate,desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName() == null &&
                dto.getCategory() == null &&
                dto.getMinPrice() == null &&
                dto.getMaxPrice() == null &&
                pageable.getPageNumber() == 0 &&
                pageable.getPageSize() == 10 &&
                pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "createdDate"));
        }));
    }

    @Test
    @DisplayName("가격 오름차순으로 정렬하는 테스트")
    public void PriceAscSortingTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("sort", "price")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName() == null &&
                dto.getCategory() == null &&
                dto.getMinPrice() == null &&
                dto.getMaxPrice() == null &&
                pageable.getPageNumber() == 0 &&
                pageable.getPageSize() == 10 &&
                pageable.getSort().equals(Sort.by(Sort.Direction.ASC, "price"));
        }));
    }

    @Test
    @DisplayName("가격 내림차순으로 정렬하는 테스트")
    public void PriceDescSortingTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("sort", "price,desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName() == null &&
                dto.getCategory() == null &&
                dto.getMinPrice() == null &&
                dto.getMaxPrice() == null &&
                pageable.getPageNumber() == 0 &&
                pageable.getPageSize() == 10 &&
                pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "price"));
        }));
    }

    @Test
    @DisplayName("이상한 sort 값 넣었을 때")
    public void InvalidSortValue() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("sort", "xxxxx")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_SORT.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("sort 2개 넣었을 때")
    public void SortCountTwoValue() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                .param("sort", "xxxxx", "basd")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_SORT.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("정상적인 아이템 등록 테스트")
    public void defaultItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "20",
              "description": "testDescription",
              "category": "ELECTRONICS_IT",
              "thumbnail": "https://www.default.com"
            }
            """;

        ItemRegisterResponseDto responseDto = new ItemRegisterResponseDto(1L);
        when(itemService.itemRegister(any(Long.class),
            any(ItemRegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(status().isOk());

        ArgumentCaptor<ItemRegisterRequestDto> captor = ArgumentCaptor.forClass(
            ItemRegisterRequestDto.class);
        verify(itemService).itemRegister(any(Long.class), captor.capture());

        ItemRegisterRequestDto capturedDto = captor.getValue();
        assertThat(capturedDto.getTitle()).isEqualTo("testTitle");
        assertThat(capturedDto.getPrice()).isEqualTo(20);
        assertThat(capturedDto.getDescription()).isEqualTo("testDescription");
        assertThat(capturedDto.getCategory()).isEqualTo(Category.ELECTRONICS_IT);
        assertThat(capturedDto.getThumbnail()).isEqualTo("https://www.default.com");
        assertThat(capturedDto.getImages()).isNull();
    }

    @Test
    @DisplayName("title이 null일때 등록 테스트")
    public void titleNullItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "",
              "price": "20",
              "description": "testDescription",
              "category": "ELECTRONICS_IT",
              "thumbnail": "https://www.default.com"
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_TITLE.getCode()));

    }

    @Test
    @DisplayName("price가 null일때 등록 테스트")
    public void priceNullItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": null,
              "description": "testDescription",
              "category": "ELECTRONICS_IT",
              "thumbnail": "https://www.default.com"
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_PRICE.getCode()));

    }

    @Test
    @DisplayName("description가 null일때 등록 테스트")
    public void descriptionNullItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": null,
              "category": "ELECTRONICS_IT",
              "thumbnail": "https://www.default.com"
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_DESCRIPTION.getCode()));

    }

    @Test
    @DisplayName("적절하지 않은 category 일때 등록 테스트")
    public void InvalidCategoryItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": "testDescription",
              "category": "SSS",
              "thumbnail": "https://www.default.com"
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    HttpMessageNotReadableException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_CATEGORY.getCode()));
    }

    @Test
    @DisplayName("적절하지 않은 썸네일 일때 등록 테스트")
    public void InvalidThumbnailItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": "testDescription",
              "category": null,
              "thumbnail": "https://"
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_THUMBNAIL.getCode()));
    }

    @Test
    @DisplayName("적절하지 않은 이미지들 등록 테스트")
    public void InvalidImagesItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": "testDescription",
              "category": null,
              "thumbnail": "https://default.com",
              "images" : ["ddd","https://images.com"]
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentNotValidException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_ITEM_PHOTOS.getCode()));
    }

    @Test
    @DisplayName("이미지의 개수가 5개 초과일 때 테스트")
    public void InvalidImagesSizeOverFiveItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": "testDescription",
              "category": null,
              "thumbnail": "https://default.com",
              "images" : ["https://images1.com","https://images2.com",
              "https://images3.com","https://images4.com",
              "https://images5.com","https://images6.com"]
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_ITEM_PHOTOS_COUNT.getCode()));
    }

    @Test
    @DisplayName("중복된 이미지 링크 검증 테스트")
    public void DuplicatedImagesItemRegisterTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "10",
              "description": "testDescription",
              "category": null,
              "thumbnail": "https://default.com",
              "images" : ["https://images1.com","https://images1.com",
              "https://images3.com","https://images4.com"]
            }
            """;

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(DUPLICATE_ITEM_PHOTOS.getCode()));
    }

    @Test
    @DisplayName("카테고리의 리스트가져오는 테스트")
    public void getItemCategoryTest() throws Exception {
        mockMvc.perform(get("/api/v1/items/categories")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

    }

    @Test
    @DisplayName("itemId에 null이 들어올 때")
    public void NullItemIdTest() throws Exception {
        Long itemId = null;
        mockMvc.perform(get("/api/v1/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    MethodArgumentTypeMismatchException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_ITEM_ID.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("itemId에 음수값 들어올 때")
    public void MInusItemIdTest() throws Exception {
        long itemId = -1L;
        mockMvc.perform(get("/api/v1/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(
                result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(
                    CustomException.class))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.code").value(INVALID_ITEM_ID.getCode()));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("정상적인 아이템 업데이트 테스트")
    public void defaultItemUpdateTest() throws Exception {
        String itemRequestJson = """
            {
              "title": "testTitle",
              "price": "20",
              "description": "testDescription",
              "category": "ELECTRONICS_IT",
              "thumbnail": "https://www.default.com"
            }
            """;

        doNothing().when(itemService)
            .updateItem(any(Long.class), any(Long.class), any(ItemRegisterRequestDto.class));

        long itemId = 1L;
        mockMvc.perform(patch("/api/v1/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemRequestJson))
            .andDo(print())
            .andExpect(status().isNoContent());

        ArgumentCaptor<ItemRegisterRequestDto> captor = ArgumentCaptor.forClass(
            ItemRegisterRequestDto.class);
        verify(itemService).updateItem(any(Long.class), eq(itemId), captor.capture());

        ItemRegisterRequestDto capturedDto = captor.getValue();
        assertThat(capturedDto.getTitle()).isEqualTo("testTitle");
        assertThat(capturedDto.getPrice()).isEqualTo(20);
        assertThat(capturedDto.getDescription()).isEqualTo("testDescription");
        assertThat(capturedDto.getCategory()).isEqualTo(Category.ELECTRONICS_IT);
        assertThat(capturedDto.getThumbnail()).isEqualTo("https://www.default.com");
        assertThat(capturedDto.getImages()).isNull();
    }

    @Test
    @DisplayName("정상적인 아이템 삭제 테스트")
    public void defaultItemDeleteTest() throws Exception {

        doNothing().when(itemService).deleteItem(any(Long.class), any(Long.class));

        long itemId = 1L;
        mockMvc.perform(delete("/api/v1/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(itemService).deleteItem(any(Long.class), eq(itemId));

    }


    static class MockLoginArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return true;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return AuthUserDto.builder().userId(1L).build();
        }

    }

}