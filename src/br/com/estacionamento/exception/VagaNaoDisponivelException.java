package br.com.estacionamento.exception;

/**
 * Checked Exception customizada lançada quando o pátio atinge a capacidade máxima
 * e não há vagas disponíveis para novas entradas de veículos.
 * Herda diretamente de java.lang.Exception nos moldes dos materiais da disciplina (POO_08).
 */
public class VagaNaoDisponivelException extends Exception {

    private static final long serialVersionUID = 1L;

    public VagaNaoDisponivelException() {
        super("Capacidade máxima do estacionamento atingida! Não há vagas disponíveis no momento.");
    }

    public VagaNaoDisponivelException(String mensagem) {
        super(mensagem);
    }

    public VagaNaoDisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
