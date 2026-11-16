package br.com.eventos.model;

/**
 * Representa um Ingresso do tipo Inteira (Requisito POO_07 e POO_10).
 */
public class IngressoInteira extends Ingresso {

    public IngressoInteira() {
        super();
    }

    public IngressoInteira(int codigo, String evento, String nomeParticipante, double valor) {
        super(codigo, evento, nomeParticipante, valor);
    }

    @Override
    public String getTipo() {
        return "Inteira";
    }

    @Override
    public String obterInstrucaoCatraca() {
        return "Acesso Inteira: Conferência simples de bilhete e liberação direta.";
    }
}
