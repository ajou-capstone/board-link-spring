package LinkerBell.campus_market_spring.service;

import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.repository.TermsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {

    private final TermsRepository termsRepository;

    public List<TermsResponseDto> getTermsAndUserAgreementInfo(Long userId) {
        List<Terms> terms = termsRepository.findAll();
        return createTermsResponseDto(terms);
    }

    private List<TermsResponseDto> createTermsResponseDto(List<Terms> terms) {
        return terms.stream()
            .map(term -> {
                return TermsResponseDto.builder().id(term.getTermsId())
                    .url(term.getTermsUrl()).title(term.getTitle())
                    .isRequired(term.isRequired())
                    .isAgree(true).build();
            }).toList();
    }
}
