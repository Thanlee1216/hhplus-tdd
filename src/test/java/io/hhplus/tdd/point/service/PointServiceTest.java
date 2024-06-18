package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    PointServiceImpl pointService;

    @Mock
    PointRepository pointRepository;

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     */
    @Test
    void 포인트_조회() {
        long id = 1L;
        when(pointRepository.getUserPoint(id)).thenReturn(new UserPoint(id, 0, 0));
        UserPoint userPoint = pointService.getUserPoint(id);
        assertEquals(id, userPoint.id());
    }

    /**
     * 존재하지 않는 유저를 조회하는 테스트
     * service에서 NFE를 발생시키고 컨트롤러에서 CustomException을 발생
     * TODO - 존재하지 않는 유저를 조회하면 에러를 발생시킬 것인가, 기존 메소드대로 신규 생성을 할 것인가 고민 필요
     */
    @Test
    void 포인트_조회_실패() {
        long id = 1L;
        when(pointRepository.getUserPoint(id)).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> pointService.getUserPoint(id));
    }
  
}