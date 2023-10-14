package com.rique.calculator.controller;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.rique.calculator.model.EquationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    @PostMapping("/evaluate")
    public ResponseEntity<Object> evaluate(@RequestBody EquationRequest equationRequest) {
        try {
            Expression expression = new Expression(equationRequest.getEquation());
            BigDecimal result = expression.evaluate().getNumberValue();
            return ResponseEntity.ok(result);
        } catch (EvaluationException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
