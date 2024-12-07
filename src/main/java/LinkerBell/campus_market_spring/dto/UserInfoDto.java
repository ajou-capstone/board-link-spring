package LinkerBell.campus_market_spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long id;
    private String nickname;
    private String profileImage;
    private Double rating;
    @JsonProperty(value = "isDeleted") private Boolean isDeleted;
    private String suspendedReason;
    private String campusName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime suspendedDate;

    public UserInfoDto(Long id, String nickname, String profileImage, Double rating,
        Boolean isDeleted, String suspendedReason, String universityName, String region, LocalDateTime suspendedDate) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.rating = rating;
        this.isDeleted = isDeleted;
        this.suspendedReason = suspendedReason;
        this.campusName = universityName.concat(" ").concat(region);
        this.suspendedDate = suspendedDate;
    }
}
