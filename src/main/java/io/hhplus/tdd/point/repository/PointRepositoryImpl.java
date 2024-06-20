package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Override
    public UserPoint getUserPoint(Long id) {
        return null;
    }

    @Override
    public List<PointHistory> getPointHistory(long userId) {
        return List.of();
    }

    @Override
    public PointHistory insertPointHistory(long userId, long amount, TransactionType type, long updateMillis) {
        return null;
    }

    @Override
    public UserPoint insertUserPoint(long id, long amount) {
        return null;
    }
}
