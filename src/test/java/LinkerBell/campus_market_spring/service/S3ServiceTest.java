package LinkerBell.campus_market_spring.service;

import static org.assertj.core.api.Assertions.*;

import LinkerBell.campus_market_spring.dto.S3ResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class S3ServiceTest {

    @Autowired
    S3Service s3Service;

//    @Test
    @DisplayName("presigned put url 발급 테스트")
    public void getPresignedPutUrlTest() {
        // given
        String fileName = "test-file";
        // when
        S3ResponseDto responseDto = s3Service.createPreSignedPutUrl(fileName);
        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getPresignedUrl()).contains(fileName);
        assertThat(responseDto.getS3url()).contains(fileName);
    }

//    @Test
    @DisplayName("파일 삭제 테스트")
    public void deleteFileTest() {
        // given
        String s3Url = "https://test-url";
        // when
        s3Service.deleteS3File(s3Url);

        // then
    }
}