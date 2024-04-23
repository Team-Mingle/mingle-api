package community.mingle.api.domain.point.service;

import community.mingle.api.domain.point.entity.Point;
import community.mingle.api.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Long getPointAmountByMember(Long memberId) {
        Point point = getByMemberId(memberId);
        return point.getAmount();
    }

    public Point getByMemberId(Long memberId) {
        return pointRepository.findByMemberId(memberId);
    }
}
