package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;

public interface PointRepository {
    /**
     * 유저 조회
     * @param id
     * @return
     */
    UserPoint getUserPoint(Long id);
}
