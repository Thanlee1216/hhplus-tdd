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
     * 존재하지 않는 유저는 신규로 생성
     * 신규 유저를 생성할 때 정보를 기입하거나 신규 유저로 등록되었다는 메세지 리턴은 요구사항이 없기에 제외
     * @throws Exception
     */
    @Test
    void 유저를_조회하고_존재하지_않는_유저_조회_시_신규_유저로_등록한다() throws Exception {

        //given
        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, 0, 0);
        when(pointService.getUserPoint(id)).thenReturn(userPoint);

        //when
        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        //then
        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    /**
     * id에 해당하는 유저의 포인트 내역을 조회해오기 위한 테스트
     * @throws Exception
     */
    @Test
    void 유저_포인트_내역_조회() throws Exception {

        //given
        //MockBean 객체의 역할 부여
        when(pointService.getPointHistory(id)).thenReturn(List.of(new PointHistory(1, id, 100, TransactionType.CHARGE, 0)));

        //when
        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id + "/histories"));

        //then
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

        //given
        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.getPointHistory(id)).thenThrow(NullPointerException.class);

        //when
        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id + "/histories"));

        //then
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

        //given
        long amount = 100L;
        long defaultAmount = 0L; //기존 포인트

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, defaultAmount + amount, 0);
        when(pointService.chargePoint(id, amount)).thenReturn(userPoint);

        //when
        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/charge").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        //then
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

        //given
        long amount = 100L;

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.chargePoint(id, amount)).thenThrow(NullPointerException.class);

        //when
        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/charge").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        //then
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

        //given
        long amount = 100L;
        long defaultAmount = 200L; //기존 포인트

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, defaultAmount - amount, 0);
        when(pointService.usePoint(id, amount)).thenReturn(userPoint);

        //when
        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/use").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        //then
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

        //given
        long amount = 100L;

        //MockBean 객체의 역할 부여
        //존재하지 않는 유저를 조회(NPE)할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.usePoint(id, amount)).thenThrow(NullPointerException.class);

        //when
        ResultActions resultAcitons = mockMvc.perform(patch("/point/" + id + "/use").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(amount)));

        //then
        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));
    }

}