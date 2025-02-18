package br.com.nlw.events.modules.shared.dtos;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private int statusCode;
    private String message;

    public ErrorResponseDTO(HttpStatus status, String message) {
        this.statusCode = status.value();
        this.message = message;
    }
}
