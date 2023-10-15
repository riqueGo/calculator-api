package com.rique.calculator.controller;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.rique.calculator.model.EquationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final List<String> equationHistory = new ArrayList<>();
    private BigDecimal lastResult;

    @PostMapping("/evaluate")
    public ResponseEntity<Object> evaluate(@RequestBody @Valid EquationRequest equationRequest) {
        String equation = equationRequest.getEquation();
        try {
            Expression expression = new Expression(equation);
            BigDecimal result = expression.evaluate().getNumberValue();
            equation += " = " + result.toString();

            lastResult = result;
            equationHistory.add(equation);

            return ResponseEntity.ok(result);
        } catch (EvaluationException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/lastResult")
    public ResponseEntity<BigDecimal> getLastResult() {
        if (lastResult != null) {
            return ResponseEntity.ok(lastResult);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("history")
    public ResponseEntity<List<String>> getHistory() {
        return ResponseEntity.ok(equationHistory);
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory() {
        equationHistory.clear();
        return ResponseEntity.noContent().build();
    }
}
