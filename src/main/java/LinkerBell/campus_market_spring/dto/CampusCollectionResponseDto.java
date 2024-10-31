package LinkerBell.campus_market_spring.dto;

public record CampusCollectionResponseDto<T> (T campuses) {
    public static <T> CampusCollectionResponseDto<T> from(T campuses) {
        return new CampusCollectionResponseDto<>(campuses);
    }
}
