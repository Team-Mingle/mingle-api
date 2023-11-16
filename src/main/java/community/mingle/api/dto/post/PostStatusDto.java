package community.mingle.api.dto.post;

public record PostStatusDto(
        boolean isMyPost,
        boolean isLiked,
        boolean isScraped,
        boolean isBlinded
) {
}
