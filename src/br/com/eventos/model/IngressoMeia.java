package br.com.eventos.model;

import br.com.eventos.exception.IngressoInvalidoException;

public class IngressoMeia extends Ingresso {

    private static final String COMPROVANTE_PADRAO = "Carteira Estudantil / DNE";

    private String documentoComprovante;

    public IngressoMeia() {
        super();
        this.documentoComprovante = COMPROVANTE_PADRAO;
    }

    public IngressoMeia(int codigo, String evento, String nomeParticipante, double valor) {
        this(codigo, evento, nomeParticipante, valor, COMPROVANTE_PADRAO);
    }

    public IngressoMeia(int codigo, String evento, String nomeParticipante, double valor, String documentoComprovante) {
        super(codigo, evento, nomeParticipante, valor);
        this.documentoComprovante = (documentoComprovante != null && !documentoComprovante.trim().isEmpty())
                ? documentoComprovante.trim()
                : COMPROVANTE_PADRAO;
    }

    public String getDocumentoComprovante() {
        return documentoComprovante;
    }

    public void setDocumentoComprovante(String documentoComprovante) {
        this.documentoComprovante = documentoComprovante != null ? documentoComprovante.trim() : "";
    }

    @Override
    public String getTipo() {
        return "Meia-entrada";
    }

    @Override
    public String obterInstrucaoCatraca() {
        return "Atenção portaria: exigir apresentação de " + documentoComprovante + ".";
    }

    @Override
    public boolean validarAcessoCatraca() throws IngressoInvalidoException {
        if (documentoComprovante == null || documentoComprovante.trim().isEmpty()) {
            throw new IngressoInvalidoException("Acesso negado: ingresso meia-entrada sem documento comprobatório.");
        }
        return super.validarAcessoCatraca();
    }

    @Override
    public String toString() {
        return super.toString() + " | Comprovante: " + documentoComprovante;
    }
}
