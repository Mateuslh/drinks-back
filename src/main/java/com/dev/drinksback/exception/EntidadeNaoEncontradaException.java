package com.dev.drinksback.exception;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(Class<?> c) {
        super(c.getSimpleName() + " não encontrado(a).");
    }
}