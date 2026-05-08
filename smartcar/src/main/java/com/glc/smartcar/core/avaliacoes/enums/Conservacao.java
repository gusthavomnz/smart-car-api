package com.glc.smartcar.core.avaliacoes.enums;

public enum Conservacao {
    NOVO(1.0),
    BOM(0.75),
    REGULAR(0.5),
    RUIM(0.25);


    private final double valor;

    Conservacao(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }
}
