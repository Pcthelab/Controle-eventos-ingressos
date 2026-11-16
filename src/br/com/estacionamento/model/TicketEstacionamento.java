package br.com.estacionamento.model;

import br.com.estacionamento.interfaces.Tarifavel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Entidade principal abstrata que representa um ticket de estacionamento.
 * Implementa Comparable para ordenação natural por horário de entrada
 * e a interface de negócio Tarifavel para cálculo polimórfico de estadia.
 */
public abstract class TicketEstacionamento implements Comparable<TicketEstacionamento>, Tarifavel {

    private int numero;
    private String placa;
    private String modelo;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private double valorPago;
    private boolean finalizado;

    public static final DateTimeFormatter FORMATADOR_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Construtor padrão
    public TicketEstacionamento() {
        this.horaEntrada = LocalDateTime.now();
        this.finalizado = false;
        this.valorPago = 0.0;
    }

    // Construtor com campos principais
    public TicketEstacionamento(int numero, String placa, String modelo, LocalDateTime horaEntrada) {
        this.numero = numero;
        this.placa = (placa != null) ? placa.toUpperCase().trim() : "";
        this.modelo = (modelo != null) ? modelo.trim() : "";
        this.horaEntrada = (horaEntrada != null) ? horaEntrada : LocalDateTime.now();
        this.horaSaida = null;
        this.valorPago = 0.0;
        this.finalizado = false;
    }

    // Método abstrato para identificar o tipo do veículo nas subclasses
    public abstract String getTipoVeiculo();

    // Getters e Setters com encapsulamento rigoroso
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = (placa != null) ? placa.toUpperCase().trim() : "";
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = (modelo != null) ? modelo.trim() : "";
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalDateTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    /**
     * Requisito POO_09: Ordenação natural com Comparable<T>.
     * O enunciado da P2 exige expressamente: ordenação pelo horário de entrada.
     */
    @Override
    public int compareTo(TicketEstacionamento outro) {
        if (outro == null || outro.horaEntrada == null) {
            return 1;
        }
        if (this.horaEntrada == null) {
            return -1;
        }
        int comparacaoData = this.horaEntrada.compareTo(outro.horaEntrada);
        if (comparacaoData != 0) {
            return comparacaoData;
        }
        // Desempate pelo número do ticket
        return Integer.compare(this.numero, outro.numero);
    }

    /**
     * Requisito POO_09: Sobrescrita de equals e hashCode.
     * Considera único o número do ticket e a placa.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEstacionamento that = (TicketEstacionamento) o;
        return numero == that.numero && Objects.equals(placa, that.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, placa);
    }

    @Override
    public String toString() {
        String entradaStr = (horaEntrada != null) ? horaEntrada.format(FORMATADOR_DATA_HORA) : "N/D";
        String saidaStr = (horaSaida != null) ? horaSaida.format(FORMATADOR_DATA_HORA) : "Ainda no pátio";
        String status = finalizado ? "FINALIZADO (" + formatarValor(valorPago) + ")" : "NO PÁTIO";

        return String.format("[Ticket #%03d] [%s] Placa: %-8s | Modelo: %-12s | Entrada: %s | Saída: %s | Status: %s",
                numero, getTipoVeiculo(), placa, modelo, entradaStr, saidaStr, status);
    }
}
