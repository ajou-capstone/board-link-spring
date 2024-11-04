package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.TermsCollectionRequestDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.TermsCollectionResponseDto;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.TermsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/terms")
public class TermsController {

    private final TermsService termsService;

    @GetMapping
    public ResponseEntity<TermsCollectionResponseDto> getTermsAndUserInfo(@Login AuthUserDto user) {
        List<TermsResponseDto> termsResponseDtoList =
            termsService.getTermsAndUserAgreementInfo(user.getUserId());
        return ResponseEntity.ok(TermsCollectionResponseDto.from(termsResponseDtoList));
    }

    @PostMapping("/agreement")
    public ResponseEntity<?> agreeTerms(@Login AuthUserDto user, @RequestBody
        TermsCollectionRequestDto termsCollectionRequestDto) {
        termsService.agreeTerms(user.getUserId(), termsCollectionRequestDto.getTerms());
        return ResponseEntity.noContent().build();
    }
}
