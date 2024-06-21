package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PointServiceConcurrencyTest {

    @Autowired
    PointService pointService;

    @Test
    void 동시성_테스트() {
        //given
        long id = 1L;
        UserPoint userPoint = pointService.getUserPoint(id);
        pointService.chargePoint(userPoint.id(), 100000L);

        //when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    pointService.chargePoint(userPoint.id(), 10000L);
                }),
                CompletableFuture.runAsync(() -> {
                    pointService.usePoint(userPoint.id(), 20000L);
                }),
                CompletableFuture.runAsync(() -> {
                    pointService.usePoint(userPoint.id(), 300L);
                })
        ).join();

        //then
        UserPoint resultUserPoint = pointService.getUserPoint(id);
        assertThat(resultUserPoint.point()).isEqualTo(100000L + 10000L - 20000L - 300L);
    }
}
