package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    PointServiceImpl pointService;

    @Mock
    PointRepository pointRepository;

    UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     */
    @Test
    void 유저를_조회해온다() {
        long id = 1L;
        when(pointRepository.getUserPoint(id)).thenReturn(new UserPoint(id, 0, 0));
        UserPoint userPoint = pointService.getUserPoint(id);
        assertThat(userPoint.id()).isEqualTo(id);
    }

    /**
     * 존재하지 않는 유저를 조회하는 테스트
     * 존재하지 않는 유저는 신규로 등록하고 0포인트를 충전한 이력을 남긴다.
     */
    @Test
    void 존재하지_않는_유저는_신규로_등록하고_0포인트_충전_이력을_생성한다() {
        long id = 1L;
        UserPoint userPoint = UserPoint.empty(id);
        when(pointRepository.getUserPoint(id)).thenReturn(userPoint); //생성되는 신규 유저의 생성 시간을 0으로 세팅
        when(pointRepository.insertUserPoint(userPoint.id(), userPoint.point())).thenReturn(userPointTable.insertOrUpdate(userPoint.id(), userPoint.point())); //신규 유저 등록의 결과는 유저 포인트 테이블의 등록 결과와 같음

        UserPoint resultUserPoint = pointService.getUserPoint(id);

        assertThat(resultUserPoint.id()).isEqualTo(id);
        assertThat(resultUserPoint.updateMillis()).isNotEqualTo(userPoint.updateMillis());//신규 생성된 user는 UserPoint.empty(id)의 결과인 System.currentTimeMillis()값을 가진다
    }
  
}