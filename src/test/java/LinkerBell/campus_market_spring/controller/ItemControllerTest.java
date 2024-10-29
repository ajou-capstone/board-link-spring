package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.global.error.GlobalExceptionHandler;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
                .andExpect(MockMvcResultMatchers.status().isOk());

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
                        .param("category", "ELECTRONICS")
                        .param("minPrice", "100")
                        .param("maxPrice", "1000")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "price,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(itemService).itemSearch(any(Long.class), Mockito.argThat(dto -> {
            Pageable pageable = dto.getPageable();
            return dto.getName().equals("exampleItem") &&
                    dto.getCategory() != null &&
                    dto.getCategory().name().equals("ELECTRONICS") &&
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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(MethodArgumentTypeMismatchException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4017));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("category에 category해당하지 않는 값이 들어 올때")
    public void invalidCategoryTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                        .param("category","sss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(MethodArgumentTypeMismatchException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4016));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("최신순으로 정렬하는 테스트")
    public void createdDateSortingTest() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                        .param("sort","createdDate,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

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
                        .param("sort","price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

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
                        .param("sort","price,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

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
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4018));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
    }

    @Test
    @DisplayName("sort 2개 넣었을 때")
    public void SortCountTwoValue() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                        .param("sort", "xxxxx","basd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> assertThat(result.getResolvedException().getClass()).isAssignableFrom(CustomException.class))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value(4018));

        verify(itemService, times(0)).itemSearch(any(Long.class), any());
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