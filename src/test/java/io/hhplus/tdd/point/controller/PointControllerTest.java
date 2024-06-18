package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.ExceptionType;
import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     * @throws Exception
     */
    @Test
    void 유저_조회() throws Exception {

        //Case 1 : id가 1인 유저 조회
        long id = 1;

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

        //Case 2 : id가 999인 유저 조회
        long id = 999;

        //MockBean 객체의 역할 부여
        UserPoint userPoint = new UserPoint(id, 0, 0);

        //존재하지 않는 유저를 조회할 경우 PointException을 발생시키는 테스트 세팅
        when(pointService.getUserPoint(id)).thenThrow(NullPointerException.class);

        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(ExceptionType.getMessage("NOT_EXIST_USER")));

    }

}