package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Repository;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Override
    public UserPoint getUserPoint(Long id) {
        return null;
    }
}
