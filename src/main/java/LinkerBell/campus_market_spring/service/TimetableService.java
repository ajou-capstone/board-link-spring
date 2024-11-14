package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.User;
import LinkerBell.campus_market_spring.dto.TimetableRequestDto;
import LinkerBell.campus_market_spring.dto.TimetableResponseDto;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> timetableList = Collections.emptyList();
        try {
            if(user.getTimetable() != null) {
                timetableList = objectMapper.readValue(user.getTimetable(), List.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TimetableResponseDto.builder()
            .timetable(timetableList)
            .build();
    }

    // 시간표 데이터 수정하기
    public void patchTimetable(Long userId, TimetableRequestDto timetableRequestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Map<String, Object>> timetable = timetableRequestDto.getTimetable();
        user.setTimetableFromJson(timetable);

    }
}
