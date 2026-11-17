package br.com.eventos.interfaces;

import br.com.eventos.exception.IngressoInvalidoException;

import java.text.NumberFormat;
import java.util.Locale;

public interface Validavel {

    boolean validarAcessoCatraca() throws IngressoInvalidoException;

    default String formatarValor(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    default String obterInstrucaoCatraca() {
        return "Conferir bilhete e liberar catraca para o participante.";
    }

    static double calcularValorMeia(double valorInteira) {
        if (valorInteira <= 0) {
            return 0.0;
        }
        return valorInteira / 2.0;
    }
}
