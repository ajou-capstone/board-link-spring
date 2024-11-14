package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserRepository;
import LinkerBell.campus_market_spring.dto.TimetableRequestDto;
import LinkerBell.campus_market_spring.dto.TimetableResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testGetTimetable_Success() {
        // Given
        Long userId = 1L;
        String sampleTimetable = "{\"monday\": \"Math\"}";
        User user = User.builder()
            .userId(userId)
            .timetable(sampleTimetable)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        TimetableResponseDto responseDto = timetableService.getTimetable(userId);

        // Then
        assertNotNull(responseDto);
        assertEquals(sampleTimetable, responseDto.getTimetable());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
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
    void testGetTimetable_TimetableNotFound() {
        // Given
        Long userId = 1L;
        User user = User.builder()
            .userId(userId)
            .timetable(null)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> timetableService.getTimetable(userId));

        // Then
        assertEquals(ErrorCode.TIMETABLE_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testPatchTimetable_Success() {
        // Given
        Long userId = 1L;
        String updatedTimetable = "{\"monday\": \"Science\"}";
        User user = User.builder()
            .userId(userId)
            .timetable("{\"monday\": \"Math\"}")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        TimetableRequestDto timetableRequestDto = new TimetableRequestDto();
        timetableRequestDto.setTimetable(updatedTimetable);

        // When
        timetableService.patchTimetable(userId, timetableRequestDto);

        // Then
        assertEquals(updatedTimetable, user.getTimetable());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testPatchTimetable_UserNotFound() {
        // Given
        Long userId = 1L;
        TimetableRequestDto timetableRequestDto = new TimetableRequestDto();
        timetableRequestDto.setTimetable("{\"monday\": \"Science\"}");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> timetableService.patchTimetable(userId, timetableRequestDto));

        // Then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findById(userId);
    }
}

