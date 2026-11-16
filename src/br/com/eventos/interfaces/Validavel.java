package br.com.eventos.interfaces;

import br.com.eventos.exception.IngressoInvalidoException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Interface de Negócio Validavel (Requisito POO_10).
 * Contém o contrato de validação na portaria/catraca e métodos
 * default e static do Java moderno.
 */
public interface Validavel {

    /**
     * Contrato abstrato para conferência e validação de acesso na portaria/catraca.
     * @return true se o acesso for concedido
     * @throws IngressoInvalidoException se o ingresso for nulo, cancelado, já utilizado ou com documento irregular
     */
    boolean validarAcessoCatraca() throws IngressoInvalidoException;

    /**
     * Método default: Formata valores monetários no padrão da moeda corrente brasileira (R$).
     * @param valor quantia numérica
     * @return texto formatado (ex: "R$ 50,00")
     */
    default String formatarValor(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    /**
     * Método default: Retorna a instrução operacional esperada na catraca de acordo com o tipo de bilhete.
     * @return orientação ao operador da portaria
     */
    default String obterInstrucaoCatraca() {
        return "Conferir bilhete e liberar catraca para o participante.";
    }

    /**
     * Método static: Calcula o valor da meia-entrada aplicando 50% de desconto sobre o valor base da inteira.
     * @param valorInteira preço da inteira
     * @return preço da meia-entrada calculado
     */
    static double calcularValorMeia(double valorInteira) {
        if (valorInteira <= 0) return 0.0;
        return valorInteira / 2.0;
    }
}
