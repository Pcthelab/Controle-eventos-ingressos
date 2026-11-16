package br.com.eventos.model;

import br.com.eventos.exception.IngressoInvalidoException;
import br.com.eventos.interfaces.Validavel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Entidade Principal Abstrata Ingresso (Requisito POO_07, POO_09 e POO_10).
 * Implementa Comparable para ordenação natural por código sequencial do bilhete
 * e a interface de negócio Validavel para conferência polimórfica na catraca.
 */
public abstract class Ingresso implements Comparable<Ingresso>, Validavel {

    private int codigo;
    private String evento;
    private String nomeParticipante;
    private double valor;
    private boolean utilizado;
    private boolean cancelado;
    private LocalDateTime dataHoraAcesso;

    public static final DateTimeFormatter FORMATADOR_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Construtor padrão
    public Ingresso() {
        this.utilizado = false;
        this.cancelado = false;
        this.dataHoraAcesso = null;
    }

    // Construtor completo
    public Ingresso(int codigo, String evento, String nomeParticipante, double valor) {
        this.codigo = codigo;
        this.evento = (evento != null) ? evento.trim() : "";
        this.nomeParticipante = (nomeParticipante != null) ? nomeParticipante.trim() : "";
        this.valor = Math.max(0.0, valor);
        this.utilizado = false;
        this.cancelado = false;
        this.dataHoraAcesso = null;
    }

    /**
     * Método abstrato implementado pelas subclasses para identificar o tipo do bilhete.
     */
    public abstract String getTipo();

    // Getters e Setters (Encapsulamento POO)
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = (evento != null) ? evento.trim() : "";
    }

    public String getNomeParticipante() {
        return nomeParticipante;
    }

    public void setNomeParticipante(String nomeParticipante) {
        this.nomeParticipante = (nomeParticipante != null) ? nomeParticipante.trim() : "";
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = Math.max(0.0, valor);
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    public LocalDateTime getDataHoraAcesso() {
        return dataHoraAcesso;
    }

    public void setDataHoraAcesso(LocalDateTime dataHoraAcesso) {
        this.dataHoraAcesso = dataHoraAcesso;
    }

    /**
     * Validação na catraca (contrato da interface Validavel).
     * Sobrescrito nas subclasses para comportamentos polimórficos adicionais.
     */
    @Override
    public boolean validarAcessoCatraca() throws IngressoInvalidoException {
        if (cancelado) {
            throw new IngressoInvalidoException("ACESSO NEGADO: Este ingresso (#" + codigo + ") foi CANCELADO e não pode ser utilizado.");
        }
        if (utilizado) {
            String horaStr = (dataHoraAcesso != null) ? dataHoraAcesso.format(FORMATADOR_DATA_HORA) : "horário anterior";
            throw new IngressoInvalidoException("ACESSO NEGADO: Este ingresso (#" + codigo + ") JÁ FOI UTILIZADO em " + horaStr + "!");
        }

        this.utilizado = true;
        this.dataHoraAcesso = LocalDateTime.now();
        return true;
    }

    /**
     * Cancela o ingresso caso ele ainda esteja elegível.
     */
    public void cancelar() throws IngressoInvalidoException {
        if (cancelado) {
            throw new IngressoInvalidoException("O ingresso #" + codigo + " já está cancelado.");
        }
        if (utilizado) {
            throw new IngressoInvalidoException("Não é permitido cancelar um ingresso que já foi utilizado na portaria.");
        }
        this.cancelado = true;
    }

    /**
     * Retorna a descrição amigável do status do bilhete.
     */
    public String getStatus() {
        if (cancelado) return "Cancelado";
        if (utilizado) return "Utilizado (Acesso Realizado)";
        return "Disponível (Vendido)";
    }

    /**
     * Requisito POO_09: Ordenação natural com Comparable<T>.
     * O Tema 08 exige expressamente: ordenação por código sequencial do bilhete.
     */
    @Override
    public int compareTo(Ingresso outro) {
        if (outro == null) return 1;
        return Integer.compare(this.codigo, outro.codigo);
    }

    /**
     * Requisito POO_09: Sobrescrita de equals() e hashCode().
     * Considera único o código sequencial do ingresso no evento.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingresso)) return false;
        Ingresso ingresso = (Ingresso) o;
        return codigo == ingresso.codigo && Objects.equals(evento, ingresso.evento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, evento);
    }

    @Override
    public String toString() {
        String acessoStr = (dataHoraAcesso != null) ? dataHoraAcesso.format(FORMATADOR_DATA_HORA) : "Não utilizado";
        return String.format("[Ingresso #%04d] Evento: %s | Participante: %s | Tipo: %s | Valor: %s | Status: %s | Acesso: %s",
                codigo, evento, nomeParticipante, getTipo(), formatarValor(valor), getStatus(), acessoStr);
    }
}
