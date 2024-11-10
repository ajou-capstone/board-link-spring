package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.TimetableRequestDto;
import LinkerBell.campus_market_spring.dto.TimetableResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    // 시간표 데이터 가져오기
    @GetMapping("/api/v1/timetable/{userId}")
    public ResponseEntity<TimetableResponseDto> getTimetable(@PathVariable Long userId) {
        TimetableResponseDto timetableResponseDto = timetableService.getTimetable(userId);
        return ResponseEntity.ok(timetableResponseDto);
    }

    // 시간표 데이터 수정하기
    @PatchMapping("/api/v1/timetable")
    public ResponseEntity<Void> patchTimetable(@Login AuthUserDto authUserDto,
        @RequestBody TimetableRequestDto timetableRequestDto) {
        timetableService.patchTimetable(authUserDto.getUserId(), timetableRequestDto);
        return ResponseEntity.noContent().build();
    }
}
