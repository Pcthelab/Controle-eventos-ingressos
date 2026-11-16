package br.com.estacionamento.interfaces;

/**
 * Interface de Negócio Tarifavel
 * Define o contrato para cálculo de estadia em estacionamento
 * e métodos utilitários (default e static) do Java moderno.
 */
public interface Tarifavel {

    /**
     * Calcula o preço total da estadia baseado nos minutos de permanência.
     * @param minutosPermanencia tempo total que o veículo permaneceu no pátio
     * @return valor total a pagar em reais
     */
    double calcularPrecoEstadia(long minutosPermanencia);

    /**
     * Método default: Formata um valor numérico para padrão monetário brasileiro.
     * @param valor quantia em reais
     * @return texto formatado no formato "R$ XX,XX"
     */
    default String formatarValor(double valor) {
        return String.format("R$ %.2f", valor);
    }

    /**
     * Método default: Aplica desconto de convênio se aplicável.
     * @param valorOriginal valor antes do desconto
     * @param percentualDesconto porcentagem de desconto (ex: 10.0 para 10%)
     * @return valor com desconto aplicado
     */
    default double aplicarDescontoConvenio(double valorOriginal, double percentualDesconto) {
        if (percentualDesconto <= 0) return valorOriginal;
        double desconto = valorOriginal * (percentualDesconto / 100.0);
        return Math.max(0.0, valorOriginal - desconto);
    }

    /**
     * Método estático: Retorna o tempo padrão de tolerância sem cobrança em minutos.
     * @return minutos de tolerância gratuita (ex: 15 minutos)
     */
    static int obterToleranciaMinutos() {
        return 15;
    }
}
