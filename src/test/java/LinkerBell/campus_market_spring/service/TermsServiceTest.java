package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import LinkerBell.campus_market_spring.domain.Terms;
import LinkerBell.campus_market_spring.dto.TermsResponseDto;
import LinkerBell.campus_market_spring.repository.TermsRepository;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @Mock
    TermsRepository termsRepository;

    @InjectMocks
    TermsService termsService;

    @Test
    public void getTermsAndUserAgreementInfoTest() {
        // given
        Terms terms1 = Terms.builder().termsId(1L).termsUrl("test").isRequired(true).title("test").build();
        Terms terms2 = Terms.builder().termsId(2L).termsUrl("test2").isRequired(false).title("test2").build();
        given(termsRepository.findAll()).willReturn(Lists.newArrayList(terms1, terms2));
        // when
        List<TermsResponseDto> termsResponseDtoList = termsService.getTermsAndUserAgreementInfo(1L);

        // then
        assertThat(termsResponseDtoList.size()).isEqualTo(2);
    }
}