package br.com.estacionamento.model;

import br.com.estacionamento.interfaces.Tarifavel;
import java.time.LocalDateTime;

/**
 * Representa um Ticket de Estacionamento para Motocicletas.
 * Implementa as regras específicas de precificação para motos (polimorfismo).
 */
public class TicketMoto extends TicketEstacionamento {

    private static final double TARIFA_PRIMEIRA_HORA = 7.00;
    private static final double TARIFA_HORA_ADICIONAL = 3.50;

    public TicketMoto() {
        super();
    }

    public TicketMoto(int numero, String placa, String modelo, LocalDateTime horaEntrada) {
        super(numero, placa, modelo, horaEntrada);
    }

    @Override
    public String getTipoVeiculo() {
        return "Moto";
    }

    /**
     * Regra de negócio polimórfica para Motocicletas:
     * - Até 15 minutos (tolerância): R$ 0,00
     * - Até 60 minutos: R$ 7,00
     * - Horas subsequentes (ou fração): + R$ 3,50 por hora
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
