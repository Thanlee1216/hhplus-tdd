package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    @Override
    public UserPoint getUserPoint(Long id) {
        return null;
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
