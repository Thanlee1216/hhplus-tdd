package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {

    /**
     * 유저 조회
     * @param id
     * @return
     */
    UserPoint getUserPoint(Long id);

    /**
     * 포인트 내역 조회
     * @param id
     * @return
     */
    List<PointHistory> getPointHistory(Long id);

    /**
     * 포인트 충전
     * @param id
     * @param amount
     * @return
     */
    UserPoint chargePoint(Long id, Long amount);

    /**
     * 포인트 사용
     * @param id
     * @param amount
     * @return
     */
    UserPoint usePoint(Long id, Long amount);
}
