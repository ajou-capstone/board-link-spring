package LinkerBell.campus_market_spring.dto;


public record CollectionResponseDto<T> (T terms) {
    public static <T> CollectionResponseDto<T> from(T terms) {
        return new CollectionResponseDto<>(terms);
    }
}
