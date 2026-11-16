package br.com.eventos.model;

import br.com.eventos.exception.IngressoInvalidoException;

/**
 * Representa um Ingresso do tipo Meia-entrada (Requisito POO_07 e POO_10).
 * Especializa a regra de validação na portaria exigindo documentação comprobatória (polimorfismo).
 */
public class IngressoMeia extends Ingresso {

    private String documentoComprovante;

    public IngressoMeia() {
        super();
        this.documentoComprovante = "Comprovante Estudantil";
    }

    public IngressoMeia(int codigo, String evento, String nomeParticipante, double valor) {
        this(codigo, evento, nomeParticipante, valor, "Carteira Estudantil / DNE");
    }

    public IngressoMeia(int codigo, String evento, String nomeParticipante, double valor, String documentoComprovante) {
        super(codigo, evento, nomeParticipante, valor);
        this.documentoComprovante = (documentoComprovante != null && !documentoComprovante.trim().isEmpty())
                ? documentoComprovante.trim()
                : "Carteira Estudantil / DNE";
    }

    public String getDocumentoComprovante() {
        return documentoComprovante;
    }

    public void setDocumentoComprovante(String documentoComprovante) {
        this.documentoComprovante = (documentoComprovante != null) ? documentoComprovante.trim() : "";
    }

    @Override
    public String getTipo() {
        return "Meia-entrada";
    }

    @Override
    public String obterInstrucaoCatraca() {
        return "ATENÇÃO PORTARIA: Exigir obrigatoriamente a apresentação de: " + documentoComprovante;
    }

    /**
     * Validação polimórfica específica para Meia-entrada:
     * Verifica documento e invoca a validação de portaria.
     */
    @Override
    public boolean validarAcessoCatraca() throws IngressoInvalidoException {
        if (documentoComprovante == null || documentoComprovante.trim().isEmpty()) {
            throw new IngressoInvalidoException("ACESSO NEGADO: Ingresso meia-entrada sem documento comprobatório registrado.");
        }
        return super.validarAcessoCatraca();
    }

    @Override
    public String toString() {
        return super.toString() + " | Comprovante: " + documentoComprovante;
    }
}
