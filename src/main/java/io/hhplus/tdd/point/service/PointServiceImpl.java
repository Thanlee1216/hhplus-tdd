package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointRepository;
import io.hhplus.tdd.point.util.LockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    LockUtil lockUtil;

    PointRepository repository;

    public PointServiceImpl(PointRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserPoint getUserPoint(Long id) {
        lockUtil.lock(id);
        try {
            UserPoint userPoint = repository.getUserPoint(id);
            //포인트 이력이 많아지게 되면 소요시간이 어마무시하게 늘어나겠지만 실제 DB를 쓴다면 count 집계 함수의 결과로 변경해준다는 가정으로 만든 조건문
            if (repository.getPointHistory(userPoint.id()).size() == 0) {
                userPoint = repository.insertUserPoint(userPoint.id(), userPoint.point());
                //신규 고객은 0포인트 충전 이력을 등록 TODO - 충전에 실패하면 롤백해야하나 UserPointTable에는 현재 삭제 메소드가 없기에 생략
                repository.insertPointHistory(userPoint.id(), userPoint.point(), TransactionType.CHARGE, System.currentTimeMillis());
            }
            return userPoint;
        }finally {
            lockUtil.unlock();
        }
    }

    @Override
    public List<PointHistory> getPointHistory(Long id) {
        lockUtil.lock(id);
        try {
            List<PointHistory>historyList = repository.getPointHistory(id);
            if(historyList.isEmpty()) {
                throw new NullPointerException();
            }
            return historyList;
        }finally {
            lockUtil.unlock();
        }
    }

    @Override
    public UserPoint chargePoint(Long id, Long amount) {
        lockUtil.lock(id);
        try {
            if(amount < 0) {
                throw new IllegalArgumentException();
            }
            //포인트 이력이 많아지게 되면 소요시간이 어마무시하게 늘어나겠지만 실제 DB를 쓴다면 count 집계 함수의 결과로 변경해준다는 가정으로 만든 조건문
            if (repository.getPointHistory(id).size() == 0) {
                throw new NullPointerException();
            }
            UserPoint userPoint = repository.getUserPoint(id);
            PointHistory pointHistory = repository.insertPointHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return repository.insertUserPoint(userPoint.id(), userPoint.point() + pointHistory.amount());
        }finally {
            lockUtil.unlock();
        }
    }

    @Override
    public UserPoint usePoint(Long id, Long amount) {
        lockUtil.lock(id);
        try {
            if (amount < 0) {
                throw new IllegalArgumentException();
            }
            //포인트 이력이 많아지게 되면 소요시간이 어마무시하게 늘어나겠지만 실제 DB를 쓴다면 count 집계 함수의 결과로 변경해준다는 가정으로 만든 조건문
            if (repository.getPointHistory(id).size() == 0) {
                throw new NullPointerException();
            }
            UserPoint userPoint = repository.getUserPoint(id);
            if (userPoint.point() - amount < 0) {
                throw new ArithmeticException();
            }
            PointHistory pointHistory = repository.insertPointHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
            return repository.insertUserPoint(userPoint.id(), userPoint.point() - pointHistory.amount());
        }finally {
            lockUtil.unlock();
        }
    }
}
