package br.com.eventos.model;

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
        return "Acesso Inteira: conferência simples do bilhete e liberação direta.";
    }
}
