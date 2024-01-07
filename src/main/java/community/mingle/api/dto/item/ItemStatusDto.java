package community.mingle.api.dto.item;

public record ItemStatusDto (
        boolean isMyPost,
        boolean isLiked
) {
}
