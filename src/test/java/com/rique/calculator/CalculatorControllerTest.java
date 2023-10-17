package com.rique.calculator;

import com.rique.calculator.controller.CalculatorController;
import com.rique.calculator.model.EquationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(CalculatorController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CalculatorController calculatorController;

    @Test
    public void testEvaluateValidEquation() throws Exception {
        EquationRequest equationRequest = new EquationRequest();
        equationRequest.setEquation("5+3*2");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/calculator/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equation\": \"" + equationRequest.getEquation() + "\"}")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("11"));
    }

    @Test
    public void testEvaluateInvalidEquation() throws Exception {
        EquationRequest equationRequest = new EquationRequest();
        equationRequest.setEquation("invalid equation");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/calculator/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equation\": \"" + equationRequest.getEquation() + "\"}")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testNotFoundLastResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/calculator/lastResult")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetLastResult() throws Exception {
        ReflectionTestUtils.setField(calculatorController, "lastResult", new BigDecimal(12));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/calculator/lastResult")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("12"));
    }

    @Test
    public void testGetHistoryWithEntriesAndDeleteThem() throws Exception {
        List<String> equationHistory = new ArrayList<>(List.of("5+3*2", "10/2"));

        ReflectionTestUtils.setField(calculatorController, "equationHistory", equationHistory);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/calculator/history")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[\"5+3*2\",\"10/2\"]"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/calculator/history")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        equationHistory = (List<String>) ReflectionTestUtils.getField(calculatorController, "equationHistory");
        assert equationHistory.isEmpty();
    }

    @Test
    public void testGetHistoryEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/calculator/history")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }
}
