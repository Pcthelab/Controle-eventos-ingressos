package br.com.eventos.exception;

public class IngressoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public IngressoInvalidoException() {
        super("Ingresso inválido para a operação solicitada.");
    }

    public IngressoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public IngressoInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
