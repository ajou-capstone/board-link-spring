package LinkerBell.campus_market_spring.controller;

import LinkerBell.campus_market_spring.domain.QaCategory;
import LinkerBell.campus_market_spring.dto.QaCategoryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class QaController {

    @GetMapping("/questions")
    public ResponseEntity<?> getQaCategory() {
        return ResponseEntity.ok(new QaCategoryResponseDto(QaCategory.values()));
    }
}
