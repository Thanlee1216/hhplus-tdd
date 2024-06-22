package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Autowired
    PointHistoryTable pointHistoryTable;

    @Autowired
    UserPointTable userPointTable;

    @Override
    public UserPoint getUserPoint(Long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public PointHistory insertPointHistory(long userId, long amount, TransactionType type, long updateMillis) {
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }

    @Override
    public UserPoint insertUserPoint(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
