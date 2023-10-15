package com.rique.calculator.model;

import jakarta.validation.constraints.NotNull;

public class EquationRequest {
    @NotNull
    private String equation;

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }
}
