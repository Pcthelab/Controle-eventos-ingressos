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

public class TelaEventos extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    public static class PaletaTema {
        public final String nome;
        public final Color canvas;
        public final Color card;
        public final Color header;
        public final Color primary;
        public final Color primaryLight;
        public final Color secondaryBg;
        public final Color texto;
        public final Color muted;
        public final Color borda;
        public final boolean isDark;

        public PaletaTema(String nome, Color canvas, Color card, Color header, Color primary,
                          Color primaryLight, Color secondaryBg, Color texto, Color muted,
                          Color borda, boolean isDark) {
            this.nome = nome;
            this.canvas = canvas;
            this.card = card;
            this.header = header;
            this.primary = primary;
            this.primaryLight = primaryLight;
            this.secondaryBg = secondaryBg;
            this.texto = texto;
            this.muted = muted;
            this.borda = borda;
            this.isDark = isDark;
        }
    }

    public static final PaletaTema[] PALETAS = {
            new PaletaTema("Graphite Minimal",
                    new Color(246, 246, 247), new Color(255, 255, 255), new Color(24, 24, 27),
                    new Color(39, 39, 42), new Color(244, 244, 245), new Color(244, 244, 245),
                    new Color(24, 24, 27), new Color(113, 113, 122), new Color(228, 228, 231), false),

            new PaletaTema("Slate Blue",
                    new Color(244, 246, 249), new Color(255, 255, 255), new Color(15, 23, 42),
                    new Color(29, 78, 216), new Color(239, 246, 255), new Color(241, 245, 249),
                    new Color(15, 23, 42), new Color(100, 116, 139), new Color(226, 232, 240), false)
    };

    private static final Color PERIGO_TEXTO_CLARO = new Color(185, 28, 28);
    private static final Color PERIGO_TEXTO_ESCURO = new Color(252, 165, 165);
    private static final Color PERIGO_BG_CLARO = new Color(254, 242, 242);
    private static final Color PERIGO_BG_ESCURO = new Color(69, 26, 26);

    private PaletaTema temaAtual = PALETAS[0];
    private final List<Ingresso> listaIngressos = new ArrayList<>();
    private int contadorSequencial = 1;

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

    private BotaoModerno btnCadastrar;
    private BotaoModerno btnPesquisar;
    private BotaoModerno btnProcessarOperacao;
    private BotaoModerno btnCancelar;
    private BotaoModerno btnOrdenar;
    private BotaoModerno btnRelatorio;
    private BotaoModerno btnLimpar;
    private BotaoModerno btnSair;

    private JTabbedPane abasVisualizacao;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextArea txtRelatorioPublico;
    private JScrollPane scrollTabela;
    private JScrollPane scrollRelatorio;

    private final List<PainelCard> cardsRegistrados = new ArrayList<>();
    private final List<JLabel> titulosRegistrados = new ArrayList<>();
    private final List<JLabel> rotulosRegistrados = new ArrayList<>();
    private JLabel lblDica;

    private CardKPI kpiTotal;
    private CardKPI kpiPresentes;
    private CardKPI kpiCancelados;
    private CardKPI kpiReceita;

    public TelaEventos() {
        super("Controle de Eventos e Venda de Ingressos");
        configurarJanela();
        construirInterface();
        atualizarInterface();
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

        painelPrincipal.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(16, 0));
        painelCentral.setOpaque(false);
        painelCentral.add(criarPainelFormularios(), BorderLayout.WEST);
        painelCentral.add(criarPainelVisualizacao(), BorderLayout.CENTER);

        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
    }

    private JPanel criarCabecalho() {
        painelHeader = new JPanel(new BorderLayout(12, 4)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(temaAtual.header);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(temaAtual.primary);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        painelHeader.setOpaque(false);
        painelHeader.setBorder(new EmptyBorder(14, 22, 15, 22));

        JPanel painelTitulos = new JPanel(new GridLayout(2, 1, 0, 2));
        painelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Controle de Eventos e Bilheteria");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Venda de ingressos, conferência na portaria, cancelamento e relatório de público");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(148, 163, 184));

        painelTitulos.add(lblTitulo);
        painelTitulos.add(lblSubtitulo);
        painelHeader.add(painelTitulos, BorderLayout.WEST);

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        painelControles.setOpaque(false);

        JLabel lblTema = new JLabel("Paleta:");
        lblTema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTema.setForeground(new Color(203, 213, 225));
        painelControles.add(lblTema);

        String[] nomesTemas = new String[PALETAS.length];
        for (int i = 0; i < PALETAS.length; i++) {
            nomesTemas[i] = PALETAS[i].nome;
        }
        cbSeletorTema = new JComboBox<>(nomesTemas);
        cbSeletorTema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSeletorTema.setBackground(Color.WHITE);
        cbSeletorTema.setPreferredSize(new Dimension(160, 28));
        cbSeletorTema.addActionListener(e -> aplicarTema(cbSeletorTema.getSelectedIndex()));
        painelControles.add(cbSeletorTema);

        painelHeader.add(painelControles, BorderLayout.EAST);
        return painelHeader;
    }

    private JPanel criarPainelFormularios() {
        JPanel coluna = new JPanel();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setOpaque(false);
        coluna.setPreferredSize(new Dimension(380, 0));

        PainelCard cardVenda = new PainelCard();
        cardsRegistrados.add(cardVenda);
        cardVenda.setLayout(new BoxLayout(cardVenda, BoxLayout.Y_AXIS));

        cardVenda.add(criarTituloCard("Nova Venda"));
        cardVenda.add(Box.createVerticalStrut(12));

        cardVenda.add(criarRotuloCampo("Evento"));
        txtEvento = criarCampoTexto();
        cardVenda.add(txtEvento);
        cardVenda.add(Box.createVerticalStrut(10));

        cardVenda.add(criarRotuloCampo("Participante"));
        txtParticipante = criarCampoTexto();
        cardVenda.add(txtParticipante);
        cardVenda.add(Box.createVerticalStrut(10));

        JPanel linhaTipoValor = new JPanel(new GridLayout(1, 2, 10, 0));
        linhaTipoValor.setOpaque(false);
        linhaTipoValor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel pTipo = new JPanel(new BorderLayout(0, 4));
        pTipo.setOpaque(false);
        pTipo.add(criarRotuloCampo("Tipo de Bilhete"), BorderLayout.NORTH);
        cbTipo = new JComboBox<>(new String[]{"Inteira", "Meia-entrada (50%)"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setPreferredSize(new Dimension(100, 32));
        cbTipo.addActionListener(e -> atualizarCampoComprovante());
        pTipo.add(cbTipo, BorderLayout.CENTER);
        linhaTipoValor.add(pTipo);

        JPanel pValor = new JPanel(new BorderLayout(0, 4));
        pValor.setOpaque(false);
        pValor.add(criarRotuloCampo("Valor Inteira (R$)"), BorderLayout.NORTH);
        txtValorInteira = criarCampoTexto();
        pValor.add(txtValorInteira, BorderLayout.CENTER);
        linhaTipoValor.add(pValor);

        linhaTipoValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardVenda.add(linhaTipoValor);
        cardVenda.add(Box.createVerticalStrut(10));

        lblDocumentoMeia = criarRotuloCampo("Comprovante de Meia-entrada");
        lblDocumentoMeia.setEnabled(false);
        cardVenda.add(lblDocumentoMeia);
        txtDocumentoMeia = criarCampoTexto();
        txtDocumentoMeia.setText("Carteira Estudantil / DNE");
        txtDocumentoMeia.setEnabled(false);
        cardVenda.add(txtDocumentoMeia);
        cardVenda.add(Box.createVerticalStrut(16));

        btnCadastrar = new BotaoModerno("Cadastrar", temaAtual.primary, Color.WHITE, false);
        btnCadastrar.setToolTipText("Registrar nova venda de ingresso");
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCadastrar.addActionListener(this);
        cardVenda.add(btnCadastrar);

        coluna.add(cardVenda);
        coluna.add(Box.createVerticalStrut(14));

        PainelCard cardPortaria = new PainelCard();
        cardsRegistrados.add(cardPortaria);
        cardPortaria.setLayout(new BoxLayout(cardPortaria, BoxLayout.Y_AXIS));

        cardPortaria.add(criarTituloCard("Portaria e Operações"));
        cardPortaria.add(Box.createVerticalStrut(12));

        cardPortaria.add(criarRotuloCampo("Código do Bilhete"));

        JPanel linhaCodigoBusca = new JPanel(new BorderLayout(8, 0));
        linhaCodigoBusca.setOpaque(false);
        linhaCodigoBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        linhaCodigoBusca.setPreferredSize(new Dimension(300, 34));

        txtCodigoOperacao = criarCampoTexto();
        txtCodigoOperacao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        linhaCodigoBusca.add(txtCodigoOperacao, BorderLayout.CENTER);

        btnPesquisar = new BotaoModerno("Pesquisar", temaAtual.secondaryBg, temaAtual.texto, true);
        btnPesquisar.addActionListener(this);
        linhaCodigoBusca.add(btnPesquisar, BorderLayout.EAST);

        linhaCodigoBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPortaria.add(linhaCodigoBusca);
        cardPortaria.add(Box.createVerticalStrut(12));

        btnProcessarOperacao = new BotaoModerno("Processar Operação", temaAtual.header, Color.WHITE, false);
        btnProcessarOperacao.setToolTipText("Validar acesso na catraca e dar baixa no bilhete");
        btnProcessarOperacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProcessarOperacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnProcessarOperacao.addActionListener(this);
        cardPortaria.add(btnProcessarOperacao);
        cardPortaria.add(Box.createVerticalStrut(8));

        JPanel linhaAuxiliar = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaAuxiliar.setOpaque(false);
        linhaAuxiliar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        btnCancelar = new BotaoModerno("Cancelar", PERIGO_BG_CLARO, PERIGO_TEXTO_CLARO, true);
        btnCancelar.addActionListener(this);
        linhaAuxiliar.add(btnCancelar);

        btnLimpar = new BotaoModerno("Limpar", temaAtual.secondaryBg, temaAtual.muted, true);
        btnLimpar.addActionListener(this);
        linhaAuxiliar.add(btnLimpar);

        linhaAuxiliar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPortaria.add(linhaAuxiliar);

        coluna.add(cardPortaria);
        return coluna;
    }

    private JPanel criarPainelVisualizacao() {
        JPanel painel = new JPanel(new BorderLayout(0, 14));
        painel.setOpaque(false);

        JPanel painelKPIs = new JPanel(new GridLayout(1, 4, 12, 0));
        painelKPIs.setOpaque(false);
        painelKPIs.setPreferredSize(new Dimension(0, 72));

        kpiTotal = new CardKPI("EMITIDOS", "0", false);
        kpiPresentes = new CardKPI("PRESENTES", "0", false);
        kpiCancelados = new CardKPI("CANCELADOS", "0", false);
        kpiReceita = new CardKPI("RECEITA ATIVA", "R$ 0,00", true);

        painelKPIs.add(kpiTotal);
        painelKPIs.add(kpiPresentes);
        painelKPIs.add(kpiCancelados);
        painelKPIs.add(kpiReceita);
        painel.add(painelKPIs, BorderLayout.NORTH);

        PainelCard cardAbas = new PainelCard();
        cardsRegistrados.add(cardAbas);
        cardAbas.setLayout(new BorderLayout(0, 10));

        JPanel barraSuperior = new JPanel(new BorderLayout(8, 0));
        barraSuperior.setOpaque(false);

        lblDica = new JLabel("Clique em uma linha para selecionar o ingresso");
        lblDica.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDica.setForeground(temaAtual.muted);
        barraSuperior.add(lblDica, BorderLayout.WEST);

        JPanel botoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoesAcao.setOpaque(false);

        btnOrdenar = new BotaoModerno("Ordenar", temaAtual.secondaryBg, temaAtual.texto, true);
        btnOrdenar.setToolTipText("Ordenar bilhetes pelo código sequencial");
        btnOrdenar.addActionListener(this);
        botoesAcao.add(btnOrdenar);

        btnRelatorio = new BotaoModerno("Relatório", temaAtual.secondaryBg, temaAtual.texto, true);
        btnRelatorio.setToolTipText("Abrir relatório consolidado de público");
        btnRelatorio.addActionListener(this);
        botoesAcao.add(btnRelatorio);

        btnSair = new BotaoModerno("Sair", PERIGO_BG_CLARO, PERIGO_TEXTO_CLARO, true);
        btnSair.addActionListener(this);
        botoesAcao.add(btnSair);

        barraSuperior.add(botoesAcao, BorderLayout.EAST);
        cardAbas.add(barraSuperior, BorderLayout.NORTH);

        abasVisualizacao = new JTabbedPane();
        abasVisualizacao.setFont(new Font("Segoe UI", Font.BOLD, 12));

        modeloTabela = new DefaultTableModel(
                new Object[]{"Código", "Evento", "Participante", "Tipo", "Valor", "Status", "Acesso"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Integer.class : String.class;
            }
        };

        tabela = new JTable(modeloTabela);
        configurarTabela(tabela);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) {
                    txtCodigoOperacao.setText(String.valueOf(tabela.getValueAt(linha, 0)));
                }
            }
        });

        scrollTabela = new JScrollPane(tabela);
        estilizarScroll(scrollTabela);
        abasVisualizacao.addTab("Ingressos", scrollTabela);

        txtRelatorioPublico = new JTextArea();
        txtRelatorioPublico.setEditable(false);
        txtRelatorioPublico.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRelatorioPublico.setBackground(new Color(250, 251, 252));
        txtRelatorioPublico.setForeground(temaAtual.texto);
        txtRelatorioPublico.setBorder(new EmptyBorder(14, 16, 14, 16));

        scrollRelatorio = new JScrollPane(txtRelatorioPublico);
        estilizarScroll(scrollRelatorio);
        abasVisualizacao.addTab("Relatório de Público", scrollRelatorio);

        cardAbas.add(abasVisualizacao, BorderLayout.CENTER);
        painel.add(cardAbas, BorderLayout.CENTER);

        return painel;
    }

    private void configurarTabela(JTable tab) {
        tab.setRowHeight(40);
        tab.setFillsViewportHeight(true);
        tab.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tab.setShowVerticalLines(false);
        tab.setShowHorizontalLines(true);
        tab.setGridColor(new Color(241, 245, 249));
        tab.setSelectionBackground(temaAtual.primaryLight);
        tab.setSelectionForeground(temaAtual.texto);

        JTableHeader header = tab.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        int[] larguras = {65, 185, 175, 115, 105, 125, 155};
        for (int i = 0; i < larguras.length; i++) {
            tab.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tab.getColumnModel().getColumn(0).setCellRenderer(centro);
        tab.getColumnModel().getColumn(4).setCellRenderer(centro);
        tab.getColumnModel().getColumn(6).setCellRenderer(centro);
        tab.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
    }

    private void estilizarScroll(JScrollPane scroll) {
        scroll.setBorder(new LineBorder(temaAtual.borda, 1));
        scroll.getViewport().setBackground(temaAtual.card);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
    }

    private void aplicarTema(int index) {
        if (index < 0 || index >= PALETAS.length) {
            return;
        }
        temaAtual = PALETAS[index];

        painelPrincipal.setBackground(temaAtual.canvas);
        painelHeader.repaint();

        btnCadastrar.setEstilo(temaAtual.primary, Color.WHITE);
        btnProcessarOperacao.setEstilo(temaAtual.isDark ? new Color(51, 65, 85) : temaAtual.header, Color.WHITE);

        btnPesquisar.setEstilo(temaAtual.secondaryBg, temaAtual.texto);
        btnOrdenar.setEstilo(temaAtual.secondaryBg, temaAtual.texto);
        btnRelatorio.setEstilo(temaAtual.secondaryBg, temaAtual.texto);
        btnLimpar.setEstilo(temaAtual.secondaryBg, temaAtual.muted);

        Color perigoBg = temaAtual.isDark ? PERIGO_BG_ESCURO : PERIGO_BG_CLARO;
        Color perigoFg = temaAtual.isDark ? PERIGO_TEXTO_ESCURO : PERIGO_TEXTO_CLARO;
        btnCancelar.setEstilo(perigoBg, perigoFg);
        btnSair.setEstilo(perigoBg, perigoFg);

        lblDica.setForeground(temaAtual.muted);

        for (JLabel titulo : titulosRegistrados) {
            titulo.setForeground(temaAtual.texto);
        }
        for (JLabel rotulo : rotulosRegistrados) {
            rotulo.setForeground(temaAtual.muted);
        }

        kpiTotal.atualizarEstilo();
        kpiPresentes.atualizarEstilo();
        kpiCancelados.atualizarEstilo();
        kpiReceita.atualizarEstilo();

        tabela.setSelectionBackground(temaAtual.primaryLight);
        tabela.setSelectionForeground(temaAtual.texto);
        tabela.setBackground(temaAtual.card);
        tabela.setForeground(temaAtual.texto);
        tabela.setGridColor(temaAtual.borda);
        tabela.getTableHeader().setBackground(temaAtual.isDark ? new Color(30, 41, 59) : new Color(248, 250, 252));
        tabela.getTableHeader().setForeground(temaAtual.isDark ? new Color(203, 213, 225) : new Color(71, 85, 105));

        scrollTabela.setBorder(new LineBorder(temaAtual.borda, 1));
        scrollTabela.getViewport().setBackground(temaAtual.card);
        scrollRelatorio.setBorder(new LineBorder(temaAtual.borda, 1));
        scrollRelatorio.getViewport().setBackground(temaAtual.isDark ? new Color(15, 23, 42) : new Color(250, 251, 252));

        txtRelatorioPublico.setBackground(temaAtual.isDark ? new Color(15, 23, 42) : new Color(250, 251, 252));
        txtRelatorioPublico.setForeground(temaAtual.texto);

        JTextField[] campos = {txtEvento, txtParticipante, txtValorInteira, txtDocumentoMeia, txtCodigoOperacao};
        for (JTextField campo : campos) {
            campo.setBackground(temaAtual.card);
            campo.setForeground(temaAtual.texto);
            campo.setCaretColor(temaAtual.texto);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(temaAtual.borda, 1),
                    new EmptyBorder(3, 10, 3, 10)));
        }

        for (PainelCard card : cardsRegistrados) {
            card.repaint();
        }

        SwingUtilities.updateComponentTreeUI(this);
        atualizarInterface();
    }

    private void atualizarCampoComprovante() {
        boolean meia = cbTipo.getSelectedIndex() == 1;
        lblDocumentoMeia.setEnabled(meia);
        txtDocumentoMeia.setEnabled(meia);
        if (meia) {
            txtDocumentoMeia.requestFocusInWindow();
        }
    }

    private JLabel criarTituloCard(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(temaAtual.texto);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        titulosRegistrados.add(titulo);
        return titulo;
    }

    private JLabel criarRotuloCampo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rotulo.setForeground(temaAtual.muted);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        rotulosRegistrados.add(rotulo);
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

    private void cadastrarIngresso() {
        String evento = txtEvento.getText().trim();
        String participante = txtParticipante.getText().trim();
        String valorTexto = txtValorInteira.getText().trim();

        if (evento.isEmpty() || participante.isEmpty() || valorTexto.isEmpty()) {
            exibirAviso("Preencha o evento, o participante e o valor da inteira.", "Campos obrigatórios");
            return;
        }

        final double valorInteira;
        try {
            valorInteira = converterValor(valorTexto);
            if (valorInteira <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            exibirErro("Valor inválido: '" + valorTexto + "'. Informe um valor positivo (ex: 120,00).", "Valor inválido");
            txtValorInteira.requestFocusInWindow();
            return;
        }

        int codigo = contadorSequencial++;
        Ingresso novo;
        if (cbTipo.getSelectedIndex() == 1) {
            double valorMeia = Validavel.calcularValorMeia(valorInteira);
            novo = new IngressoMeia(codigo, evento, participante, valorMeia, txtDocumentoMeia.getText().trim());
        } else {
            novo = new IngressoInteira(codigo, evento, participante, valorInteira);
        }

        listaIngressos.add(novo);
        atualizarInterface();
        selecionarLinhaPorCodigo(novo.getCodigo());

        JOptionPane.showMessageDialog(this,
                "Venda registrada com sucesso!\n\n"
                        + "Código: #" + novo.getCodigo() + "\n"
                        + "Tipo: " + novo.getTipo() + "\n"
                        + "Valor: " + novo.formatarValor(novo.getValor()) + "\n"
                        + "Participante: " + novo.getNomeParticipante(),
                "Venda concluída",
                JOptionPane.INFORMATION_MESSAGE);

        limparCamposVenda();
    }

    private void pesquisarIngresso() {
        String codTexto = txtCodigoOperacao.getText().trim();
        String nomeTexto = txtParticipante.getText().trim();

        if (codTexto.isEmpty() && nomeTexto.isEmpty()) {
            exibirAviso("Informe o código do bilhete ou o nome do participante para pesquisar.", "Pesquisa");
            return;
        }

        Ingresso encontrado = null;

        if (!codTexto.isEmpty()) {
            try {
                int codigo = Integer.parseInt(codTexto);
                for (Ingresso ing : listaIngressos) {
                    if (ing.getCodigo() == codigo) {
                        encontrado = ing;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
                exibirErro("Código inválido: '" + codTexto + "'. Digite apenas números inteiros.", "Formato inválido");
                return;
            }
        }

        if (encontrado == null && !nomeTexto.isEmpty()) {
            for (Ingresso ing : listaIngressos) {
                if (ing.getNomeParticipante().toLowerCase().contains(nomeTexto.toLowerCase())) {
                    encontrado = ing;
                    break;
                }
            }
        }

        if (encontrado != null) {
            preencherFormulario(encontrado);
            selecionarLinhaPorCodigo(encontrado.getCodigo());
            JOptionPane.showMessageDialog(this,
                    "Ingresso encontrado:\n\n" + encontrado,
                    "Pesquisa",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nenhum ingresso encontrado com os dados informados.",
                    "Pesquisa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processarAcessoCatraca() {
        Ingresso ingresso = obterIngressoSelecionado();
        if (ingresso == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Conferência na portaria:\n\n"
                        + "Bilhete: #" + ingresso.getCodigo() + " (" + ingresso.getTipo() + ")\n"
                        + "Participante: " + ingresso.getNomeParticipante() + "\n"
                        + "Evento: " + ingresso.getEvento() + "\n"
                        + "Orientação: " + ingresso.obterInstrucaoCatraca() + "\n\n"
                        + "Confirmar liberação na catraca?",
                "Controle de Portaria",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ingresso.validarAcessoCatraca();
            atualizarInterface();
            selecionarLinhaPorCodigo(ingresso.getCodigo());

            JOptionPane.showMessageDialog(this,
                    "Acesso liberado para " + ingresso.getNomeParticipante() + ".\n"
                            + "Bilhete #" + ingresso.getCodigo() + " validado com sucesso.",
                    "Acesso concedido",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IngressoInvalidoException ex) {
            exibirErro(ex.getMessage(), "Acesso negado");
        }
    }

    private void cancelarIngresso() {
        Ingresso ingresso = obterIngressoSelecionado();
        if (ingresso == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente cancelar o ingresso #" + ingresso.getCodigo()
                        + " de " + ingresso.getNomeParticipante() + "?",
                "Confirmar cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ingresso.cancelar();
            atualizarInterface();
            selecionarLinhaPorCodigo(ingresso.getCodigo());

            JOptionPane.showMessageDialog(this,
                    "Ingresso #" + ingresso.getCodigo() + " cancelado com sucesso.",
                    "Cancelamento",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IngressoInvalidoException ex) {
            exibirErro(ex.getMessage(), "Cancelamento negado");
        }
    }

    private void ordenarIngressos() {
        if (listaIngressos.isEmpty()) {
            exibirAviso("Não há ingressos cadastrados para ordenar.", "Ordenação");
            return;
        }

        Collections.sort(listaIngressos);
        atualizarInterface();

        JOptionPane.showMessageDialog(this,
                "Ingressos ordenados por código sequencial.",
                "Ordenação concluída",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirRelatorio() {
        atualizarRelatorioTexto();
        abasVisualizacao.setSelectedIndex(1);
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja encerrar a aplicação?\nOs dados mantidos na sessão atual serão perdidos.",
                "Sair",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void atualizarInterface() {
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

            String horaAcesso = ing.getDataHoraAcesso() != null
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

        kpiTotal.setValor(String.valueOf(total));
        kpiPresentes.setValor(String.valueOf(presentes));
        kpiCancelados.setValor(String.valueOf(cancelados));
        kpiReceita.setValor(String.format("R$ %.2f", receita));

        atualizarRelatorioTexto();
    }

    private void atualizarRelatorioTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================================================\n");
        sb.append("                             RELATÓRIO DE PÚBLICO E BILHETERIA                              \n");
        sb.append("============================================================================================\n\n");

        int total = listaIngressos.size();
        int presentes = 0;
        int cancelados = 0;
        int inteiras = 0;
        int meias = 0;
        double recInteiras = 0.0;
        double recMeias = 0.0;

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
        double taxaPresenca = ativos > 0 ? ((double) presentes / ativos) * 100.0 : 0.0;

        sb.append(String.format("Total de ingressos emitidos: %d\n", total));
        sb.append(String.format("Ingressos válidos:           %d\n", ativos));
        sb.append(String.format("Ingressos cancelados:        %d\n", cancelados));
        sb.append(String.format("Público presente:            %d pessoa(s)\n", presentes));
        sb.append(String.format("Taxa de comparecimento:      %.1f %%\n", taxaPresenca));
        sb.append("--------------------------------------------------------------------------------------------\n");
        sb.append(String.format("Receita (Inteira):           R$ %10.2f (%d)\n", recInteiras, inteiras));
        sb.append(String.format("Receita (Meia-entrada):      R$ %10.2f (%d)\n", recMeias, meias));
        sb.append(String.format("Receita total ativa:         R$ %10.2f\n", recInteiras + recMeias));
        sb.append("============================================================================================\n\n");

        sb.append("INGRESSOS REGISTRADOS:\n");
        sb.append("--------------------------------------------------------------------------------------------\n");
        if (listaIngressos.isEmpty()) {
            sb.append("Nenhum ingresso cadastrado.\n");
        } else {
            for (Ingresso ing : listaIngressos) {
                sb.append(ing).append("\n");
            }
        }
        sb.append("--------------------------------------------------------------------------------------------\n");

        txtRelatorioPublico.setText(sb.toString());
        txtRelatorioPublico.setCaretPosition(0);
    }

    private Ingresso obterIngressoSelecionado() {
        String codTexto = txtCodigoOperacao.getText().trim();
        if (codTexto.isEmpty()) {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                codTexto = String.valueOf(tabela.getValueAt(linha, 0));
                txtCodigoOperacao.setText(codTexto);
            }
        }

        if (codTexto.isEmpty()) {
            exibirAviso("Selecione um ingresso na tabela ou informe o código do bilhete.", "Seleção necessária");
            return null;
        }

        try {
            int codigo = Integer.parseInt(codTexto);
            for (Ingresso ing : listaIngressos) {
                if (ing.getCodigo() == codigo) {
                    return ing;
                }
            }
            exibirAviso("Ingresso #" + codigo + " não encontrado.", "Não localizado");
        } catch (NumberFormatException ex) {
            exibirErro("Código inválido: '" + codTexto + "'.", "Formato inválido");
        }
        return null;
    }

    private void preencherFormulario(Ingresso ing) {
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
        txtDocumentoMeia.setText("Carteira Estudantil / DNE");
        txtEvento.requestFocusInWindow();
    }

    private double converterValor(String texto) {
        String limpo = texto.trim().replace("R$", "").replace(" ", "");
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(',', '.');
        }
        if (limpo.isEmpty()) {
            throw new NumberFormatException();
        }
        return Double.parseDouble(limpo);
    }

    private void exibirAviso(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.WARNING_MESSAGE);
    }

    private void exibirErro(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origem = e.getSource();
        if (origem == btnCadastrar) {
            cadastrarIngresso();
        } else if (origem == btnPesquisar) {
            pesquisarIngresso();
        } else if (origem == btnProcessarOperacao) {
            processarAcessoCatraca();
        } else if (origem == btnCancelar) {
            cancelarIngresso();
        } else if (origem == btnOrdenar) {
            ordenarIngressos();
        } else if (origem == btnRelatorio) {
            abrirRelatorio();
        } else if (origem == btnLimpar) {
            limparCampos();
        } else if (origem == btnSair) {
            confirmarSaida();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        confirmarSaida();
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    private class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public PainelCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(16, 18, 16, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 1;
            int y = 1;
            int w = getWidth() - 3;
            int h = getHeight() - 3;

            g2.setColor(new Color(15, 23, 42, temaAtual.isDark ? 24 : 7));
            g2.fillRoundRect(x, y + 2, w, h, 14, 14);

            g2.setColor(temaAtual.card);
            g2.fillRoundRect(x, y, w, h, 12, 12);

            g2.setColor(temaAtual.borda);
            g2.drawRoundRect(x, y, w, h, 12, 12);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class BotaoModerno extends JButton {
        private static final long serialVersionUID = 1L;

        private Color corFundo;
        private Color corHover;
        private final boolean comBorda;
        private boolean hover;

        public BotaoModerno(String texto, Color fundo, Color frente, boolean comBorda) {
            super(texto);
            this.comBorda = comBorda;
            setEstilo(fundo, frente);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(7, 14, 7, 14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        public void setEstilo(Color fundo, Color frente) {
            this.corFundo = fundo;
            this.corHover = escurecer(fundo, 0.08f);
            setForeground(frente);
            repaint();
        }

        private Color escurecer(Color c, float fator) {
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
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            if (comBorda) {
                g2.setColor(temaAtual.borda);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class CardKPI extends JPanel {
        private static final long serialVersionUID = 1L;

        private final JLabel lblTitulo;
        private final JLabel lblValor;
        private final boolean destaque;

        public CardKPI(String titulo, String valorInicial, boolean destaque) {
            this.destaque = destaque;
            setLayout(new BorderLayout(4, 3));
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));

            lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblTitulo.setForeground(temaAtual.muted);

            lblValor = new JLabel(valorInicial);
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 19));
            lblValor.setForeground(destaque ? temaAtual.primary : temaAtual.texto);

            add(lblTitulo, BorderLayout.NORTH);
            add(lblValor, BorderLayout.CENTER);
        }

        public void setValor(String valor) {
            lblValor.setText(valor);
            atualizarEstilo();
        }

        public void atualizarEstilo() {
            lblTitulo.setForeground(temaAtual.muted);
            lblValor.setForeground(destaque ? temaAtual.primary : temaAtual.texto);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 2;
            int h = getHeight() - 2;

            g2.setColor(new Color(15, 23, 42, temaAtual.isDark ? 20 : 6));
            g2.fillRoundRect(0, 2, w, h, 10, 10);

            g2.setColor(temaAtual.card);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.setColor(temaAtual.borda);
            g2.drawRoundRect(0, 0, w, h, 10, 10);

            g2.setColor(destaque ? temaAtual.primary : temaAtual.borda);
            g2.fillRoundRect(0, 0, 3, h, 3, 3);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private Color pillBg;
        private Color pillFg;
        private String textoStatus;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            textoStatus = value != null ? value.toString() : "";

            if ("Utilizado".equalsIgnoreCase(textoStatus)) {
                pillBg = temaAtual.isDark ? new Color(6, 78, 59) : new Color(236, 253, 245);
                pillFg = temaAtual.isDark ? new Color(167, 243, 208) : new Color(6, 95, 70);
            } else if ("Cancelado".equalsIgnoreCase(textoStatus)) {
                pillBg = temaAtual.isDark ? new Color(69, 26, 26) : new Color(254, 242, 242);
                pillFg = temaAtual.isDark ? new Color(254, 202, 202) : new Color(153, 27, 27);
            } else {
                pillBg = temaAtual.primaryLight;
                pillFg = temaAtual.isDark ? new Color(191, 219, 254) : temaAtual.primary;
            }

            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setOpaque(true);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(textoStatus);
            int pillW = Math.min(getWidth() - 16, textW + 20);
            int pillH = 24;
            int px = (getWidth() - pillW) / 2;
            int py = (getHeight() - pillH) / 2;

            g2.setColor(pillBg);
            g2.fillRoundRect(px, py, pillW, pillH, 12, 12);

            g2.setColor(pillFg);
            int tx = px + (pillW - textW) / 2;
            int ty = py + ((pillH - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(textoStatus, tx, ty);

            g2.dispose();
        }
    }

    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = temaAtual.card;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return criarBotaoVazio();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return criarBotaoVazio();
        }

        private JButton criarBotaoVazio() {
            JButton botao = new JButton();
            botao.setPreferredSize(new Dimension(0, 0));
            return botao;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
            if (bounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() ? new Color(148, 163, 184) : thumbColor);
            g2.fillRoundRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, 6, 6);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
        }
    }
}
