package LinkerBell.campus_market_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3ResponseDto {
    private String presignedUrl;
    private String s3url;
}
