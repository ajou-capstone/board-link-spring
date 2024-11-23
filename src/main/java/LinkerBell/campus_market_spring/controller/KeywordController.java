package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.CollectionResponse.KeywordCollectionResponseDto;
import LinkerBell.campus_market_spring.dto.KeywordRegisterRequestDto;
import LinkerBell.campus_market_spring.dto.KeywordRegisterResponseDto;
import LinkerBell.campus_market_spring.dto.KeywordResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.global.error.ErrorCode;
import LinkerBell.campus_market_spring.global.error.exception.CustomException;
import LinkerBell.campus_market_spring.service.KeywordService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping
    public ResponseEntity<KeywordRegisterResponseDto> keywordRegister(
        @Login AuthUserDto user,
        @Valid @RequestBody KeywordRegisterRequestDto keywordRegisterRequestDto
    ) {
        KeywordRegisterResponseDto responseDto = keywordService.addKeyword(user.getUserId(),
            keywordRegisterRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<KeywordCollectionResponseDto> getKeywords(
        @Login AuthUserDto user
    ) {
        List<KeywordResponseDto> keywordList = keywordService.getKeywords(user.getUserId());
        return ResponseEntity.ok(KeywordCollectionResponseDto.from(keywordList));
    }

    @DeleteMapping("/{keywordId}")
    public ResponseEntity<?> deleteKeyword(
        @Login AuthUserDto user,
        @PathVariable Long keywordId
    ) {
        validKeywordId(keywordId);
        keywordService.deleteKeyword(user.getUserId(), keywordId);
        return ResponseEntity.noContent().build();
    }

    private void validKeywordId(Long keywordId) {

        if (keywordId == null) {
            throw new CustomException(ErrorCode.INVALID_KEYWORD_ID);
        }
        if (keywordId < 1) {
            throw new CustomException(ErrorCode.INVALID_KEYWORD_ID);
        }
    }
}
