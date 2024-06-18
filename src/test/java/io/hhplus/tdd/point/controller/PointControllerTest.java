package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.ExceptionType;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
@ExtendWith(SpringExtension.class)
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointService pointService;
    
    long id;

    @BeforeEach
    void setUp() {
        id = 1L;
    }

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     * @throws Exception
     */
    @Test
    void 유저_조회() throws Exception {

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, 0, 0);
        when(pointService.getUserPoint(id)).thenReturn(userPoint);

        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    /**
     * id에 해당하는 유저가 존재하지 않을 경우의 테스트
     * @throws Exception
     */
    @Test
    void 유저_조회_실패() throws Exception {

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.getUserPoint(id)).thenThrow(NullPointerException.class);

        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));
    }

    /**
     * id에 해당하는 유저의 포인트 내역을 조회해오기 위한 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_내역_조회() throws Exception {

        //MockBean 객체의 역할 부여
        when(pointService.getPointHistory(id)).thenReturn(List.of(new PointHistory(1, id, 100, TransactionType.CHARGE, 0)));

        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id + "/histories"));

        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(id));
    }

    /**
     * 존재하지 않는 유저의 포인트 내역을 조회하는 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_내역_조회_실패() throws Exception {

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.getPointHistory(id)).thenThrow(NullPointerException.class);

        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id + "/histories"));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));
    }

    /**
     * id에 해당하는 유저의 포인트를 충전하는 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_충전() throws Exception {

        long amount = 100L;
        long defaultAmount = 0L; //기존 포인트

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, defaultAmount + amount, 0);
        when(pointService.chargePoint(id, amount)).thenReturn(userPoint);

        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/charge").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(defaultAmount + amount));
    }

    /**
     * 존재하지 않는 유저의 포인트를 충전하는 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_충전_실패() throws Exception {

        long amount = 100L;

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.chargePoint(id, amount)).thenThrow(NullPointerException.class);

        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/charge").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));
    }

    /**
     * id에 해당하는 유저의 포인트를 사용하는 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_사용() throws Exception {

        long amount = 100L;
        long defaultAmount = 200L; //기존 포인트

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, defaultAmount - amount, 0);
        when(pointService.usePoint(id, amount)).thenReturn(userPoint);

        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/use").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(defaultAmount - amount));
    }

    /**
     * 존재하지 않는 유저의 포인트를 사용하는 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_사용_실패() throws Exception {

        long amount = 100L;

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.usePoint(id, amount)).thenThrow(NullPointerException.class);

        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/use").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));
    }

}