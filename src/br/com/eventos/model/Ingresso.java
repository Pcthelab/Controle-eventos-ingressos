package br.com.eventos.model;

import br.com.eventos.exception.IngressoInvalidoException;
import br.com.eventos.interfaces.Validavel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Ingresso implements Comparable<Ingresso>, Validavel {

    public static final DateTimeFormatter FORMATADOR_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int codigo;
    private String evento;
    private String nomeParticipante;
    private double valor;
    private boolean utilizado;
    private boolean cancelado;
    private LocalDateTime dataHoraAcesso;

    public Ingresso() {
    }

    public Ingresso(int codigo, String evento, String nomeParticipante, double valor) {
        this.codigo = codigo;
        this.evento = evento != null ? evento.trim() : "";
        this.nomeParticipante = nomeParticipante != null ? nomeParticipante.trim() : "";
        this.valor = Math.max(0.0, valor);
    }

    public abstract String getTipo();

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
        this.evento = evento != null ? evento.trim() : "";
    }

    public String getNomeParticipante() {
        return nomeParticipante;
    }

    public void setNomeParticipante(String nomeParticipante) {
        this.nomeParticipante = nomeParticipante != null ? nomeParticipante.trim() : "";
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

    @Override
    public boolean validarAcessoCatraca() throws IngressoInvalidoException {
        if (cancelado) {
            throw new IngressoInvalidoException("Acesso negado: o ingresso #" + codigo + " foi cancelado.");
        }
        if (utilizado) {
            String hora = dataHoraAcesso != null ? dataHoraAcesso.format(FORMATADOR_DATA_HORA) : "horário anterior";
            throw new IngressoInvalidoException("Acesso negado: o ingresso #" + codigo + " já foi utilizado em " + hora + ".");
        }

        this.utilizado = true;
        this.dataHoraAcesso = LocalDateTime.now();
        return true;
    }

    public void cancelar() throws IngressoInvalidoException {
        if (cancelado) {
            throw new IngressoInvalidoException("O ingresso #" + codigo + " já está cancelado.");
        }
        if (utilizado) {
            throw new IngressoInvalidoException("Não é possível cancelar um ingresso que já foi utilizado.");
        }
        this.cancelado = true;
    }

    public String getStatus() {
        if (cancelado) {
            return "Cancelado";
        }
        if (utilizado) {
            return "Utilizado";
        }
        return "Disponível";
    }

    @Override
    public int compareTo(Ingresso outro) {
        if (outro == null) {
            return 1;
        }
        return Integer.compare(this.codigo, outro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ingresso)) {
            return false;
        }
        Ingresso ingresso = (Ingresso) o;
        return codigo == ingresso.codigo && Objects.equals(evento, ingresso.evento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, evento);
    }

    @Override
    public String toString() {
        String acesso = dataHoraAcesso != null ? dataHoraAcesso.format(FORMATADOR_DATA_HORA) : "Não utilizado";
        return String.format("[#%04d] %s | %s | %s | %s | %s | Acesso: %s",
                codigo, evento, nomeParticipante, getTipo(), formatarValor(valor), getStatus(), acesso);
    }
}
