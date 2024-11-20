package LinkerBell.campus_market_spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.TimetableRequestDto;
import LinkerBell.campus_market_spring.dto.TimetableResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TimetableServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TimetableService timetableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("time table을 get 요청 하는데 성공하는 test")
    void testGetTimetable_Success() throws Exception {
        // Given
        Long userId = 1L;

        List<Map<String, Object>> expectedTimetable = List.of(
            Map.of(
                "dayOfWeek", 1,
                "startTime", "09:00",
                "endTime", "10:30"
            )
        );

        User user = User.builder()
            .userId(userId)
            .build();
        user.setTimetableFromJson(expectedTimetable);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        TimetableResponseDto responseDto = timetableService.getTimetable(userId);

        // Then
        assertNotNull(responseDto);
        assertEquals(expectedTimetable, responseDto.getTimetable());
        verify(userRepository, times(1)).findById(userId);
    }


    @Test
    @DisplayName("timeTable get 요청하는데 user가 없는 경우 테스트")
    void testGetTimetable_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> timetableService.getTimetable(userId));

        // Then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("time table을 patch 요청 하는데 성공하는 test")
    void testPatchTimetable_Success() throws JsonProcessingException {
        // Given
        Long userId = 1L;
        List<Map<String, Object>> updatedTimetable = List.of(
            Map.of(
                "dayOfWeek", 3,
                "startTime", "10:00",
                "endTime", "10:30"
            )
        );

        User user = User.builder()
            .userId(userId)
            .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        TimetableRequestDto timetableRequestDto = new TimetableRequestDto();
        timetableRequestDto.setTimetable(updatedTimetable);

        // When
        timetableService.patchTimetable(userId, timetableRequestDto);

        // Then
        assertEquals(new ObjectMapper().writeValueAsString(updatedTimetable), user.getTimetable());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("timeTable patch 요청하는데 user가 없는 경우 테스트")
    void testPatchTimetable_UserNotFound() {
        // Given
        Long userId = 1L;
        List<Map<String, Object>> updatedTimetable = List.of(
            Map.of(
                "dayOfWeek", 3,
                "startTime", "10:00",
                "endTime", "10:30"
            )
        );
        TimetableRequestDto timetableRequestDto = new TimetableRequestDto();
        timetableRequestDto.setTimetable(updatedTimetable);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> timetableService.patchTimetable(userId, timetableRequestDto));

        // Then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findById(userId);
    }
}

