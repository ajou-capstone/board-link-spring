package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.TimetableRequestDto;
import LinkerBell.campus_market_spring.dto.TimetableResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableService {

    private final UserRepository userRepository;

    // 시간표 데이터 가져오기
    @Transactional(readOnly = true)
    public TimetableResponseDto getTimetable(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String timetable = user.getTimetable();

        if (timetable == null) {
            throw new CustomException(ErrorCode.TIMETABLE_NOT_FOUND);
        }

        TimetableResponseDto timetableResponseDto = TimetableResponseDto.builder()
            .timetable(timetable)
            .build();

        return timetableResponseDto;
    }

    // 시간표 데이터 수정하기
    public void patchTimetable(Long userId, TimetableRequestDto timetableRequestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String timetable = timetableRequestDto.getTimetable();

        user.setTimetable(timetable);
    }
}
