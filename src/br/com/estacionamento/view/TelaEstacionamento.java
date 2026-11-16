package br.com.estacionamento.view;

import br.com.estacionamento.exception.VagaNaoDisponivelException;
import br.com.estacionamento.model.TicketCarro;
import br.com.estacionamento.model.TicketEstacionamento;
import br.com.estacionamento.model.TicketMoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface Gráfica desenvolvida em javax.swing.*
 * Gerencia o Estacionamento Rotativo 100% em memória RAM (List<TicketEstacionamento>).
 * Implementa ActionListener para eventos de botões e WindowListener para controle de fechamento.
 */
public class TelaEstacionamento extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    // Persistência 100% em memória RAM conforme diretriz do trabalho
    private final List<TicketEstacionamento> listaTickets = new ArrayList<>();

    // Limite de capacidade para disparo da Checked Exception VagaNaoDisponivelException
    private static final int CAPACIDADE_MAXIMA = 5;
    private int contadorSequencialTicket = 1;

    // Componentes de Interface Gráfica
    private JTextField txtNumero;
    private JTextField txtPlaca;
    private JTextField txtModelo;
    private JComboBox<String> cbTipoVeiculo;
    private JTextField txtMinutosSimulados;

    private JLabel lblStatusVagas;
    private JTextArea txtRelatorio;
    private JScrollPane scrollRelatorio;

    private JButton btnCadastrar;
    private JButton btnPesquisar;
    private JButton btnProcessarOperacao;
    private JButton btnOrdenar;
    private JButton btnLimpar;
    private JButton btnSair;

    public TelaEstacionamento() {
        super("Sistema de Estacionamento Rotativo - P2 POO (Prof. Maromo)");
        configurarJanela();
        inicializarComponentes();
        atualizarPainelVagas();
        gerarDadosIniciaisDemonstracao();
        atualizarRelatorio("Sistema inicializado com sucesso. Pronto para operações.");
    }

    private void configurarJanela() {
        this.setSize(880, 680);
        this.setMinimumSize(new Dimension(800, 600));
        this.setLocationRelativeTo(null);
        // Interceptado pelo WindowListener
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
    }

    private void inicializarComponentes() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout(10, 10));
        ((JPanel) container).setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Painel de Topo: Título e Status de Vagas
        JPanel painelTopo = new JPanel(new BorderLayout(8, 8));
        painelTopo.setBackground(new Color(33, 50, 75));
        painelTopo.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("ESTACIONAMENTO ROTATIVO - CONTROLE EM MEMÓRIA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        lblStatusVagas = new JLabel("Vagas: 0 / " + CAPACIDADE_MAXIMA);
        lblStatusVagas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatusVagas.setForeground(new Color(175, 238, 238));

        painelTopo.add(lblTitulo, BorderLayout.WEST);
        painelTopo.add(lblStatusVagas, BorderLayout.EAST);
        container.add(painelTopo, BorderLayout.NORTH);

        // 2. Painel Central: Formulário de Entrada e Ações
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));

        // Formulário de Campos
        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        painelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Veículo e Ticket",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));

        painelFormulario.add(new JLabel("Número do Ticket (para busca/baixa):"));
        txtNumero = new JTextField();
        painelFormulario.add(txtNumero);

        painelFormulario.add(new JLabel("Placa do Veículo (ex: ABC-1234 / BRA2E19):"));
        txtPlaca = new JTextField();
        painelFormulario.add(txtPlaca);

        painelFormulario.add(new JLabel("Modelo do Veículo (ex: Civic, Gol, CB 500):"));
        txtModelo = new JTextField();
        painelFormulario.add(txtModelo);

        painelFormulario.add(new JLabel("Tipo de Veículo (Polimorfismo):"));
        cbTipoVeiculo = new JComboBox<>(new String[]{"Carro", "Moto"});
        painelFormulario.add(cbTipoVeiculo);

        painelFormulario.add(new JLabel("Minutos de Estadia (Simulação para Baixa):"));
        txtMinutosSimulados = new JTextField();
        txtMinutosSimulados.setToolTipText("Deixe em branco para tempo real ou digite minutos (ex: 45, 90, 180)");
        painelFormulario.add(txtMinutosSimulados);

        painelCentral.add(painelFormulario, BorderLayout.NORTH);

        // Barra de Botões com as ações exigidas no PDF
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));

        btnCadastrar = new JButton("Cadastrar (Entrada)");
        btnPesquisar = new JButton("Pesquisar");
        btnProcessarOperacao = new JButton("Processar Operação (Baixa)");
        btnOrdenar = new JButton("Ordenar (Horário Entrada)");
        btnLimpar = new JButton("Limpar Campos");
        btnSair = new JButton("Sair");

        // Estilização básica dos botões
        Font fonteBotao = new Font("Segoe UI", Font.PLAIN, 12);
        JButton[] botoes = {btnCadastrar, btnPesquisar, btnProcessarOperacao, btnOrdenar, btnLimpar, btnSair};
        for (JButton b : botoes) {
            b.setFont(fonteBotao);
            b.setFocusPainted(false);
            b.addActionListener(this);
            painelBotoes.add(b);
        }

        btnCadastrar.setBackground(new Color(220, 245, 220));
        btnProcessarOperacao.setBackground(new Color(220, 235, 250));
        btnOrdenar.setBackground(new Color(255, 250, 220));
        btnSair.setBackground(new Color(255, 225, 225));

        painelCentral.add(painelBotoes, BorderLayout.SOUTH);
        container.add(painelCentral, BorderLayout.CENTER);

        // 3. Painel Inferior: Relatórios e Extrato dos Veículos (JTextArea com JScrollPane)
        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));
        painelInferior.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Extrato em Memória / Relatório de Movimentação",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));

        txtRelatorio = new JTextArea(13, 70);
        txtRelatorio.setEditable(false);
        txtRelatorio.setFont(new Font("Consolas", Font.PLAIN, 12));
        scrollRelatorio = new JScrollPane(txtRelatorio);
        painelInferior.add(scrollRelatorio, BorderLayout.CENTER);

        container.add(painelInferior, BorderLayout.SOUTH);
    }

    /**
     * Popula alguns registros iniciais para demonstrar imediatamente
     * a aplicação em funcionamento e a ordenação.
     */
    private void gerarDadosIniciaisDemonstracao() {
        LocalDateTime agora = LocalDateTime.now();
        // Veículo 1 - entrou há 2 horas
        TicketCarro c1 = new TicketCarro(contadorSequencialTicket++, "ABC-1234", "Toyota Corolla", agora.minusMinutes(120));
        // Veículo 2 - entrou há 45 minutos
        TicketMoto m1 = new TicketMoto(contadorSequencialTicket++, "XYZ-9876", "Honda CG 160", agora.minusMinutes(45));
        // Veículo 3 - entrou há 90 minutos
        TicketCarro c2 = new TicketCarro(contadorSequencialTicket++, "FATE-2024", "VW Gol", agora.minusMinutes(90));

        listaTickets.add(c1);
        listaTickets.add(m1);
        listaTickets.add(c2);
    }

    /**
     * Calcula quantos veículos estão atualmente no pátio (não finalizados).
     */
    private int contarVeiculosNoPatio() {
        int count = 0;
        for (TicketEstacionamento t : listaTickets) {
            if (!t.isFinalizado()) {
                count++;
            }
        }
        return count;
    }

    private void atualizarPainelVagas() {
        int ocupadas = contarVeiculosNoPatio();
        lblStatusVagas.setText(String.format("Pátio: %d / %d vagas ocupadas", ocupadas, CAPACIDADE_MAXIMA));
        if (ocupadas >= CAPACIDADE_MAXIMA) {
            lblStatusVagas.setForeground(new Color(255, 120, 120));
        } else {
            lblStatusVagas.setForeground(new Color(175, 238, 238));
        }
    }

    /**
     * Atualiza o JTextArea com o extrato dos veículos em memória.
     */
    private void atualizarRelatorio(String cabecalhoMensagem) {
        StringBuilder sb = new StringBuilder();
        if (cabecalhoMensagem != null && !cabecalhoMensagem.isEmpty()) {
            sb.append(">>> ").append(cabecalhoMensagem).append("\n\n");
        }

        sb.append(String.format("%-8s | %-6s | %-9s | %-15s | %-20s | %-12s | %s\n",
                "TICKET", "TIPO", "PLACA", "MODELO", "ENTRADA", "VALOR PAGO", "STATUS"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");

        if (listaTickets.isEmpty()) {
            sb.append("Nenhum registro no momento (Coleção em memória vazia).\n");
        } else {
            for (TicketEstacionamento t : listaTickets) {
                String entradaFormatada = (t.getHoraEntrada() != null)
                        ? t.getHoraEntrada().format(TicketEstacionamento.FORMATADOR_DATA_HORA) : "N/D";
                String valorFormatado = t.isFinalizado() ? t.formatarValor(t.getValorPago()) : "-";
                String statusDesc = t.isFinalizado() ? "LIBERADO / PAGO" : "NO PÁTIO";

                sb.append(String.format("#%-7d | %-6s | %-9s | %-15s | %-20s | %-12s | %s\n",
                        t.getNumero(),
                        t.getTipoVeiculo(),
                        t.getPlaca(),
                        t.getModelo(),
                        entradaFormatada,
                        valorFormatado,
                        statusDesc));
            }
        }

        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("Total de registros em memória: %d | Veículos atualmente no pátio: %d\n",
                listaTickets.size(), contarVeiculosNoPatio()));

        txtRelatorio.setText(sb.toString());
        txtRelatorio.setCaretPosition(0);
        atualizarPainelVagas();
    }

    private void limparCampos() {
        txtNumero.setText("");
        txtPlaca.setText("");
        txtModelo.setText("");
        txtMinutosSimulados.setText("");
        cbTipoVeiculo.setSelectedIndex(0);
        txtPlaca.requestFocus();
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO E AÇÕES DOS BOTÕES
    // =========================================================================

    /**
     * Operação 1: Cadastrar (Entrada de Veículo).
     * Dispara a Checked Exception customizada VagaNaoDisponivelException se o pátio estiver lotado!
     */
    private void acaoCadastrar() {
        try {
            // Validação de entrada
            String placa = txtPlaca.getText().trim();
            String modelo = txtModelo.getText().trim();
            String tipo = (String) cbTipoVeiculo.getSelectedItem();

            if (placa.isEmpty() || modelo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, preencha os campos obrigatórios: Placa e Modelo do veículo.",
                        "Validação de Campos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verifica se este veículo já está no pátio
            for (TicketEstacionamento t : listaTickets) {
                if (t.getPlaca().equalsIgnoreCase(placa) && !t.isFinalizado()) {
                    JOptionPane.showMessageDialog(this,
                            "O veículo de placa " + placa.toUpperCase() + " já se encontra no pátio (Ticket #" + t.getNumero() + ")!",
                            "Veículo Já Presente",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // REQUISITO TÉCNICO: Verificação de Vagas e Disparo da Checked Exception customizada
            if (contarVeiculosNoPatio() >= CAPACIDADE_MAXIMA) {
                throw new VagaNaoDisponivelException("O estacionamento atingiu sua capacidade máxima ("
                        + CAPACIDADE_MAXIMA + " vagas)! Libere uma vaga antes de registrar nova entrada.");
            }

            // Criação do objeto com base no polimorfismo de classes
            TicketEstacionamento novoTicket;
            int numeroTicket = contadorSequencialTicket++;
            LocalDateTime agora = LocalDateTime.now();

            if ("Moto".equalsIgnoreCase(tipo)) {
                novoTicket = new TicketMoto(numeroTicket, placa, modelo, agora);
            } else {
                novoTicket = new TicketCarro(numeroTicket, placa, modelo, agora);
            }

            // Adiciona na coleção em memória RAM
            listaTickets.add(novoTicket);

            JOptionPane.showMessageDialog(this,
                    "Entrada registrada com sucesso!\nTicket #" + novoTicket.getNumero()
                            + "\nVeículo: " + novoTicket.getTipoVeiculo() + " - " + novoTicket.getModelo()
                            + "\nPlaca: " + novoTicket.getPlaca(),
                    "Sucesso no Cadastro",
                    JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            atualizarRelatorio("Entrada do veículo " + novoTicket.getPlaca() + " cadastrada com sucesso!");

        } catch (VagaNaoDisponivelException ex) {
            // REQUISITO POO_08: Exibição da mensagem da Checked Exception via JOptionPane
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Erro: Vaga Não Disponível",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocorreu um erro inesperado ao cadastrar: " + ex.getMessage(),
                    "Erro Geral",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Operação 2: Pesquisar veículo por Número de Ticket ou por Placa.
     * Trata NumberFormatException ao tentar converter o número do ticket.
     */
    private void acaoPesquisar() {
        String numTexto = txtNumero.getText().trim();
        String placaTexto = txtPlaca.getText().trim();

        if (numTexto.isEmpty() && placaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o Número do Ticket ou a Placa para pesquisar.",
                    "Pesquisa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketEstacionamento ticketEncontrado = null;

        // Se informou número, tenta buscar por ID
        if (!numTexto.isEmpty()) {
            try {
                // REQUISITO POO_08: Tratamento explícito de NumberFormatException
                int numeroBusca = Integer.parseInt(numTexto);
                for (TicketEstacionamento t : listaTickets) {
                    if (t.getNumero() == numeroBusca) {
                        ticketEncontrado = t;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "O número do ticket informado ('" + numTexto + "') é inválido! Digite apenas números inteiros.",
                        "Formato Numérico Inválido",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Se não achou por número e informou placa, busca pela placa
        if (ticketEncontrado == null && !placaTexto.isEmpty()) {
            for (TicketEstacionamento t : listaTickets) {
                if (t.getPlaca().equalsIgnoreCase(placaTexto)) {
                    ticketEncontrado = t;
                    break;
                }
            }
        }

        if (ticketEncontrado != null) {
            // Carrega os dados na tela
            txtNumero.setText(String.valueOf(ticketEncontrado.getNumero()));
            txtPlaca.setText(ticketEncontrado.getPlaca());
            txtModelo.setText(ticketEncontrado.getModelo());
            cbTipoVeiculo.setSelectedItem(ticketEncontrado.getTipoVeiculo());

            JOptionPane.showMessageDialog(this,
                    "Ticket localizado:\n" + ticketEncontrado,
                    "Veículo Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);

            atualizarRelatorio("Pesquisa concluída: Ticket #" + ticketEncontrado.getNumero() + " localizado.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nenhum ticket encontrado com os dados informados.",
                    "Não Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Operação 3: Processar Operação (Cálculo e Baixa de Estadia / Liberação de Vaga).
     * Aplica o método polimórfico calcularPrecoEstadia() da interface Tarifavel.
     */
    private void acaoProcessarOperacao() {
        String numTexto = txtNumero.getText().trim();
        String placaTexto = txtPlaca.getText().trim();

        if (numTexto.isEmpty() && placaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Para processar a baixa, informe o Número do Ticket ou a Placa.",
                    "Identificação Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketEstacionamento ticketAlvo = null;

        // Localiza por número ou por placa
        if (!numTexto.isEmpty()) {
            try {
                int num = Integer.parseInt(numTexto);
                for (TicketEstacionamento t : listaTickets) {
                    if (t.getNumero() == num) {
                        ticketAlvo = t;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Número do ticket inválido: " + numTexto,
                        "Erro de Formato",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (ticketAlvo == null && !placaTexto.isEmpty()) {
            for (TicketEstacionamento t : listaTickets) {
                if (t.getPlaca().equalsIgnoreCase(placaTexto) && !t.isFinalizado()) {
                    ticketAlvo = t;
                    break;
                }
            }
        }

        if (ticketAlvo == null) {
            JOptionPane.showMessageDialog(this,
                    "Ticket não encontrado para processamento da baixa.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ticketAlvo.isFinalizado()) {
            JOptionPane.showMessageDialog(this,
                    "Este ticket já foi finalizado anteriormente!\nValor pago: "
                            + ticketAlvo.formatarValor(ticketAlvo.getValorPago()),
                    "Ticket Já Baixado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Determinação dos minutos de permanência
        long minutos;
        String minutosSimuladosStr = txtMinutosSimulados.getText().trim();

        if (!minutosSimuladosStr.isEmpty()) {
            try {
                // Tratamento de conversão numérica
                minutos = Long.parseLong(minutosSimuladosStr);
                if (minutos < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Os minutos de permanência não podem ser negativos.",
                            "Valor Inválido",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Minutos de simulação inválidos: '" + minutosSimuladosStr + "'. Digite um valor numérico inteiro.",
                        "Erro de Conversão",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Tempo real decorrido desde a entrada
            Duration duracao = Duration.between(ticketAlvo.getHoraEntrada(), LocalDateTime.now());
            minutos = Math.max(1, duracao.toMinutes());
        }

        // REQUISITO POO_10: POLIMORFISMO POR INTERFACE
        // Chama calcularPrecoEstadia definido na interface Tarifavel e implementado em TicketCarro / TicketMoto
        double valorCalculado = ticketAlvo.calcularPrecoEstadia(minutos);
        String valorFormatado = ticketAlvo.formatarValor(valorCalculado);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "DEMONSTRATIVO DE COBRANÇA:\n"
                        + "Ticket: #" + ticketAlvo.getNumero() + " (" + ticketAlvo.getTipoVeiculo() + ")\n"
                        + "Placa: " + ticketAlvo.getPlaca() + " - Modelo: " + ticketAlvo.getModelo() + "\n"
                        + "Tempo Total de Permanência: " + minutos + " minuto(s)\n"
                        + "Valor Total da Estadia: " + valorFormatado + "\n\n"
                        + "Confirmar recebimento do pagamento e liberar vaga no pátio?",
                "Processar Baixa e Pagamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            ticketAlvo.setHoraSaida(ticketAlvo.getHoraEntrada().plusMinutes(minutos));
            ticketAlvo.setValorPago(valorCalculado);
            ticketAlvo.setFinalizado(true);

            JOptionPane.showMessageDialog(this,
                    "Operação concluída com sucesso!\nVaga liberada no pátio. Recibo emitido: " + valorFormatado,
                    "Estadia Encerrada",
                    JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            atualizarRelatorio("Baixa processada no Ticket #" + ticketAlvo.getNumero()
                    + " (" + ticketAlvo.getPlaca() + ") - " + valorFormatado);
        }
    }

    /**
     * Operação 4: Ordenar registros em memória.
     * REQUISITO POO_09: Utiliza Collections.sort() graças à implementação
     * de Comparable<TicketEstacionamento> ordenando por horário de entrada.
     */
    private void acaoOrdenar() {
        if (listaTickets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "A lista em memória está vazia. Não há itens para ordenar.",
                    "Ordenação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ordenação natural baseada no compareTo()
        Collections.sort(listaTickets);

        JOptionPane.showMessageDialog(this,
                "Lista reordenada com sucesso pelo Horário de Entrada utilizando Collections.sort()!",
                "Ordenação Concluída",
                JOptionPane.INFORMATION_MESSAGE);

        atualizarRelatorio("Lista ordenada por Horário de Entrada (Comparable<T>).");
    }

    /**
     * Operação 5: Sair do sistema com diálogo de confirmação.
     */
    private void acaoSair() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do Sistema de Estacionamento?\n(Os dados mantidos em memória RAM serão descartados)",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            this.dispose();
            System.exit(0);
        }
    }

    // =========================================================================
    // IMPLEMENTAÇÃO DE ACTIONLISTENER (EVENTOS DE BOTÃO)
    // =========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object origem = e.getSource();

        if (origem == btnCadastrar) {
            acaoCadastrar();
        } else if (origem == btnPesquisar) {
            acaoPesquisar();
        } else if (origem == btnProcessarOperacao) {
            acaoProcessarOperacao();
        } else if (origem == btnOrdenar) {
            acaoOrdenar();
        } else if (origem == btnLimpar) {
            limparCampos();
        } else if (origem == btnSair) {
            acaoSair();
        }
    }

    // =========================================================================
    // IMPLEMENTAÇÃO DE WINDOWLISTENER (EVENTOS DE JANELA)
    // =========================================================================
    @Override
    public void windowClosing(WindowEvent e) {
        // Fechamento de janela tratado com confirmação
        acaoSair();
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
