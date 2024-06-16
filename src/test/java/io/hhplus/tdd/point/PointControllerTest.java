package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * id에 해당하는 유저를 조회해오기 위한 테스트
     * @throws Exception
     */
    @Test
    void 유저_조회() throws Exception {

        //Case 1 : id가 1인 유저 조회
        //controller의 return 객체를 new UserPoint(1, 0, 0)로 수정하여 해결
        //TODO - UserPoint 세팅 시 id 값이 1인 유저는 항상 존재하도록 세팅할 것
        String id = "1";
        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        resultAcitons.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

    }

    @Test
    void 유저_조회_실패() throws Exception {

        //Case 2 : id가 999인 유저 조회
        //TODO - UserPoint 세팅 시 id 값이 999인 유저는 항상 존재하지 않도록 세팅할 것
        String id = "999";
        ResultActions resultAcitons = mockMvc.perform(get("/point/" + id));

        resultAcitons.andDo(print())
                .andExpect(status().isInternalServerError());

    }

}