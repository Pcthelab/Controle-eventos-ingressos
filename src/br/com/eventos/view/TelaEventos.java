package br.com.eventos.view;

import br.com.eventos.exception.IngressoInvalidoException;
import br.com.eventos.interfaces.Validavel;
import br.com.eventos.model.Ingresso;
import br.com.eventos.model.IngressoInteira;
import br.com.eventos.model.IngressoMeia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface Gráfica Moderna e Interativa em Java Swing (javax.swing.*).
 * Possui Seletor Dinâmico de 4 Paletas Clean em Tempo Real:
 * 1. Royal Blue & Frost (Stripe/Apple)
 * 2. Deep Indigo & Slate (Linear/Vercel)
 * 3. Emerald & Mint (Fintech Clean)
 * 4. Midnight Dark Mode (Cyber Slate / OLED)
 *
 * Cumpre 100% dos requisitos da P2 de POO (Prof. Maromo - FATEC):
 * - Persistência 100% em RAM (List<Ingresso>)
 * - ActionListener e WindowListener implementados
 * - Botões padronizados: Cadastrar, Pesquisar, Processar Operação, Ordenar, Sair
 * - JTextArea para relatórios e JOptionPane para mensagens
 */
public class TelaEventos extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    // =========================================================================
    // ESTRUTURA DAS 4 PALETAS CLEAN
    // =========================================================================
    public static class PaletaTema {
        public final String nome;
        public final Color canvas;
        public final Color card;
        public final Color header;
        public final Color primary;
        public final Color primaryLight;
        public final Color texto;
        public final Color muted;
        public final Color borda;
        public final boolean isDark;

        public PaletaTema(String nome, Color canvas, Color card, Color header, Color primary,
                          Color primaryLight, Color texto, Color muted, Color borda, boolean isDark) {
            this.nome = nome;
            this.canvas = canvas;
            this.card = card;
            this.header = header;
            this.primary = primary;
            this.primaryLight = primaryLight;
            this.texto = texto;
            this.muted = muted;
            this.borda = borda;
            this.isDark = isDark;
        }
    }

    public static final PaletaTema[] PALETAS = {
            new PaletaTema("1. Royal Blue & Frost (Stripe)",
                    new Color(248, 250, 252), new Color(255, 255, 255), new Color(15, 23, 42),
                    new Color(37, 99, 235), new Color(239, 246, 255), new Color(15, 23, 42),
                    new Color(100, 116, 139), new Color(226, 232, 240), false),

            new PaletaTema("2. Deep Indigo & Slate (Linear)",
                    new Color(241, 245, 249), new Color(255, 255, 255), new Color(30, 27, 75),
                    new Color(99, 102, 241), new Color(238, 242, 255), new Color(15, 23, 42),
                    new Color(100, 116, 139), new Color(226, 232, 240), false),

            new PaletaTema("3. Emerald & Mint (Fintech)",
                    new Color(240, 253, 244), new Color(255, 255, 255), new Color(6, 78, 59),
                    new Color(5, 150, 105), new Color(236, 253, 245), new Color(15, 23, 42),
                    new Color(100, 116, 139), new Color(209, 250, 229), false),

            new PaletaTema("4. Midnight Dark Mode (OLED)",
                    new Color(15, 23, 42), new Color(30, 41, 59), new Color(2, 6, 23),
                    new Color(56, 189, 248), new Color(30, 41, 59), new Color(248, 250, 252),
                    new Color(148, 163, 184), new Color(51, 65, 85), true)
    };

    private PaletaTema temaAtual = PALETAS[0];

    // Estados Funcionais
    private static final Color COR_SUCESSO = new Color(16, 185, 129);
    private static final Color COR_PERIGO = new Color(239, 68, 68);
    private static final Color COR_AMBAR = new Color(217, 119, 6);

    // Coleção 100% em RAM
    private final List<Ingresso> listaIngressos = new ArrayList<>();
    private int contadorSequencial = 1;

    // Componentes de Formulário
    private JPanel painelPrincipal;
    private JPanel painelHeader;
    private JComboBox<String> cbSeletorTema;
    private JTextField txtEvento;
    private JTextField txtParticipante;
    private JTextField txtValorInteira;
    private JComboBox<String> cbTipo;
    private JTextField txtDocumentoMeia;
    private JLabel lblDocumentoMeia;
    private JTextField txtCodigoOperacao;

    // Botões
    private BotaoModerno btnCadastrar;
    private BotaoModerno btnPesquisar;
    private BotaoModerno btnProcessarOperacao;
    private BotaoModerno btnCancelar;
    private BotaoModerno btnOrdenar;
    private BotaoModerno btnRelatorio;
    private BotaoModerno btnLimpar;
    private BotaoModerno btnSair;

    // Visualizações
    private JTabbedPane abasVisualizacao;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextArea txtRelatorioPublico;

    // Cards
    private List<PainelCard> cardsRegistrados = new ArrayList<>();
    private CardKPI kpiTotal;
    private CardKPI kpiPresentes;
    private CardKPI kpiCancelados;
    private CardKPI kpiReceita;

    public TelaEventos() {
        super("Controle de Eventos & Bilheteria - P2 POO (Prof. Maromo)");
        configurarJanela();
        construirInterface();
        inicializarDadosDemonstracao();
        atualizarTodaInterface();
    }

    private void configurarJanela() {
        setSize(1220, 820);
        setMinimumSize(new Dimension(1060, 740));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }

    private void construirInterface() {
        painelPrincipal = new JPanel(new BorderLayout(0, 16));
        painelPrincipal.setBackground(temaAtual.canvas);
        painelPrincipal.setBorder(new EmptyBorder(16, 20, 18, 20));
        setContentPane(painelPrincipal);

        // 1. Header Bar com Seletor de Tema
        painelPrincipal.add(criarBarraCabecalho(), BorderLayout.NORTH);

        // 2. Área Central Dividida
        JPanel painelCentral = new JPanel(new BorderLayout(16, 0));
        painelCentral.setOpaque(false);

        painelCentral.add(criarColunaEsquerdaFormularios(), BorderLayout.WEST);
        painelCentral.add(criarColunaDireitaVisualizacoes(), BorderLayout.CENTER);

        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
    }

    private JPanel criarBarraCabecalho() {
        painelHeader = new JPanel(new BorderLayout(12, 4)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(temaAtual.header);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(temaAtual.primary);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        painelHeader.setOpaque(false);
        painelHeader.setBorder(new EmptyBorder(12, 22, 14, 22));

        // Títulos
        JPanel painelTitulos = new JPanel(new GridLayout(2, 1, 0, 3));
        painelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("CONTROLE DE EVENTOS & BILHETERIA DIGITAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Tema 08 • Venda de Ingressos, Conferência na Catraca, Cancelamento e Relatórios em RAM");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(148, 163, 184));

        painelTitulos.add(lblTitulo);
        painelTitulos.add(lblSubtitulo);
        painelHeader.add(painelTitulos, BorderLayout.WEST);

        // Lado Direito: Seletor Interativo de Paletas + Badge de Status
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        painelControles.setOpaque(false);

        JLabel lblSeletor = new JLabel("Paleta Clean:");
        lblSeletor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSeletor.setForeground(new Color(203, 213, 225));
        painelControles.add(lblSeletor);

        String[] nomesTemas = new String[PALETAS.length];
        for (int i = 0; i < PALETAS.length; i++) nomesTemas[i] = PALETAS[i].nome;
        cbSeletorTema = new JComboBox<>(nomesTemas);
        cbSeletorTema.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbSeletorTema.setBackground(Color.WHITE);
        cbSeletorTema.setPreferredSize(new Dimension(220, 30));
        cbSeletorTema.addActionListener(e -> trocarTema(cbSeletorTema.getSelectedIndex()));
        painelControles.add(cbSeletorTema);

        JLabel lblPulse = new JLabel("● 100% RAM");
        lblPulse.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPulse.setForeground(new Color(52, 211, 153));
        painelControles.add(lblPulse);

        painelHeader.add(painelControles, BorderLayout.EAST);
        return painelHeader;
    }

    private JPanel criarColunaEsquerdaFormularios() {
        JPanel coluna = new JPanel();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setOpaque(false);
        coluna.setPreferredSize(new Dimension(390, 0));

        // CARD 1: Venda
        PainelCard cardVenda = new PainelCard();
        cardsRegistrados.add(cardVenda);
        cardVenda.setLayout(new BoxLayout(cardVenda, BoxLayout.Y_AXIS));

        JLabel tituloVenda = new JLabel("Nova Venda de Ingresso");
        tituloVenda.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloVenda.setForeground(temaAtual.texto);
        tituloVenda.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardVenda.add(tituloVenda);
        cardVenda.add(Box.createVerticalStrut(12));

        cardVenda.add(criarRotuloCampo("Nome do Evento:"));
        txtEvento = criarCampoTexto();
        cardVenda.add(txtEvento);
        cardVenda.add(Box.createVerticalStrut(8));

        cardVenda.add(criarRotuloCampo("Nome do Participante:"));
        txtParticipante = criarCampoTexto();
        cardVenda.add(txtParticipante);
        cardVenda.add(Box.createVerticalStrut(8));

        JPanel linhaTipoValor = new JPanel(new GridLayout(1, 2, 10, 0));
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
        cardVenda.add(Box.createVerticalStrut(14));

        btnCadastrar = new BotaoModerno("+ Cadastrar (Registrar Venda)", temaAtual.primary, Color.WHITE, true);
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCadastrar.addActionListener(this);
        cardVenda.add(btnCadastrar);

        coluna.add(cardVenda);
        coluna.add(Box.createVerticalStrut(14));

        // CARD 2: Portaria
        PainelCard cardOperacoes = new PainelCard();
        cardsRegistrados.add(cardOperacoes);
        cardOperacoes.setLayout(new BoxLayout(cardOperacoes, BoxLayout.Y_AXIS));

        JLabel tituloOperacoes = new JLabel("Operações de Portaria & Catraca");
        tituloOperacoes.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tituloOperacoes.setForeground(temaAtual.texto);
        tituloOperacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(tituloOperacoes);
        cardOperacoes.add(Box.createVerticalStrut(12));

        cardOperacoes.add(criarRotuloCampo("Código do Bilhete:"));

        JPanel linhaCodigoBusca = new JPanel(new BorderLayout(8, 0));
        linhaCodigoBusca.setOpaque(false);
        linhaCodigoBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        linhaCodigoBusca.setPreferredSize(new Dimension(300, 34));

        txtCodigoOperacao = criarCampoTexto();
        txtCodigoOperacao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        linhaCodigoBusca.add(txtCodigoOperacao, BorderLayout.CENTER);

        btnPesquisar = new BotaoModerno("Pesquisar", new Color(241, 245, 249), temaAtual.texto, false);
        btnPesquisar.addActionListener(this);
        linhaCodigoBusca.add(btnPesquisar, BorderLayout.EAST);

        linhaCodigoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(linhaCodigoBusca);
        cardOperacoes.add(Box.createVerticalStrut(12));

        btnProcessarOperacao = new BotaoModerno("Processar Operação (Catraca)", COR_SUCESSO, Color.WHITE, true);
        btnProcessarOperacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcessarOperacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnProcessarOperacao.addActionListener(this);
        cardOperacoes.add(btnProcessarOperacao);
        cardOperacoes.add(Box.createVerticalStrut(8));

        JPanel linhaAuxiliar = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaAuxiliar.setOpaque(false);
        linhaAuxiliar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        btnCancelar = new BotaoModerno("Cancelar", new Color(254, 242, 242), COR_PERIGO, false);
        btnCancelar.addActionListener(this);
        linhaAuxiliar.add(btnCancelar);

        btnLimpar = new BotaoModerno("Limpar", new Color(241, 245, 249), temaAtual.muted, false);
        btnLimpar.addActionListener(this);
        linhaAuxiliar.add(btnLimpar);

        linhaAuxiliar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOperacoes.add(linhaAuxiliar);

        coluna.add(cardOperacoes);
        return coluna;
    }

    private JPanel criarColunaDireitaVisualizacoes() {
        JPanel painel = new JPanel(new BorderLayout(0, 14));
        painel.setOpaque(false);

        // 4 Cards KPIs
        JPanel painelKPIs = new JPanel(new GridLayout(1, 4, 12, 0));
        painelKPIs.setOpaque(false);
        painelKPIs.setPreferredSize(new Dimension(0, 72));

        kpiTotal = new CardKPI("TOTAL EMITIDOS", "0", temaAtual.primary);
        kpiPresentes = new CardKPI("PÚBLICO CATRACA", "0", COR_SUCESSO);
        kpiCancelados = new CardKPI("CANCELADOS", "0", COR_PERIGO);
        kpiReceita = new CardKPI("RECEITA TOTAL", "R$ 0,00", new Color(124, 58, 237));

        painelKPIs.add(kpiTotal);
        painelKPIs.add(kpiPresentes);
        painelKPIs.add(kpiCancelados);
        painelKPIs.add(kpiReceita);
        painel.add(painelKPIs, BorderLayout.NORTH);

        // Card com Abas
        PainelCard cardAbas = new PainelCard();
        cardsRegistrados.add(cardAbas);
        cardAbas.setLayout(new BorderLayout(0, 10));

        // Barra superior
        JPanel barraSuperior = new JPanel(new BorderLayout(8, 0));
        barraSuperior.setOpaque(false);

        JLabel lblDica = new JLabel("Dica: Clique em qualquer linha para carregar o código instantaneamente");
        lblDica.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDica.setForeground(temaAtual.muted);
        barraSuperior.add(lblDica, BorderLayout.WEST);

        JPanel botoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoesAcao.setOpaque(false);

        btnOrdenar = new BotaoModerno("Ordenar (Código)", new Color(254, 243, 199), COR_AMBAR, false);
        btnOrdenar.addActionListener(this);
        botoesAcao.add(btnOrdenar);

        btnRelatorio = new BotaoModerno("Relatório de Público", new Color(243, 232, 255), new Color(126, 34, 206), false);
        btnRelatorio.addActionListener(this);
        botoesAcao.add(btnRelatorio);

        btnSair = new BotaoModerno("Sair", new Color(254, 242, 242), COR_PERIGO, false);
        btnSair.addActionListener(this);
        botoesAcao.add(btnSair);

        barraSuperior.add(botoesAcao, BorderLayout.EAST);
        cardAbas.add(barraSuperior, BorderLayout.NORTH);

        // Abas
        abasVisualizacao = new JTabbedPane();
        abasVisualizacao.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Aba 1: Tabela
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
        aplicarScrollbarModerna(scrollTabela);
        abasVisualizacao.addTab("Grade de Ingressos (Tabela)", scrollTabela);

        // Aba 2: Relatório JTextArea
        txtRelatorioPublico = new JTextArea();
        txtRelatorioPublico.setEditable(false);
        txtRelatorioPublico.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRelatorioPublico.setBackground(temaAtual.isDark ? new Color(15, 23, 42) : new Color(250, 252, 255));
        txtRelatorioPublico.setForeground(temaAtual.isDark ? new Color(241, 245, 249) : temaAtual.texto);
        txtRelatorioPublico.setBorder(new EmptyBorder(14, 16, 14, 16));

        JScrollPane scrollRelatorio = new JScrollPane(txtRelatorioPublico);
        aplicarScrollbarModerna(scrollRelatorio);
        abasVisualizacao.addTab("Extrato & Relatório Executivo (JTextArea)", scrollRelatorio);

        cardAbas.add(abasVisualizacao, BorderLayout.CENTER);
        painel.add(cardAbas, BorderLayout.CENTER);

        return painel;
    }

    private void configurarEstiloTabela(JTable tab) {
        tab.setRowHeight(38);
        tab.setFillsViewportHeight(true);
        tab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tab.setShowVerticalLines(false);
        tab.setShowHorizontalLines(true);
        tab.setGridColor(new Color(241, 245, 249));
        tab.setSelectionBackground(temaAtual.primaryLight);
        tab.setSelectionForeground(temaAtual.texto);

        JTableHeader th = tab.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(241, 245, 249));
        th.setForeground(new Color(71, 85, 105));
        th.setPreferredSize(new Dimension(0, 38));
        th.setReorderingAllowed(false);

        int[] larguras = {65, 175, 175, 110, 110, 160, 160};
        for (int i = 0; i < larguras.length; i++) {
            tab.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tab.getColumnModel().getColumn(0).setCellRenderer(centro);
        tab.getColumnModel().getColumn(4).setCellRenderer(centro);
        tab.getColumnModel().getColumn(6).setCellRenderer(centro);

        tab.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());
    }

    private void aplicarScrollbarModerna(JScrollPane scroll) {
        scroll.setBorder(new LineBorder(temaAtual.borda, 1));
        scroll.getViewport().setBackground(temaAtual.card);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
    }

    /**
     * Aplica uma nova paleta clean em tempo real em todos os componentes.
     */
    private void trocarTema(int index) {
        if (index < 0 || index >= PALETAS.length) return;
        temaAtual = PALETAS[index];

        // Atualiza Canvas e Header
        painelPrincipal.setBackground(temaAtual.canvas);
        painelHeader.repaint();

        // Atualiza Botão Primário e KPI de destaque
        btnCadastrar.setCorFundo(temaAtual.primary);
        kpiTotal.setCorAcento(temaAtual.primary);

        // Atualiza Tabela
        tabela.setSelectionBackground(temaAtual.primaryLight);
        tabela.setSelectionForeground(temaAtual.texto);
        tabela.setBackground(temaAtual.card);
        tabela.setForeground(temaAtual.texto);
        tabela.getTableHeader().setBackground(temaAtual.isDark ? new Color(30, 41, 59) : new Color(241, 245, 249));
        tabela.getTableHeader().setForeground(temaAtual.isDark ? new Color(226, 232, 240) : new Color(71, 85, 105));

        // Atualiza Relatório de Texto
        txtRelatorioPublico.setBackground(temaAtual.isDark ? new Color(15, 23, 42) : new Color(250, 252, 255));
        txtRelatorioPublico.setForeground(temaAtual.isDark ? new Color(241, 245, 249) : temaAtual.texto);

        // Atualiza Campos de Texto
        JTextField[] campos = {txtEvento, txtParticipante, txtValorInteira, txtDocumentoMeia, txtCodigoOperacao};
        for (JTextField f : campos) {
            if (f != null) {
                f.setBackground(temaAtual.card);
                f.setForeground(temaAtual.texto);
                f.setCaretColor(temaAtual.texto);
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(temaAtual.borda, 1),
                        new EmptyBorder(3, 10, 3, 10)));
            }
        }

        // Repinta todos os Cards
        for (PainelCard card : cardsRegistrados) {
            card.repaint();
        }

        SwingUtilities.updateComponentTreeUI(this);
        atualizarTodaInterface();
    }

    private void alternarCampoDocumentoMeia() {
        boolean ehMeia = cbTipo.getSelectedIndex() == 1;
        lblDocumentoMeia.setEnabled(ehMeia);
        txtDocumentoMeia.setEnabled(ehMeia);
        if (ehMeia) txtDocumentoMeia.requestFocusInWindow();
    }

    private JLabel criarRotuloCampo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rotulo.setForeground(new Color(71, 85, 105));
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campo.setPreferredSize(new Dimension(100, 34));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(temaAtual.borda, 1),
                new EmptyBorder(3, 10, 3, 10)));

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(temaAtual.primary, 2),
                        new EmptyBorder(2, 9, 2, 9)));
            }
            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(temaAtual.borda, 1),
                        new EmptyBorder(3, 10, 3, 10)));
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

        Collections.sort(listaIngressos);
        atualizarTodaInterface();

        JOptionPane.showMessageDialog(this,
                "Coleção reorganizada por Código Sequencial com sucesso!\n(Executado via Collections.sort())",
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
    // COMPONENTES MODERNOS COM SOMBRA E ESTILO 2D (PURE JAVA SWING)
    // =========================================================================

    class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public PainelCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(16, 18, 16, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 2;
            int y = 2;
            int w = getWidth() - 5;
            int h = getHeight() - 5;

            // Sombra suave (Drop Shadow)
            g2.setColor(new Color(15, 23, 42, temaAtual.isDark ? 30 : 8));
            g2.fillRoundRect(x + 1, y + 3, w, h, 16, 16);

            // Fundo do Card
            g2.setColor(temaAtual.card);
            g2.fillRoundRect(x, y, w, h, 14, 14);

            // Borda
            g2.setColor(temaAtual.borda);
            g2.drawRoundRect(x, y, w, h, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class BotaoModerno extends JButton {
        private static final long serialVersionUID = 1L;
        private Color corFundo;
        private Color corHover;
        private final boolean temSombra;
        private boolean hover = false;

        public BotaoModerno(String texto, Color fundo, Color frente, boolean temSombra) {
            super(texto);
            setCorFundo(fundo);
            this.temSombra = temSombra;
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

        public void setCorFundo(Color c) {
            this.corFundo = c;
            this.corHover = escurecerCor(c, 0.12f);
            repaint();
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

            if (temSombra) {
                g2.setColor(new Color(15, 23, 42, 18));
                g2.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 2, 10, 10);
            }

            g2.setColor(hover ? corHover : corFundo);
            g2.fillRoundRect(0, 0, getWidth() - (temSombra ? 1 : 0), getHeight() - (temSombra ? 1 : 0), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class CardKPI extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JLabel lblValor;
        private Color corAcento;

        public CardKPI(String titulo, String valorInicial, Color corDestaque) {
            this.corAcento = corDestaque;
            setLayout(new BorderLayout(4, 3));
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));

            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblTitulo.setForeground(temaAtual.muted);

            lblValor = new JLabel(valorInicial);
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 19));
            lblValor.setForeground(corDestaque);

            add(lblTitulo, BorderLayout.NORTH);
            add(lblValor, BorderLayout.CENTER);
        }

        public void setValor(String val) {
            lblValor.setText(val);
        }

        public void setCorAcento(Color c) {
            this.corAcento = c;
            lblValor.setForeground(c);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 3;
            int h = getHeight() - 3;

            g2.setColor(new Color(15, 23, 42, temaAtual.isDark ? 25 : 8));
            g2.fillRoundRect(1, 2, w, h, 12, 12);

            g2.setColor(temaAtual.card);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
            g2.setColor(temaAtual.borda);
            g2.drawRoundRect(0, 0, w, h, 12, 12);

            g2.setColor(corAcento);
            g2.fillRoundRect(0, 0, 4, h, 4, 4);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    class BadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));

            String status = (value != null) ? value.toString() : "";
            if (status.contains("Utilizado") || status.contains("Realizado")) {
                label.setBackground(new Color(236, 253, 245));
                label.setForeground(new Color(4, 120, 87));
            } else if (status.contains("Cancelado")) {
                label.setBackground(new Color(254, 242, 242));
                label.setForeground(new Color(185, 28, 28));
            } else {
                label.setBackground(temaAtual.primaryLight);
                label.setForeground(temaAtual.primary);
            }

            label.setOpaque(true);
            label.setBorder(new EmptyBorder(3, 8, 3, 8));
            return label;
        }
    }

    class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = temaAtual.card;
        }

        @Override protected JButton createDecreaseButton(int orientation) { return criarBotaoVazio(); }
        @Override protected JButton createIncreaseButton(int orientation) { return criarBotaoVazio(); }

        private JButton criarBotaoVazio() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() ? new Color(148, 163, 184) : thumbColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle thumbBounds) {}
    }
}
