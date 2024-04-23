package community.mingle.api.domain.point.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.point.controller.RemainingPointResponse;
import community.mingle.api.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final TokenService tokenService;
    private final PointService pointService;

    public RemainingPointResponse getRemainingPointAmount() {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Long remainingPointAmount = pointService.getPointAmountByMember(memberId);
        return new RemainingPointResponse(remainingPointAmount);
    }
}
