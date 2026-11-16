package br.com.eventos.exception;

/**
 * Checked Exception customizada para o domínio de eventos e ingressos (Requisito POO_08).
 * Estende diretamente java.lang.Exception nos moldes do exercício SemSaldoException.
 */
public class IngressoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public IngressoInvalidoException() {
        super("Ingresso inválido para a operação solicitada!");
    }

    public IngressoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public IngressoInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
