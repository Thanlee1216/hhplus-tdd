package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointRepository {
    /**
     * 유저 조회
     * @param id
     * @return
     */
    UserPoint getUserPoint(Long id);
    List<PointHistory> getPointHistory(long userId);
    PointHistory insertPointHistory(long userId, long amount, TransactionType type, long updateMillis);
    UserPoint insertUserPoint(long id, long amount);
}
