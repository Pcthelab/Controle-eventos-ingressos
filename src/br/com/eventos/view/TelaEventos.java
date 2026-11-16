package br.com.eventos.view;

import br.com.eventos.exception.IngressoInvalidoException;
import br.com.eventos.interfaces.Validavel;
import br.com.eventos.model.Ingresso;
import br.com.eventos.model.IngressoInteira;
import br.com.eventos.model.IngressoMeia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface Gráfica desenvolvida em javax.swing.* (Requisitos POO_07 a POO_10).
 * Gerencia a venda, validação em catraca, cancelamento e relatórios de público
 * mantendo todos os dados 100% em memória RAM (List<Ingresso>).
 * Implementa ActionListener e WindowListener diretamente, conforme padronização das aulas.
 */
public class TelaEventos extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    // Cores e Estilos Visuais
    private static final Color FUNDO = new Color(245, 247, 250);
    private static final Color AZUL_PRIMARIO = new Color(35, 89, 145);
    private static final Color TEXTO_ESCURO = new Color(37, 48, 61);

    // Persistência 100% em memória RAM (Requisito POO_07)
    private final List<Ingresso> listaIngressos = new ArrayList<>();
    private int contadorSequencialCodigo = 1;

    // Componentes de Formulário
    private JTextField txtCodigo;
    private JTextField txtEvento;
    private JTextField txtParticipante;
    private JTextField txtValorInteira;
    private JComboBox<String> cbTipo;
    private JTextField txtDocumentoMeia;
    private JLabel lblDocumentoMeia;

    // Botões de Ação Exigidos no PDF (Pág. 3)
    private JButton btnCadastrar;
    private JButton btnPesquisar;
    private JButton btnProcessarOperacao;
    private JButton btnCancelar;
    private JButton btnOrdenar;
    private JButton btnRelatorioPublico;
    private JButton btnLimpar;
    private JButton btnSair;

    // Visualização dos Dados (JTable + JTextArea para Relatórios)
    private JTabbedPane abasExibicao;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextArea txtRelatorioPublico;
    private JLabel lblResumoBarra;

    public TelaEventos() {
        super("Sistema de Controle de Eventos e Venda de Ingressos - P2 POO (Prof. Maromo)");
        configurarJanela();
        montarComponentes();
        gerarDadosIniciaisDemonstracao();
        atualizarVisualizacoes();
    }

    private void configurarJanela() {
        this.setSize(1080, 740);
        this.setMinimumSize(new Dimension(900, 640));
        this.setLocationRelativeTo(null);
        // Interceptado pelo WindowListener
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
    }

    private void montarComponentes() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout(0, 12));
        ((JPanel) container).setBackground(FUNDO);
        ((JPanel) container).setBorder(new EmptyBorder(16, 20, 16, 20));

        // 1. Cabeçalho Superior
        JPanel painelCabecalho = new JPanel(new BorderLayout(8, 4));
        painelCabecalho.setBackground(AZUL_PRIMARIO);
        painelCabecalho.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitulo = new JLabel("CONTROLE DE EVENTOS & BILHETERIA DIGITAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Tema 08 - Venda de Bilhetes, Portaria com Catraca, Cancelamento e Relatórios em RAM");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(210, 230, 255));

        painelCabecalho.add(lblTitulo, BorderLayout.NORTH);
        painelCabecalho.add(lblSubtitulo, BorderLayout.SOUTH);
        container.add(painelCabecalho, BorderLayout.NORTH);

        // 2. Painel Central: Formulários e Ações
        JPanel painelCentral = new JPanel(new BorderLayout(0, 10));
        painelCentral.setOpaque(false);

        // 2.1 Formulário de Dados
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                new EmptyBorder(12, 16, 12, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Linha 0: Título do bloco
        c.gridx = 0; c.gridy = 0; c.gridwidth = 4;
        JLabel lblSecaoVenda = new JLabel("Formulário de Cadastro e Operações:");
        lblSecaoVenda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSecaoVenda.setForeground(TEXTO_ESCURO);
        painelFormulario.add(lblSecaoVenda, c);
        c.gridwidth = 1;

        // Linha 1: Evento e Participante
        c.gridy = 1;
        adicionarCampoGrid(painelFormulario, c, "Nome do Evento:", txtEvento = new JTextField(18), 0);
        adicionarCampoGrid(painelFormulario, c, "Nome do Participante:", txtParticipante = new JTextField(18), 2);

        // Linha 2: Valor Inteira e Tipo de Ingresso
        c.gridy = 2;
        adicionarCampoGrid(painelFormulario, c, "Valor Base Inteira (R$):", txtValorInteira = new JTextField(12), 0);
        cbTipo = new JComboBox<>(new String[]{"Inteira", "Meia-entrada (50%)"});
        cbTipo.addActionListener(e -> {
            boolean ehMeia = cbTipo.getSelectedIndex() == 1;
            txtDocumentoMeia.setEnabled(ehMeia);
            lblDocumentoMeia.setEnabled(ehMeia);
        });
        adicionarCampoGrid(painelFormulario, c, "Tipo de Ingresso:", cbTipo, 2);

        // Linha 3: Documento Comprovante (específico para Meia) e Código para busca/baixa
        c.gridy = 3;
        lblDocumentoMeia = new JLabel("Comprovante Meia-entrada:");
        lblDocumentoMeia.setEnabled(false);
        txtDocumentoMeia = new JTextField("Carteirinha de Estudante / DNE", 18);
        txtDocumentoMeia.setEnabled(false);
        adicionarCampoCustomizado(painelFormulario, c, lblDocumentoMeia, txtDocumentoMeia, 0);

        adicionarCampoGrid(painelFormulario, c, "Código do Bilhete (Operações):", txtCodigo = new JTextField(10), 2);

        painelCentral.add(painelFormulario, BorderLayout.NORTH);

        // 2.2 Barra de Botões com as ações padronizadas pelo PDF
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        painelBotoes.setOpaque(false);

        btnCadastrar = criarBotao("Cadastrar (Venda)", new Color(35, 89, 145), Color.WHITE);
        btnPesquisar = criarBotao("Pesquisar", new Color(230, 238, 248), TEXTO_ESCURO);
        btnProcessarOperacao = criarBotao("Processar Operação (Catraca)", new Color(222, 243, 230), new Color(28, 107, 60));
        btnCancelar = criarBotao("Cancelar Ingresso", new Color(252, 233, 231), new Color(175, 45, 36));
        btnOrdenar = criarBotao("Ordenar (Código)", new Color(255, 248, 220), new Color(130, 95, 20));
        btnRelatorioPublico = criarBotao("Relatório de Público", new Color(235, 230, 250), new Color(85, 45, 140));
        btnLimpar = criarBotao("Limpar", new Color(240, 240, 240), TEXTO_ESCURO);
        btnSair = criarBotao("Sair", new Color(250, 220, 220), new Color(150, 30, 30));

        painelCentral.add(painelBotoes, BorderLayout.SOUTH);
        container.add(painelCentral, BorderLayout.CENTER);

        // 3. Painel Inferior com Abas: Tabela de Dados e JTextArea de Relatório
        abasExibicao = new JTabbedPane();
        abasExibicao.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Aba 1: Tabela Dinâmica
        modeloTabela = new DefaultTableModel(new Object[]{"Código", "Evento", "Participante", "Tipo", "Valor (R$)", "Status", "Data/Hora Acesso"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : String.class; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setSelectionBackground(new Color(218, 234, 252));
        tabela.setSelectionForeground(TEXTO_ESCURO);

        int[] larguras = {70, 180, 180, 120, 110, 160, 160};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        // Ao clicar em uma linha da tabela, preenche o campo de código automaticamente
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) {
                    txtCodigo.setText(String.valueOf(tabela.getValueAt(linha, 0)));
                }
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tabela);
        abasExibicao.addTab("📋 Ingressos Registrados (Tabela)", scrollTabela);

        // Aba 2: JTextArea exigida para Relatórios
        txtRelatorioPublico = new JTextArea(14, 80);
        txtRelatorioPublico.setEditable(false);
        txtRelatorioPublico.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRelatorioPublico.setBackground(new Color(253, 254, 255));
        JScrollPane scrollRelatorio = new JScrollPane(txtRelatorioPublico);
        abasExibicao.addTab("📊 Relatório de Público & Extrato (JTextArea)", scrollRelatorio);

        JPanel painelSul = new JPanel(new BorderLayout(5, 5));
        painelSul.setOpaque(false);
        painelSul.add(abasExibicao, BorderLayout.CENTER);

        lblResumoBarra = new JLabel();
        lblResumoBarra.setBorder(new EmptyBorder(6, 4, 2, 4));
        lblResumoBarra.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblResumoBarra.setForeground(TEXTO_ESCURO);
        painelSul.add(lblResumoBarra, BorderLayout.SOUTH);

        container.add(painelSul, BorderLayout.SOUTH);
    }

    private void adicionarCampoGrid(JPanel painel, GridBagConstraints c, String rotulo, JComponent campo, int coluna) {
        c.gridx = coluna;
        c.weightx = 0;
        JLabel label = new JLabel(rotulo);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(70, 80, 95));
        painel.add(label, c);

        c.gridx = coluna + 1;
        c.weightx = 1;
        campo.setPreferredSize(new Dimension(170, 28));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        painel.add(campo, c);
    }

    private void adicionarCampoCustomizado(JPanel painel, GridBagConstraints c, JLabel label, JComponent campo, int coluna) {
        c.gridx = coluna;
        c.weightx = 0;
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(70, 80, 95));
        painel.add(label, c);

        c.gridx = coluna + 1;
        c.weightx = 1;
        campo.setPreferredSize(new Dimension(170, 28));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        painel.add(campo, c);
    }

    private JButton criarBotao(String texto, Color fundo, Color frente) {
        JButton btn = new JButton(texto);
        btn.setBackground(fundo);
        btn.setForeground(frente);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fundo.darker(), 1),
                new EmptyBorder(7, 12, 7, 12)));
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Gera dados iniciais para facilitar a demonstração imediata
     * ao professor e teste de ordenação e catraca.
     */
    private void gerarDadosIniciaisDemonstracao() {
        Ingresso i1 = new IngressoInteira(contadorSequencialCodigo++, "Festival de Rock FATEC", "Carlos Eduardo", 160.00);
        IngressoMeia i2 = new IngressoMeia(contadorSequencialCodigo++, "Festival de Rock FATEC", "Mariana Silva", 80.00, "Carteira Estudantil Fatec #4821");
        Ingresso i3 = new IngressoInteira(contadorSequencialCodigo++, "Festival de Rock FATEC", "Lucas Ferreira", 160.00);

        try {
            // Simula um acesso já realizado para enriquecer o extrato
            i2.validarAcessoCatraca();
            // Simula um cancelamento
            i3.cancelar();
        } catch (IngressoInvalidoException ignored) {}

        listaIngressos.add(i1);
        listaIngressos.add(i2);
        listaIngressos.add(i3);
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO E AÇÕES DOS BOTÕES (POO_07 A POO_10)
    // =========================================================================

    /**
     * Operação 1: Cadastrar (Venda de Bilhetes).
     * Trata validações e NumberFormatException ao converter valores.
     */
    private void acaoCadastrar() {
        String evento = txtEvento.getText().trim();
        String participante = txtParticipante.getText().trim();
        String valorStr = txtValorInteira.getText().trim();

        if (evento.isEmpty() || participante.isEmpty() || valorStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos obrigatórios: Evento, Participante e Valor da Inteira.",
                    "Dados Obrigatórios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final double valorInteira;
        try {
            // Requisito POO_08: Tratamento criterioso de NumberFormatException
            valorInteira = converterValorMoeda(valorStr);
            if (valorInteira <= 0) {
                throw new NumberFormatException("Valor deve ser positivo");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Valor da inteira inválido: '" + valorStr + "'.\nDigite um número positivo (ex: 120,00 ou 150.50).",
                    "Erro de Conversão Numérica",
                    JOptionPane.ERROR_MESSAGE);
            txtValorInteira.requestFocusInWindow();
            return;
        }

        Ingresso novoIngresso;
        int codigo = contadorSequencialCodigo++;
        boolean ehMeia = cbTipo.getSelectedIndex() == 1;

        if (ehMeia) {
            // Polimorfismo e chamada do método estático de interface
            double valorMeia = Validavel.calcularValorMeia(valorInteira);
            String doc = txtDocumentoMeia.getText().trim();
            novoIngresso = new IngressoMeia(codigo, evento, participante, valorMeia, doc);
        } else {
            novoIngresso = new IngressoInteira(codigo, evento, participante, valorInteira);
        }

        // Armazenamento 100% em memória RAM
        listaIngressos.add(novoIngresso);

        JOptionPane.showMessageDialog(this,
                "Venda registrada com sucesso!\n"
                        + "Código do Bilhete: #" + novoIngresso.getCodigo() + "\n"
                        + "Tipo: " + novoIngresso.getTipo() + "\n"
                        + "Valor: " + novoIngresso.formatarValor(novoIngresso.getValor()) + "\n"
                        + "Participante: " + novoIngresso.getNomeParticipante(),
                "Venda Concluída",
                JOptionPane.INFORMATION_MESSAGE);

        limparCamposVenda();
        atualizarVisualizacoes();
        selecionarIngressoNaTabela(novoIngresso);
    }

    /**
     * Operação 2: Pesquisar ingresso por Código ou por Nome do Participante.
     * Trata NumberFormatException ao converter o código.
     */
    private void acaoPesquisar() {
        String codTexto = txtCodigo.getText().trim();
        String participanteTexto = txtParticipante.getText().trim();

        if (codTexto.isEmpty() && participanteTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o Código do Ingresso ou o Nome do Participante para pesquisar.",
                    "Pesquisar",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ingresso encontrado = null;

        if (!codTexto.isEmpty()) {
            try {
                // Requisito POO_08: Tratamento explícito de NumberFormatException
                int cod = Integer.parseInt(codTexto);
                for (Ingresso ing : listaIngressos) {
                    if (ing.getCodigo() == cod) {
                        encontrado = ing;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Código inválido: '" + codTexto + "'. Informe um número inteiro.",
                        "Erro Numérico",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (encontrado == null && !participanteTexto.isEmpty()) {
            for (Ingresso ing : listaIngressos) {
                if (ing.getNomeParticipante().toLowerCase().contains(participanteTexto.toLowerCase())) {
                    encontrado = ing;
                    break;
                }
            }
        }

        if (encontrado != null) {
            preencherFormularioComIngresso(encontrado);
            selecionarIngressoNaTabela(encontrado);
            JOptionPane.showMessageDialog(this,
                    "Ingresso Localizado:\n" + encontrado,
                    "Sucesso na Pesquisa",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nenhum ingresso encontrado com os critérios informados.",
                    "Não Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Operação 3: Processar Operação (Conferência na Portaria e Baixa de Acesso via Catraca).
     * Aplica o método polimórfico validarAcessoCatraca() da interface Validavel
     * e captura a Checked Exception IngressoInvalidoException!
     */
    private void acaoProcessarOperacao() {
        Ingresso ingresso = obterIngressoPorCodigoOuSelecao();
        if (ingresso == null) return;

        // Exibe orientação da portaria (método polimórfico)
        String instrucao = ingresso.obterInstrucaoCatraca();

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "CONFERÊNCIA DE PORTARIA / CATRACA:\n\n"
                        + "Bilhete: #" + ingresso.getCodigo() + " (" + ingresso.getTipo() + ")\n"
                        + "Participante: " + ingresso.getNomeParticipante() + "\n"
                        + "Evento: " + ingresso.getEvento() + "\n"
                        + "Orientação: " + instrucao + "\n\n"
                        + "Confirmar liberação da catraca e dar baixa de acesso?",
                "Processar Operação - Catraca",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) return;

        try {
            // REQUISITO POO_10 & POO_08: Chamada de método polimórfico com Checked Exception
            ingresso.validarAcessoCatraca();

            JOptionPane.showMessageDialog(this,
                    "ACESSO LIBERADO COM SUCESSO!\n"
                            + "Catraca destravada para: " + ingresso.getNomeParticipante() + "\n"
                            + "Bilhete #" + ingresso.getCodigo() + " registrado como UTILIZADO.",
                    "Catraca Liberada",
                    JOptionPane.INFORMATION_MESSAGE);

            atualizarVisualizacoes();
            selecionarIngressoNaTabela(ingresso);

        } catch (IngressoInvalidoException ex) {
            // Mensagem de erro devolvida visualmente via JOptionPane (Requisito 5)
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Acesso Recusado na Portaria",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Operação 4: Cancelamento de Ingresso.
     */
    private void acaoCancelar() {
        Ingresso ingresso = obterIngressoPorCodigoOuSelecao();
        if (ingresso == null) return;

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar o ingresso #" + ingresso.getCodigo()
                        + " de " + ingresso.getNomeParticipante() + "?",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        try {
            // Dispara Checked Exception caso já esteja utilizado ou cancelado
            ingresso.cancelar();

            JOptionPane.showMessageDialog(this,
                    "Ingresso #" + ingresso.getCodigo() + " cancelado com sucesso!",
                    "Cancelamento Concluído",
                    JOptionPane.INFORMATION_MESSAGE);

            atualizarVisualizacoes();
            selecionarIngressoNaTabela(ingresso);

        } catch (IngressoInvalidoException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Falha no Cancelamento",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Operação 5: Ordenar a lista em memória com Collections.sort().
     * Requisito POO_09: Utiliza a interface Comparable<Ingresso> (código sequencial).
     */
    private void acaoOrdenar() {
        if (listaIngressos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "A lista em memória está vazia. Não há bilhetes para ordenar.",
                    "Aviso de Ordenação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ordenação natural baseada no compareTo()
        Collections.sort(listaIngressos);

        atualizarVisualizacoes();

        JOptionPane.showMessageDialog(this,
                "Coleção de ingressos ordenada com sucesso por Código Sequencial utilizando Collections.sort()!",
                "Ordenação Concluída",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Operação 6: Gerar Relatório de Público detalhado na JTextArea.
     * Atende ao requisito do Tema 08: Relatório de público.
     */
    private void acaoGerarRelatorioPublico() {
        atualizarRelatorioPublico();
        abasExibicao.setSelectedIndex(1); // Alterna para a aba do JTextArea
        JOptionPane.showMessageDialog(this,
                "Relatório de Público gerado com sucesso no painel de texto!",
                "Relatório Emitido",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Operação 7: Sair com diálogo de confirmação.
     */
    private void acaoSair() {
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente encerrar a aplicação?\n(Atenção: todos os dados estão mantidos 100% em memória RAM e serão descartados)",
                "Confirmar Encerramento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            this.dispose();
            System.exit(0);
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE SUPORTE
    // =========================================================================

    private Ingresso obterIngressoPorCodigoOuSelecao() {
        String codTexto = txtCodigo.getText().trim();
        if (codTexto.isEmpty()) {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                codTexto = String.valueOf(tabela.getValueAt(linha, 0));
                txtCodigo.setText(codTexto);
            }
        }

        if (codTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma linha na tabela ou digite o Código do Bilhete para esta operação.",
                    "Identificação Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            int cod = Integer.parseInt(codTexto);
            for (Ingresso ing : listaIngressos) {
                if (ing.getCodigo() == cod) {
                    return ing;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Ingresso #" + cod + " não foi localizado na coleção em memória.",
                    "Não Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Código inválido: '" + codTexto + "'. Digite apenas números inteiros.",
                    "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private double converterValorMoeda(String texto) {
        String limpo = texto.trim().replace("R$", "").replace(" ", "");
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(',', '.');
        }
        if (limpo.isEmpty()) throw new NumberFormatException();
        return Double.parseDouble(limpo);
    }

    private void atualizarVisualizacoes() {
        // Atualiza a tabela
        modeloTabela.setRowCount(0);

        int totalInteiras = 0;
        int totalMeias = 0;
        int cancelados = 0;
        int publicoPresente = 0;
        double receitaTotal = 0.0;

        for (Ingresso ing : listaIngressos) {
            if (ing instanceof IngressoInteira) totalInteiras++;
            if (ing instanceof IngressoMeia) totalMeias++;
            if (ing.isCancelado()) {
                cancelados++;
            } else {
                receitaTotal += ing.getValor();
                if (ing.isUtilizado()) {
                    publicoPresente++;
                }
            }

            String horaAcesso = (ing.getDataHoraAcesso() != null)
                    ? ing.getDataHoraAcesso().format(Ingresso.FORMATADOR_DATA_HORA)
                    : "-";

            modeloTabela.addRow(new Object[]{
                    ing.getCodigo(),
                    ing.getEvento(),
                    ing.getNomeParticipante(),
                    ing.getTipo(),
                    ing.formatarValor(ing.getValor()),
                    ing.getStatus(),
                    horaAcesso
            });
        }

        lblResumoBarra.setText(String.format("Total de Vendas: %d  |  Inteiras: %d  |  Meias: %d  |  Cancelados: %d  |  Público Presente (Catraca): %d  |  Receita Ativa: R$ %.2f",
                listaIngressos.size(), totalInteiras, totalMeias, cancelados, publicoPresente, receitaTotal));

        atualizarRelatorioPublico();
    }

    /**
     * Gera o relatório consolidado na JTextArea (Requisito POO_07 / Pág. 3).
     */
    private void atualizarRelatorioPublico() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================================================\n");
        sb.append("                          RELATÓRIO CONSOLIDADO DE PÚBLICO E BILHETERIA                             \n");
        sb.append("====================================================================================================\n\n");

        int totalIngressos = listaIngressos.size();
        int inteiras = 0, meias = 0, cancelados = 0, acessos = 0;
        double receitaInteira = 0, receitaMeia = 0;

        for (Ingresso ing : listaIngressos) {
            if (ing.isCancelado()) {
                cancelados++;
                continue;
            }
            if (ing instanceof IngressoInteira) {
                inteiras++;
                receitaInteira += ing.getValor();
            } else if (ing instanceof IngressoMeia) {
                meias++;
                receitaMeia += ing.getValor();
            }
            if (ing.isUtilizado()) {
                acessos++;
            }
        }

        double receitaTotal = receitaInteira + receitaMeia;
        double taxaPresenca = (totalIngressos - cancelados > 0)
                ? ((double) acessos / (totalIngressos - cancelados)) * 100.0 : 0.0;

        sb.append(String.format("• Ingressos Emitidos no Total:  %d bilhete(s)\n", totalIngressos));
        sb.append(String.format("• Ingressos Ativos / Válidos:   %d bilhete(s)\n", (totalIngressos - cancelados)));
        sb.append(String.format("• Ingressos Cancelados:         %d bilhete(s)\n", cancelados));
        sb.append(String.format("• Público Presente (Catraca):   %d pessoa(s)\n", acessos));
        sb.append(String.format("• Taxa de Presença / Conversão: %.1f %%\n", taxaPresenca));
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("• Receita com Inteiras:         R$ %.2f (%d ingressos)\n", receitaInteira, inteiras));
        sb.append(String.format("• Receita com Meias:            R$ %.2f (%d ingressos)\n", receitaMeia, meias));
        sb.append(String.format("• RECEITA TOTAL ARRECADADA:     R$ %.2f\n", receitaTotal));
        sb.append("====================================================================================================\n\n");

        sb.append("DISCRIMINAÇÃO DETALHADA DOS BILHETES EM MEMÓRIA (Comparable<T>):\n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        if (listaIngressos.isEmpty()) {
            sb.append("Nenhum ingresso registrado na sessão.\n");
        } else {
            for (Ingresso ing : listaIngressos) {
                sb.append(ing.toString()).append("\n");
            }
        }
        sb.append("----------------------------------------------------------------------------------------------------\n");

        txtRelatorioPublico.setText(sb.toString());
        txtRelatorioPublico.setCaretPosition(0);
    }

    private void preencherFormularioComIngresso(Ingresso ing) {
        txtCodigo.setText(String.valueOf(ing.getCodigo()));
        txtEvento.setText(ing.getEvento());
        txtParticipante.setText(ing.getNomeParticipante());
        txtValorInteira.setText(String.format("%.2f", ing.getValor()));

        if (ing instanceof IngressoMeia) {
            cbTipo.setSelectedIndex(1);
            txtDocumentoMeia.setText(((IngressoMeia) ing).getDocumentoComprovante());
        } else {
            cbTipo.setSelectedIndex(0);
        }
    }

    private void selecionarIngressoNaTabela(Ingresso ing) {
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            if (((Integer) modeloTabela.getValueAt(i, 0)) == ing.getCodigo()) {
                tabela.setRowSelectionInterval(i, i);
                tabela.scrollRectToVisible(tabela.getCellRect(i, 0, true));
                break;
            }
        }
        txtCodigo.setText(String.valueOf(ing.getCodigo()));
    }

    private void limparCampos() {
        limparCamposVenda();
        txtCodigo.setText("");
        tabela.clearSelection();
    }

    private void limparCamposVenda() {
        txtEvento.setText("");
        txtParticipante.setText("");
        txtValorInteira.setText("");
        cbTipo.setSelectedIndex(0);
        txtDocumentoMeia.setText("Carteirinha de Estudante / DNE");
        txtEvento.requestFocusInWindow();
    }

    // =========================================================================
    // IMPLEMENTAÇÃO DE ACTIONLISTENER (EVENTOS DE BOTÃO)
    // =========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnCadastrar) {
            acaoCadastrar();
        } else if (src == btnPesquisar) {
            acaoPesquisar();
        } else if (src == btnProcessarOperacao) {
            acaoProcessarOperacao();
        } else if (src == btnCancelar) {
            acaoCancelar();
        } else if (src == btnOrdenar) {
            acaoOrdenar();
        } else if (src == btnRelatorioPublico) {
            acaoGerarRelatorioPublico();
        } else if (src == btnLimpar) {
            limparCampos();
        } else if (src == btnSair) {
            acaoSair();
        }
    }

    // =========================================================================
    // IMPLEMENTAÇÃO DE WINDOWLISTENER (EVENTOS DE FECHAMENTO DE JANELA)
    // =========================================================================
    @Override
    public void windowClosing(WindowEvent e) {
        acaoSair();
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}
