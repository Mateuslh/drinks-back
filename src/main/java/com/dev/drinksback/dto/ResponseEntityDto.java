package com.dev.drinksback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntityDto<T> {
    private boolean success = true;
    private String message = "Success";
    private T content;

    public ResponseEntityDto setContent(T content) {
        this.content = content;
        return this;
    }
}
