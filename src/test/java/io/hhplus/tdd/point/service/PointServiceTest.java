package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    PointServiceImpl pointService;

    @Mock
    PointRepository pointRepository;

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
    }

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     */
    @Test
    void 유저를_조회해온다() {
        //given
        long id = 1L;
        when(pointRepository.getUserPoint(id)).thenReturn(new UserPoint(id, 0, 0));

        //when
        UserPoint userPoint = pointService.getUserPoint(id);

        //then
        assertThat(userPoint.id()).isEqualTo(id);
    }

    /**
     * 존재하지 않는 유저를 조회하는 테스트
     * 존재하지 않는 유저는 신규로 등록하고 0포인트를 충전한 이력을 남긴다.
     */
    @Test
    void 존재하지_않는_유저는_신규로_등록하고_0포인트_충전_이력을_생성한다() {
        //given
        long id = 1L;
        UserPoint userPoint = UserPoint.empty(id);
        when(pointRepository.getUserPoint(id)).thenReturn(userPoint); //생성되는 신규 유저의 생성 시간을 0으로 세팅
        when(pointRepository.insertUserPoint(userPoint.id(), userPoint.point())).thenReturn(userPointTable.insertOrUpdate(userPoint.id(), userPoint.point())); //신규 유저 등록의 결과는 유저 포인트 테이블의 등록 결과와 같음

        //when
        UserPoint resultUserPoint = pointService.getUserPoint(id);

        //then
        assertThat(resultUserPoint.id()).isEqualTo(id);
        assertThat(resultUserPoint.updateMillis()).isNotEqualTo(userPoint.updateMillis());//신규 생성된 user는 UserPoint.empty(id)의 결과인 System.currentTimeMillis()값을 가진다
    }

    @Test
    void 존재하는_유저의_포인트_내역을_조회한다() {
        //given
        long id = 1L;
        //포인트 충전, 사용의 내역이 1건도 없는 신규 사용자는 0포인트 충전 이력을 가지고 있다.
        when(pointRepository.getPointHistory(id)).thenReturn(List.of(new PointHistory(1, id, 0, TransactionType.CHARGE, 0)));

        //when
        List<PointHistory> historyList = pointService.getPointHistory(id);

        //then
        assertThat(historyList.get(0).userId()).isEqualTo(id);
    }

    @Test
    void 존재하지_않는_유저의_포인트_내역을_조회하면_NP가_발생한다() {
        //given
        long id = 1L;
        when(pointRepository.getPointHistory(id)).thenReturn(List.of());

        //when
        NullPointerException e = assertThrows(NullPointerException.class, () -> pointService.getPointHistory(id));

        //then
        assertThat(e.getClass().getSimpleName()).isEqualTo("NullPointerException");
    }

    @Test
    void 존재하는_유저의_포인트를_충전한다() {
        //given
        UserPoint userPoint = UserPoint.empty(1L);
        long chargePoint = 100L;
        long historyTime = System.currentTimeMillis();
        PointHistory pointHistory = pointHistoryTable.insert(userPoint.id(), chargePoint, TransactionType.CHARGE, historyTime);
        //모든 인수를 argument matcher로 만들어야 테스트 진행이 가능
        when(pointRepository.getPointHistory(userPoint.id())).thenReturn(List.of(new PointHistory(1, userPoint.id(), 0, TransactionType.CHARGE, 0)));
        when(pointRepository.getUserPoint(userPoint.id())).thenReturn(userPoint);
        when(pointRepository.insertPointHistory(eq(userPoint.id()), eq(chargePoint), eq(TransactionType.CHARGE), anyLong())).thenReturn(pointHistory);
        when(pointRepository.insertUserPoint(userPoint.id(), userPoint.point() + chargePoint)).thenReturn(new UserPoint(userPoint.id(), userPoint.point() + chargePoint, 0));

        //when
        UserPoint resultUserPoint = pointService.chargePoint(userPoint.id(), chargePoint);

        //then
        assertThat(resultUserPoint.id()).isEqualTo(userPoint.id());
        assertThat(resultUserPoint.point()).isEqualTo(userPoint.point() + chargePoint);
    }

    @Test
    void 존재하지_않는_유저의_포인트를_충전하면_NP가_발생한다() {
        //given
        UserPoint userPoint = UserPoint.empty(1L);
        long chargePoint = 100L;
        when(pointRepository.getPointHistory(userPoint.id())).thenReturn(List.of());

        //when
        NullPointerException e = assertThrows(NullPointerException.class, () -> pointService.chargePoint(userPoint.id(), chargePoint));

        //then
        assertThat(e.getClass().getSimpleName()).isEqualTo("NullPointerException");
    }

    @Test
    void 존재하는_유저의_포인트를_사용한다() {
        //given
        UserPoint userPoint = UserPoint.empty(1L);
        long usePoint = 100L;
        long historyTime = System.currentTimeMillis();
        PointHistory pointHistory = pointHistoryTable.insert(userPoint.id(), usePoint, TransactionType.USE, historyTime);
        //모든 인수를 argument matcher로 만들어야 테스트 진행이 가능
        when(pointRepository.getPointHistory(userPoint.id())).thenReturn(List.of(new PointHistory(1, userPoint.id(), 0, TransactionType.USE, 0)));
        when(pointRepository.getUserPoint(userPoint.id())).thenReturn(userPoint);
        when(pointRepository.insertPointHistory(eq(userPoint.id()), eq(usePoint), eq(TransactionType.USE), anyLong())).thenReturn(pointHistory);
        when(pointRepository.insertUserPoint(userPoint.id(), userPoint.point() - usePoint)).thenReturn(new UserPoint(userPoint.id(), userPoint.point() - usePoint, 0));

        //when
        UserPoint resultUserPoint = pointService.usePoint(userPoint.id(), usePoint);

        //then
        assertThat(resultUserPoint.id()).isEqualTo(userPoint.id());
        assertThat(resultUserPoint.point()).isEqualTo(userPoint.point() - usePoint);
    }

    @Test
    void 존재하지_않는_유저의_포인트를_사용하면_NP가_발생한다() {
        //given
        UserPoint userPoint = UserPoint.empty(1L);
        long usePoint = 100L;
        when(pointRepository.getPointHistory(userPoint.id())).thenReturn(List.of());

        //when
        NullPointerException e = assertThrows(NullPointerException.class, () -> pointService.usePoint(userPoint.id(), usePoint));

        //then
        assertThat(e.getClass().getSimpleName()).isEqualTo("NullPointerException");
    }
  
}