package com.example.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponseDto {
    private String code;

    @JsonProperty("Error")
    private int Error;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String code, int error) {
        this.code = code;
        this.Error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getError() {
        return Error;
    }

    public void setError(int error) {
        this.Error = error;
    }
}
