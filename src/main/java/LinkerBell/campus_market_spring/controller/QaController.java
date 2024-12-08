package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.QaCategoryResponseDto;
import LinkerBell.campus_market_spring.dto.QaRequestDto;
import LinkerBell.campus_market_spring.dto.QaResponseDto;
import LinkerBell.campus_market_spring.dto.QaSearchResponseDto;
import LinkerBell.campus_market_spring.dto.SliceResponse;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.QaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QaController {

    private final QaService qaService;

    @GetMapping("/questions")
    public ResponseEntity<QaCategoryResponseDto> getQaCategory() {
        return ResponseEntity.ok(new QaCategoryResponseDto(QaCategory.values()));
    }

    @PostMapping("/questions")
    public ResponseEntity<?> postQuestion(@Login AuthUserDto user,
        @Valid @RequestBody QaRequestDto requestDto) {
        qaService.postQuestion(user.getUserId(), requestDto.getTitle(), requestDto.getDescription(),
            requestDto.getCategory());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/answers")
    public ResponseEntity<SliceResponse<QaSearchResponseDto>> getAnswers(@Login AuthUserDto user,
        @PageableDefault(page = 0, size = 10)
        @SortDefaults({
            @SortDefault(sort = "createdDate", direction = Direction.DESC),
            @SortDefault(sort = "qaId", direction = Direction.DESC)}) Pageable pageable) {
        SliceResponse<QaSearchResponseDto> response =
            qaService.getAnswers(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/answers/{qaId}")
    public ResponseEntity<QaResponseDto> getAnswerDetails(@Login AuthUserDto user,
        @PathVariable("qaId") Long qaId) {
        QaResponseDto response = qaService.getAnswerDetails(user.getUserId(), qaId);
        return ResponseEntity.ok(response);
    }
}
