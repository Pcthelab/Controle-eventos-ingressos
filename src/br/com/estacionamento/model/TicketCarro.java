package br.com.estacionamento.model;

import br.com.estacionamento.interfaces.Tarifavel;
import java.time.LocalDateTime;

/**
 * Representa um Ticket de Estacionamento para Carros / Automóveis.
 * Implementa as regras específicas de precificação para carros (polimorfismo).
 */
public class TicketCarro extends TicketEstacionamento {

    private static final double TARIFA_PRIMEIRA_HORA = 12.00;
    private static final double TARIFA_HORA_ADICIONAL = 6.00;

    public TicketCarro() {
        super();
    }

    public TicketCarro(int numero, String placa, String modelo, LocalDateTime horaEntrada) {
        super(numero, placa, modelo, horaEntrada);
    }

    @Override
    public String getTipoVeiculo() {
        return "Carro";
    }

    /**
     * Regra de negócio polimórfica para Carros:
     * - Até 15 minutos (tolerância): R$ 0,00
     * - Até 60 minutos: R$ 12,00
     * - Horas subsequentes (ou fração): + R$ 6,00 por hora
     */
    @Override
    public double calcularPrecoEstadia(long minutosPermanencia) {
        if (minutosPermanencia <= Tarifavel.obterToleranciaMinutos()) {
            return 0.0;
        }

        if (minutosPermanencia <= 60) {
            return TARIFA_PRIMEIRA_HORA;
        }

        long minutosExcedentes = minutosPermanencia - 60;
        long horasAdicionais = (long) Math.ceil((double) minutosExcedentes / 60.0);

        return TARIFA_PRIMEIRA_HORA + (horasAdicionais * TARIFA_HORA_ADICIONAL);
    }
}
