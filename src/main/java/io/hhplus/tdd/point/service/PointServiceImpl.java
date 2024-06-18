package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
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
        return repository.getUserPoint(id);
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
