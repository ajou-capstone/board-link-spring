package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.dto.AuthUserDto;
import LinkerBell.campus_market_spring.dto.S3ResponseDto;
import LinkerBell.campus_market_spring.global.auth.Login;
import LinkerBell.campus_market_spring.service.S3Service;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/api/v1/s3/presigned-url")
    public ResponseEntity<S3ResponseDto> getPresignedPutUrl(@Login AuthUserDto user,
        @NotBlank @RequestParam("fileName") String fileName) {
        S3ResponseDto s3ResponseDto = s3Service.createPreSignedPutUrl(fileName);
        return ResponseEntity.ok(s3ResponseDto);
    }
}
