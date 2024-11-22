package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.QaCategoryResponseDto;
import LinkerBell.campus_market_spring.dto.QaRequestDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.QaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        qaService.postQuestion(user.getUserId(), requestDto.title(), requestDto.description(),
            requestDto.category());
        return ResponseEntity.noContent().build();
    }
}
