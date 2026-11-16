package br.com.eventos.view;

import br.com.eventos.exception.IngressoInvalidoException;
import br.com.eventos.interfaces.Validavel;
import br.com.eventos.model.Ingresso;
import br.com.eventos.model.IngressoInteira;
import br.com.eventos.model.IngressoMeia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface Gráfica Moderna e Funcional em Java Swing (javax.swing.*).
 * Desenvolvida para a Avaliação P2 de POO (Prof. Maromo - FATEC Mogi Mirim).
 * Atende com rigor a todas as regras da disciplina:
 * - 100% em memória RAM (List<Ingresso>)
 * - Implementa ActionListener e WindowListener diretamente
 * - Botões padronizados: Cadastrar, Pesquisar, Processar Operação, Ordenar, Sair
 * - JTextArea para relatórios e JOptionPane para mensagens de erro/alerta
 * - Visual moderno, limpo e profissional com cards, badges e tipografia Segoe UI.
 */
public class TelaEventos extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    // Paleta de Cores Moderna (Estilo Tailwind / Clean Slate)
    private static final Color COR_FUNDO = new Color(248, 250, 252);        // Slate 50
    private static final Color COR_HEADER = new Color(15, 23, 42);          // Slate 900
    private static final Color COR_PRIMARIA = new Color(37, 99, 235);       // Blue 600
    private static final Color COR_SUCESSO = new Color(16, 185, 129);       // Emerald 500
    private static final Color COR_PERIGO = new Color(239, 68, 68);         // Rose 500
    private static final Color COR_AMBAR = new Color(245, 158, 11);         // Amber 500
    private static final Color COR_TEXTO = new Color(15, 23, 42);           // Slate 900
    private static final Color COR_MUTED = new Color(100, 116, 139);        // Slate 500
    private static final Color COR_BORDA = new Color(226, 232, 240);        // Slate 200

    // Persistência 100% em memória RAM (Requisito da P2)
    private final List<Ingresso> listaIngressos = new ArrayList<>();
    private int contadorSequencial = 1;

    // Componentes de Entrada
    private JTextField txtEvento;
    private JTextField txtParticipante;
    private JTextField txtValorInteira;
    private JComboBox<String> cbTipo;
    private JTextField txtDocumentoMeia;
    private JLabel lblDocumentoMeia;
    private JTextField txtCodigoOperacao;

    // Botões de Ação Obrigatórios pelo PDF
    private JButton btnCadastrar;
    private JButton btnPesquisar;
    private JButton btnProcessarOperacao;
    private JButton btnCancelar;
    private JButton btnOrdenar;
    private JButton btnRelatorio;
    private JButton btnLimpar;
    private JButton btnSair;

    // Componentes de Visualização
    private JTabbedPane abasVisualizacao;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextArea txtRelatorioPublico;

    // Cards de Métricas em Tempo Real (KPIs)
    private CardKPI kpiTotal;
    private CardKPI kpiPresentes;
    private CardKPI kpiCancelados;
    private CardKPI kpiReceita;

    public TelaEventos() {
        super("Bilheteria & Controle de Eventos - Sistema Orientado a Objetos (POO)");
        configurarJanela();
        construirInterface();
        inicializarDadosDemonstracao();
        atualizarTodaInterface();
    }

    private void configurarJanela() {
        setSize(1200, 800);
        setMinimumSize(new Dimension(1040, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(0, 14));
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(new EmptyBorder(16, 18, 16, 18));
        setContentPane(painelPrincipal);

        // 1. Barra de Topo Moderna
        painelPrincipal.add(criarBarraCabecalho(), BorderLayout.NORTH);

        // 2. Área Central Dividida (Esquerda: Formulários / Direita: KPIs + Tabela / Relatório)
        JPanel painelCentral = new JPanel(new BorderLayout(14, 0));
        painelCentral.setOpaque(false);

        painelCentral.add(criarColunaEsquerdaFormularios(), BorderLayout.WEST);
        painelCentral.add(criarColunaDireitaVisualizacoes(), BorderLayout.CENTER);

        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
    }

    /**
     * Cabeçalho moderno com visual escuro elegante, título e status do sistema.
     */
    private JPanel criarBarraCabecalho() {
        JPanel header = new JPanel(new BorderLayout(10, 5)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COR_HEADER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Título e Subtítulo
        JPanel painelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        painelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("CONTROLE DE EVENTOS & BILHETERIA DIGITAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Tema 08 • Venda de Ingressos, Conferência na Catraca, Cancelamento e Relatórios em RAM");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(148, 163, 184)); // Slate 400

        painelTitulos.add(lblTitulo);
        painelTitulos.add(lblSubtitulo);
        header.add(painelTitulos, BorderLayout.WEST);

        // Badge lateral de status
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        painelStatus.setOpaque(false);

        JLabel lblBadge = new JLabel("● SESSÃO ATIVA (100% RAM)");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(new Color(74, 222, 128)); // Emerald 400
        painelStatus.add(lblBadge);

        header.add(painelStatus, BorderLayout.EAST);
        return header;
    }

    /**
     * Coluna da esquerda: Cards para Venda (Cadastro) e Operações de Portaria (Baixa/Cancelamento).
     */
    private JPanel criarColunaEsquerdaFormularios() {
        JPanel coluna = new JPanel();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setOpaque(false);
        coluna.setPreferredSize(new Dimension(390, 0));

        // --- CARD 1: FORMULÁRIO DE VENDA (CADASTRAR) ---
        PainelCard cardVenda = new PainelCard();
        cardVenda.setLayout(new BoxLayout(cardVenda, BoxLayout.Y_AXIS));

        JLabel tituloVenda = new JLabel("Nova Venda de Ingresso");
        tituloVenda.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloVenda.setForeground(COR_TEXTO);
        tituloVenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardVenda.add(tituloVenda);
        cardVenda.add(Box.createVerticalStrut(10));

        cardVenda.add(criarRotuloCampo("Nome do Evento:"));
        txtEvento = criarCampoTexto();
        cardVenda.add(txtEvento);
        cardVenda.add(Box.createVerticalStrut(8));

        cardVenda.add(criarRotuloCampo("Nome do Participante:"));
        txtParticipante = criarCampoTexto();
        cardVenda.add(txtParticipante);
        cardVenda.add(Box.createVerticalStrut(8));

        JPanel linhaTipoValor = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaTipoValor.setOpaque(false);
        linhaTipoValor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel pTipo = new JPanel(new BorderLayout(0, 4));
        pTipo.setOpaque(false);
        pTipo.add(criarRotuloCampo("Tipo de Bilhete:"), BorderLayout.NORTH);
        cbTipo = new JComboBox<>(new String[]{"Inteira", "Meia-Entrada (50%)"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setPreferredSize(new Dimension(100, 32));
        cbTipo.addActionListener(e -> alternarCampoDocumentoMeia());
        pTipo.add(cbTipo, BorderLayout.CENTER);
        linhaTipoValor.add(pTipo);

        JPanel pValor = new JPanel(new BorderLayout(0, 4));
        pValor.setOpaque(false);
        pValor.add(criarRotuloCampo("Valor Inteira (R$):"), BorderLayout.NORTH);
        txtValorInteira = criarCampoTexto();
        pValor.add(txtValorInteira, BorderLayout.CENTER);
        linhaTipoValor.add(pValor);

        linhaTipoValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardVenda.add(linhaTipoValor);
        cardVenda.add(Box.createVerticalStrut(8));

        lblDocumentoMeia = criarRotuloCampo("Comprovante Meia-Entrada:");
        lblDocumentoMeia.setEnabled(false);
        cardVenda.add(lblDocumentoMeia);
        txtDocumentoMeia = criarCampoTexto();
        txtDocumentoMeia.setText("Carteirinha de Estudante / DNE");
        txtDocumentoMeia.setEnabled(false);
        cardVenda.add(txtDocumentoMeia);
        cardVenda.add(Box.createVerticalStrut(12));

        btnCadastrar = new BotaoModerno("+ Cadastrar (Registrar Venda)", COR_PRIMARIA, Color.WHITE);
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCadastrar.addActionListener(this);
        cardVenda.add(btnCadastrar);

        coluna.add(cardVenda);
        coluna.add(Box.createVerticalStrut(12));

        // --- CARD 2: OPERAÇÕES DE PORTARIA & CONSULTA ---
        PainelCard cardOperacoes = new PainelCard();
        cardOperacoes.setLayout(new BoxLayout(cardOperacoes, BoxLayout.Y_AXIS));

        JLabel tituloOperacoes = new JLabel("Operações de Portaria & Catraca");
        tituloOperacoes.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloOperacoes.setForeground(COR_TEXTO);
        tituloOperacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(tituloOperacoes);
        cardOperacoes.add(Box.createVerticalStrut(10));

        cardOperacoes.add(criarRotuloCampo("Código do Bilhete (para operações):"));

        // Painel para campo de código e botão pesquisar alinhados com altura fixa
        JPanel linhaCodigoBusca = new JPanel(new BorderLayout(8, 0));
        linhaCodigoBusca.setOpaque(false);
        linhaCodigoBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        linhaCodigoBusca.setPreferredSize(new Dimension(300, 34));

        txtCodigoOperacao = criarCampoTexto();
        txtCodigoOperacao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        linhaCodigoBusca.add(txtCodigoOperacao, BorderLayout.CENTER);

        btnPesquisar = new BotaoModerno("Pesquisar", new Color(241, 245, 249), COR_TEXTO);
        btnPesquisar.addActionListener(this);
        linhaCodigoBusca.add(btnPesquisar, BorderLayout.EAST);

        linhaCodigoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(linhaCodigoBusca);
        cardOperacoes.add(Box.createVerticalStrut(12));

        // Botões de Ação da Portaria
        btnProcessarOperacao = new BotaoModerno("Processar Operação (Catraca)", COR_SUCESSO, Color.WHITE);
        btnProcessarOperacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcessarOperacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnProcessarOperacao.addActionListener(this);
        cardOperacoes.add(btnProcessarOperacao);
        cardOperacoes.add(Box.createVerticalStrut(8));

        JPanel linhaAuxiliar = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaAuxiliar.setOpaque(false);
        linhaAuxiliar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        btnCancelar = new BotaoModerno("Cancelar", new Color(254, 226, 226), COR_PERIGO);
        btnCancelar.addActionListener(this);
        linhaAuxiliar.add(btnCancelar);

        btnLimpar = new BotaoModerno("Limpar", new Color(241, 245, 249), COR_MUTED);
        btnLimpar.addActionListener(this);
        linhaAuxiliar.add(btnLimpar);

        linhaAuxiliar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(linhaAuxiliar);

        coluna.add(cardOperacoes);
        return coluna;
    }

    /**
     * Coluna da direita: Indicadores (KPIs) no topo e Abas (Tabela + Relatório JTextArea) com barra de ações.
     */
    private JPanel criarColunaDireitaVisualizacoes() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setOpaque(false);

        // 1. Linha de KPIs (Cards de Resumo Estatístico)
        JPanel painelKPIs = new JPanel(new GridLayout(1, 4, 10, 0));
        painelKPIs.setOpaque(false);
        painelKPIs.setPreferredSize(new Dimension(0, 68));

        kpiTotal = new CardKPI("TOTAL EMITIDOS", "0", COR_PRIMARIA);
        kpiPresentes = new CardKPI("PÚBLICO CATRACA", "0", COR_SUCESSO);
        kpiCancelados = new CardKPI("CANCELADOS", "0", COR_PERIGO);
        kpiReceita = new CardKPI("RECEITA TOTAL", "R$ 0,00", new Color(124, 58, 237));

        painelKPIs.add(kpiTotal);
        painelKPIs.add(kpiPresentes);
        painelKPIs.add(kpiCancelados);
        painelKPIs.add(kpiReceita);
        painel.add(painelKPIs, BorderLayout.NORTH);

        // 2. Centro: Abas com Tabela e JTextArea
        PainelCard cardAbas = new PainelCard();
        cardAbas.setLayout(new BorderLayout(0, 8));

        // Barra de Ferramentas acima das abas com layout sem colisões
        JPanel barraSuperiorAbas = new JPanel(new BorderLayout(8, 0));
        barraSuperiorAbas.setOpaque(false);

        JLabel lblDica = new JLabel("Dica: Clique em qualquer linha para preencher o código");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(COR_MUTED);
        barraSuperiorAbas.add(lblDica, BorderLayout.WEST);

        JPanel botoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoesAcao.setOpaque(false);

        btnOrdenar = new BotaoModerno("Ordenar (Código)", new Color(254, 243, 199), new Color(180, 83, 9));
        btnOrdenar.addActionListener(this);
        botoesAcao.add(btnOrdenar);

        btnRelatorio = new BotaoModerno("Relatório de Público", new Color(243, 232, 255), new Color(126, 34, 206));
        btnRelatorio.addActionListener(this);
        botoesAcao.add(btnRelatorio);

        btnSair = new BotaoModerno("Sair", new Color(254, 226, 226), COR_PERIGO);
        btnSair.addActionListener(this);
        botoesAcao.add(btnSair);

        barraSuperiorAbas.add(botoesAcao, BorderLayout.EAST);
        cardAbas.add(barraSuperiorAbas, BorderLayout.NORTH);

        // Configuração das Abas
        abasVisualizacao = new JTabbedPane();
        abasVisualizacao.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Aba 1: Tabela Dinâmica
        modeloTabela = new DefaultTableModel(new Object[]{"Código", "Evento", "Participante", "Tipo", "Valor (R$)", "Status do Bilhete", "Acesso Catraca"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : String.class; }
        };
        tabela = new JTable(modeloTabela);
        configurarEstiloTabela(tabela);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) {
                    txtCodigoOperacao.setText(String.valueOf(tabela.getValueAt(linha, 0)));
                }
            }
        });

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(new LineBorder(COR_BORDA, 1));
        scrollTabela.getViewport().setBackground(Color.WHITE);
        abasVisualizacao.addTab("Grade de Ingressos (Tabela)", scrollTabela);

        // Aba 2: JTextArea Exigida pelo PDF para Relatórios
        txtRelatorioPublico = new JTextArea();
        txtRelatorioPublico.setEditable(false);
        txtRelatorioPublico.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRelatorioPublico.setBackground(new Color(250, 252, 255));
        txtRelatorioPublico.setBorder(new EmptyBorder(12, 14, 12, 14));

        JScrollPane scrollRelatorio = new JScrollPane(txtRelatorioPublico);
        scrollRelatorio.setBorder(new LineBorder(COR_BORDA, 1));
        abasVisualizacao.addTab("Extrato & Relatório Executivo (JTextArea)", scrollRelatorio);

        cardAbas.add(abasVisualizacao, BorderLayout.CENTER);
        painel.add(cardAbas, BorderLayout.CENTER);

        return painel;
    }

    private void configurarEstiloTabela(JTable tab) {
        tab.setRowHeight(36);
        tab.setFillsViewportHeight(true);
        tab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tab.setShowVerticalLines(false);
        tab.setShowHorizontalLines(true);
        tab.setGridColor(new Color(241, 245, 249));
        tab.setSelectionBackground(new Color(224, 238, 254)); // Soft blue highlight
        tab.setSelectionForeground(COR_TEXTO);

        JTableHeader th = tab.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(241, 245, 249));
        th.setForeground(new Color(71, 85, 105));
        th.setPreferredSize(new Dimension(0, 36));
        th.setReorderingAllowed(false);

        // Larguras proporcionais
        int[] larguras = {65, 175, 175, 110, 110, 160, 160};
        for (int i = 0; i < larguras.length; i++) {
            tab.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        // Renderizador centralizado para Código e Alinhado para Valores
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tab.getColumnModel().getColumn(0).setCellRenderer(centro);
        tab.getColumnModel().getColumn(4).setCellRenderer(centro);
        tab.getColumnModel().getColumn(6).setCellRenderer(centro);

        // Renderizador de Badges Coloridos na Coluna Status (Tailwind Style)
        tab.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());
    }

    private void alternarCampoDocumentoMeia() {
        boolean ehMeia = cbTipo.getSelectedIndex() == 1;
        lblDocumentoMeia.setEnabled(ehMeia);
        txtDocumentoMeia.setEnabled(ehMeia);
        if (ehMeia) {
            txtDocumentoMeia.requestFocusInWindow();
        }
    }

    private JLabel criarRotuloCampo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rotulo.setForeground(new Color(71, 85, 105)); // Slate 600
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        campo.setPreferredSize(new Dimension(100, 32));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COR_BORDA, 1),
                new EmptyBorder(3, 8, 3, 8)));

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COR_PRIMARIA, 2),
                        new EmptyBorder(2, 7, 2, 7)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COR_BORDA, 1),
                        new EmptyBorder(3, 8, 3, 8)));
            }
        });
        return campo;
    }

    private void inicializarDadosDemonstracao() {
        Ingresso i1 = new IngressoInteira(contadorSequencial++, "Festival de Rock FATEC", "Carlos Eduardo", 160.00);
        IngressoMeia i2 = new IngressoMeia(contadorSequencial++, "Festival de Rock FATEC", "Mariana Silva", 80.00, "DNE #4821 - Estudante");
        Ingresso i3 = new IngressoInteira(contadorSequencial++, "Festival de Rock FATEC", "Lucas Ferreira", 160.00);

        try {
            i2.validarAcessoCatraca();
            i3.cancelar();
        } catch (IngressoInvalidoException ignored) {}

        listaIngressos.add(i1);
        listaIngressos.add(i2);
        listaIngressos.add(i3);
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO E AÇÕES DOS BOTÕES (POO_07 A POO_10)
    // =========================================================================

    private void acaoCadastrar() {
        String evento = txtEvento.getText().trim();
        String participante = txtParticipante.getText().trim();
        String valorStr = txtValorInteira.getText().trim();

        if (evento.isEmpty() || participante.isEmpty() || valorStr.isEmpty()) {
            exibirAviso("Por favor, preencha os campos obrigatórios:\n• Nome do Evento\n• Participante\n• Valor da Inteira", "Campos Pendentes");
            return;
        }

        final double valorInteira;
        try {
            valorInteira = converterValorMoeda(valorStr);
            if (valorInteira <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            exibirErro("Valor informado ('" + valorStr + "') é inválido!\nDigite um número positivo (ex: 120,00 ou 150.00).", "Erro Numérico");
            txtValorInteira.requestFocusInWindow();
            return;
        }

        Ingresso novo;
        int codigo = contadorSequencial++;
        boolean ehMeia = cbTipo.getSelectedIndex() == 1;

        if (ehMeia) {
            double valorMeia = Validavel.calcularValorMeia(valorInteira);
            String doc = txtDocumentoMeia.getText().trim();
            novo = new IngressoMeia(codigo, evento, participante, valorMeia, doc);
        } else {
            novo = new IngressoInteira(codigo, evento, participante, valorInteira);
        }

        listaIngressos.add(novo);
        atualizarTodaInterface();
        selecionarLinhaPorCodigo(novo.getCodigo());

        JOptionPane.showMessageDialog(this,
                "Venda registrada com sucesso!\n\n"
                        + "• Código: #" + novo.getCodigo() + "\n"
                        + "• Tipo: " + novo.getTipo() + "\n"
                        + "• Valor: " + novo.formatarValor(novo.getValor()) + "\n"
                        + "• Participante: " + novo.getNomeParticipante(),
                "Sucesso na Bilheteria",
                JOptionPane.INFORMATION_MESSAGE);

        limparCamposVenda();
    }

    private void acaoPesquisar() {
        String codTexto = txtCodigoOperacao.getText().trim();
        String participanteTexto = txtParticipante.getText().trim();

        if (codTexto.isEmpty() && participanteTexto.isEmpty()) {
            exibirAviso("Digite o Código do Bilhete ou o Nome do Participante para pesquisar.", "Pesquisar");
            return;
        }

        Ingresso encontrado = null;

        if (!codTexto.isEmpty()) {
            try {
                int cod = Integer.parseInt(codTexto);
                for (Ingresso ing : listaIngressos) {
                    if (ing.getCodigo() == cod) {
                        encontrado = ing;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                exibirErro("Código inválido: '" + codTexto + "'. Digite apenas números inteiros.", "Formato Inválido");
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
            carregarDadosNoFormulario(encontrado);
            selecionarLinhaPorCodigo(encontrado.getCodigo());
            JOptionPane.showMessageDialog(this,
                    "Bilhete Localizado:\n\n" + encontrado,
                    "Resultado da Pesquisa",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nenhum ingresso encontrado para a busca informada.",
                    "Não Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void acaoProcessarOperacao() {
        Ingresso ing = obterIngressoSelecionado();
        if (ing == null) return;

        String orientacao = ing.obterInstrucaoCatraca();

        int resposta = JOptionPane.showConfirmDialog(this,
                "CONFERÊNCIA DE PORTARIA:\n\n"
                        + "• Bilhete: #" + ing.getCodigo() + " (" + ing.getTipo() + ")\n"
                        + "• Participante: " + ing.getNomeParticipante() + "\n"
                        + "• Evento: " + ing.getEvento() + "\n"
                        + "• Instrução: " + orientacao + "\n\n"
                        + "Confirmar destravamento da catraca e validar acesso?",
                "Controle de Catraca",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        try {
            ing.validarAcessoCatraca();
            atualizarTodaInterface();
            selecionarLinhaPorCodigo(ing.getCodigo());

            JOptionPane.showMessageDialog(this,
                    "ACESSO LIBERADO COM SUCESSO!\n\n"
                            + "Catraca destravada para " + ing.getNomeParticipante() + ".\n"
                            + "Bilhete #" + ing.getCodigo() + " marcado como UTILIZADO.",
                    "Portaria - Acesso Concedido",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IngressoInvalidoException ex) {
            exibirErro(ex.getMessage(), "Portaria - Acesso Negado");
        }
    }

    private void acaoCancelar() {
        Ingresso ing = obterIngressoSelecionado();
        if (ing == null) return;

        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja CANCELAR o ingresso #" + ing.getCodigo()
                        + " de " + ing.getNomeParticipante() + "?\nEsta operação é irreversível.",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) return;

        try {
            ing.cancelar();
            atualizarTodaInterface();
            selecionarLinhaPorCodigo(ing.getCodigo());

            JOptionPane.showMessageDialog(this,
                    "Ingresso #" + ing.getCodigo() + " cancelado com sucesso!",
                    "Cancelamento Confirmado",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IngressoInvalidoException ex) {
            exibirErro(ex.getMessage(), "Erro no Cancelamento");
        }
    }

    private void acaoOrdenar() {
        if (listaIngressos.isEmpty()) {
            exibirAviso("A coleção está vazia. Não há itens para ordenar.", "Ordenação");
            return;
        }

        // Requisito POO_09: Collections.sort() graças ao Comparable<Ingresso>
        Collections.sort(listaIngressos);
        atualizarTodaInterface();

        JOptionPane.showMessageDialog(this,
                "Coleção de ingressos reorganizada por Código Sequencial com sucesso!\n(Executado via Collections.sort())",
                "Ordenação Concluída",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void acaoGerarRelatorio() {
        gerarExtratoRelatorioJTextArea();
        abasVisualizacao.setSelectedIndex(1);
        JOptionPane.showMessageDialog(this,
                "Relatório Consolidado de Público e Auditoria gerado na aba de texto!",
                "Relatório Emitido",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void acaoSair() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente fechar o sistema?\nTodos os dados da sessão (em memória RAM) serão descartados.",
                "Confirmar Encerramento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    // =========================================================================
    // ATUALIZAÇÃO DA INTERFACE, TABELA E CARDS
    // =========================================================================

    private void atualizarTodaInterface() {
        modeloTabela.setRowCount(0);

        int total = listaIngressos.size();
        int presentes = 0;
        int cancelados = 0;
        double receita = 0.0;

        for (Ingresso ing : listaIngressos) {
            if (ing.isCancelado()) {
                cancelados++;
            } else {
                receita += ing.getValor();
                if (ing.isUtilizado()) {
                    presentes++;
                }
            }

            String hora = (ing.getDataHoraAcesso() != null)
                    ? ing.getDataHoraAcesso().format(Ingresso.FORMATADOR_DATA_HORA)
                    : "-";

            modeloTabela.addRow(new Object[]{
                    ing.getCodigo(),
                    ing.getEvento(),
                    ing.getNomeParticipante(),
                    ing.getTipo(),
                    ing.formatarValor(ing.getValor()),
                    ing.getStatus(),
                    hora
            });
        }

        // Atualiza os Cards de KPIs
        kpiTotal.setValor(String.valueOf(total));
        kpiPresentes.setValor(String.valueOf(presentes));
        kpiCancelados.setValor(String.valueOf(cancelados));
        kpiReceita.setValor(String.format("R$ %.2f", receita));

        gerarExtratoRelatorioJTextArea();
    }

    private void gerarExtratoRelatorioJTextArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================================\n");
        sb.append("                     RELATÓRIO CONSOLIDADO DE BILHETERIA & AUDITORIA DE PORTARIA                        \n");
        sb.append("========================================================================================================\n\n");

        int total = listaIngressos.size();
        int presentes = 0, cancelados = 0, inteiras = 0, meias = 0;
        double recInteiras = 0.0, recMeias = 0.0;

        for (Ingresso ing : listaIngressos) {
            if (ing.isCancelado()) {
                cancelados++;
                continue;
            }
            if (ing instanceof IngressoInteira) {
                inteiras++;
                recInteiras += ing.getValor();
            } else if (ing instanceof IngressoMeia) {
                meias++;
                recMeias += ing.getValor();
            }
            if (ing.isUtilizado()) {
                presentes++;
            }
        }

        int ativos = total - cancelados;
        double txPresenca = (ativos > 0) ? ((double) presentes / ativos) * 100.0 : 0.0;

        sb.append(String.format("• TOTAL DE INGRESSOS EMITIDOS: %d\n", total));
        sb.append(String.format("• INGRESSOS VÁLIDOS / ATIVOS:  %d\n", ativos));
        sb.append(String.format("• INGRESSOS CANCELADOS:        %d\n", cancelados));
        sb.append(String.format("• PÚBLICO PRESENTE NA CATRACA: %d pessoa(s)\n", presentes));
        sb.append(String.format("• TAXA DE CONVERSÃO / ACESSO:  %.1f %%\n", txPresenca));
        sb.append("--------------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("• Receita Inteiras:            R$ %10.2f (%d bilhetes)\n", recInteiras, inteiras));
        sb.append(String.format("• Receita Meias:               R$ %10.2f (%d bilhetes)\n", recMeias, meias));
        sb.append(String.format("• ARRECADAÇÃO TOTAL ATIVA:     R$ %10.2f\n", (recInteiras + recMeias)));
        sb.append("========================================================================================================\n\n");

        sb.append("REGISTRO AUDITADO DE BILHETES EM MEMÓRIA RAM (Comparable<Ingresso>):\n");
        sb.append("--------------------------------------------------------------------------------------------------------\n");
        if (listaIngressos.isEmpty()) {
            sb.append("Nenhum ingresso emitido nesta sessão.\n");
        } else {
            for (Ingresso ing : listaIngressos) {
                sb.append(ing.toString()).append("\n");
            }
        }
        sb.append("--------------------------------------------------------------------------------------------------------\n");

        txtRelatorioPublico.setText(sb.toString());
        txtRelatorioPublico.setCaretPosition(0);
    }

    private Ingresso obterIngressoSelecionado() {
        String codStr = txtCodigoOperacao.getText().trim();
        if (codStr.isEmpty()) {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                codStr = String.valueOf(tabela.getValueAt(linha, 0));
                txtCodigoOperacao.setText(codStr);
            }
        }

        if (codStr.isEmpty()) {
            exibirAviso("Selecione um ingresso na tabela ou digite o código do bilhete.", "Identificação");
            return null;
        }

        try {
            int cod = Integer.parseInt(codStr);
            for (Ingresso ing : listaIngressos) {
                if (ing.getCodigo() == cod) return ing;
            }
            exibirAviso("Ingresso #" + cod + " não encontrado na memória.", "Não Localizado");
        } catch (NumberFormatException ex) {
            exibirErro("Código inválido: '" + codStr + "'.", "Erro Numérico");
        }
        return null;
    }

    private void carregarDadosNoFormulario(Ingresso ing) {
        txtCodigoOperacao.setText(String.valueOf(ing.getCodigo()));
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

    private void selecionarLinhaPorCodigo(int codigo) {
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            if (((Integer) modeloTabela.getValueAt(i, 0)) == codigo) {
                tabela.setRowSelectionInterval(i, i);
                tabela.scrollRectToVisible(tabela.getCellRect(i, 0, true));
                break;
            }
        }
        txtCodigoOperacao.setText(String.valueOf(codigo));
    }

    private void limparCampos() {
        limparCamposVenda();
        txtCodigoOperacao.setText("");
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

    private double converterValorMoeda(String texto) {
        String limpo = texto.trim().replace("R$", "").replace(" ", "");
        if (limpo.contains(",")) limpo = limpo.replace(".", "").replace(',', '.');
        if (limpo.isEmpty()) throw new NumberFormatException();
        return Double.parseDouble(limpo);
    }

    private void exibirAviso(String msg, String titulo) {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.WARNING_MESSAGE);
    }

    private void exibirErro(String msg, String titulo) {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================================
    // LISTENERS DE BOTÕES E JANELA (REQUISITOS POO_07)
    // =========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s == btnCadastrar) acaoCadastrar();
        else if (s == btnPesquisar) acaoPesquisar();
        else if (s == btnProcessarOperacao) acaoProcessarOperacao();
        else if (s == btnCancelar) acaoCancelar();
        else if (s == btnOrdenar) acaoOrdenar();
        else if (s == btnRelatorio) acaoGerarRelatorio();
        else if (s == btnLimpar) limparCampos();
        else if (s == btnSair) acaoSair();
    }

    @Override public void windowClosing(WindowEvent e) { acaoSair(); }
    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    // =========================================================================
    // COMPONENTES MODERNOS CUSTOMIZADOS (100% PURE JAVA SWING)
    // =========================================================================

    /**
     * Card moderno com fundo branco, cantos arredondados e borda sutil Slate.
     */
    static class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;
        public PainelCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(14, 16, 14, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(COR_BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão moderno com bordas arredondadas, cor plana e efeito hover suave.
     */
    static class BotaoModerno extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color corFundo;
        private final Color corHover;
        private boolean hover = false;

        public BotaoModerno(String texto, Color fundo, Color frente) {
            super(texto);
            this.corFundo = fundo;
            this.corHover = escurecerCor(fundo, 0.12f);
            setForeground(frente);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(7, 14, 7, 14));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        private static Color escurecerCor(Color c, float fator) {
            int r = Math.max(0, (int) (c.getRed() * (1 - fator)));
            int g = Math.max(0, (int) (c.getGreen() * (1 - fator)));
            int b = Math.max(0, (int) (c.getBlue() * (1 - fator)));
            return new Color(r, g, b);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? corHover : corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Card KPI moderno com métrica em destaque numérico e legenda.
     */
    static class CardKPI extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JLabel lblValor;

        public CardKPI(String titulo, String valorInicial, Color corDestaque) {
            setLayout(new BorderLayout(4, 3));
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));

            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblTitulo.setForeground(COR_MUTED);

            lblValor = new JLabel(valorInicial);
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblValor.setForeground(corDestaque);

            add(lblTitulo, BorderLayout.NORTH);
            add(lblValor, BorderLayout.CENTER);
        }

        public void setValor(String val) {
            lblValor.setText(val);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(COR_BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Renderizador de Badges / Pílulas modernas para a coluna de Status.
     */
    static class BadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));

            String status = (value != null) ? value.toString() : "";
            if (status.contains("Utilizado") || status.contains("Realizado")) {
                label.setBackground(new Color(209, 250, 229)); // Verde Esmeralda #D1FAE5
                label.setForeground(new Color(6, 95, 70));     // Verde Escuro #065F46
            } else if (status.contains("Cancelado")) {
                label.setBackground(new Color(254, 226, 226)); // Vermelho Suave #FEE2E2
                label.setForeground(new Color(153, 27, 27));   // Vermelho Escuro #991B1B
            } else {
                label.setBackground(new Color(219, 234, 254)); // Azul Suave #DBEAFE
                label.setForeground(new Color(30, 64, 175));   // Azul Escuro #1E40AF
            }

            label.setOpaque(true);
            label.setBorder(new EmptyBorder(3, 8, 3, 8));
            return label;
        }
    }
}
