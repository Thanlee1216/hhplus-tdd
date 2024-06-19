package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    PointRepository repository;

    public PointServiceImpl(PointRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserPoint getUserPoint(Long id) {
        UserPoint userPoint = repository.getUserPoint(id);
        long time = System.currentTimeMillis() - userPoint.updateMillis();
        if(time < 500) { //0.5초 안에 수정되었다면 신규 계정으로 판단..? TODO - 의문이 드는 방법이기 때문에 대안을 생각해야함
            userPoint = repository.insertUserPoint(userPoint.id(), userPoint.point());
            //신규 고객은 0포인트 충전 이력을 등록 TODO - 충전에 실패하면 롤백해야하나 UserPointTable에는 현재 삭제 메소드가 없기에 생략
            repository.insertPointHistory(userPoint.id(), userPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());
        }
        return userPoint;
    }

    @Override
    public List<PointHistory> getPointHistory(Long id) {
        return List.of();
    }

    @Override
    public UserPoint chargePoint(Long id, Long amount) {
        return null;
    }

    @Override
    public UserPoint usePoint(Long id, Long amount) {
        return null;
    }
}
