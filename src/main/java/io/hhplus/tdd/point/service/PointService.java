package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {

    UserPoint getUserPoint(Long id);
    List<PointHistory> getPointHistory(Long id);
    UserPoint chargePoint(Long id, Long amount);
    UserPoint usePoint(Long id, Long amount);
}
