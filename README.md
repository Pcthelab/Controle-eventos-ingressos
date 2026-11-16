# Sistema de Estacionamento Rotativo em Memória (Swing + Java Collections)

Projeto Prático referente à **Avaliação P2** da disciplina de **Programação Orientada a Objetos (POO)**.  
**Professor:** Prof. Mestre Marcos Roberto de Moraes (Maromo)  
**Instituição:** FATEC Mogi Mirim  
**Materiais de Referência:** POO_07 a POO_10  

---

## 👥 1. Identificação do(s) Aluno(s)

- **Integrantes da Dupla / Aluno:**
  - **Nome Completo:** Juan Diego [Preencher se aplicável] — **RA:** [Preencher RA]
  - **Nome Completo:** [Preencher nome da dupla, se houver] — **RA:** [Preencher RA]
- **Turma:** Análise e Desenvolvimento de Sistemas / Informática para Negócios
- **Semestre:** 2º Semestre

---

## 📌 2. Tema Escolhido

- **Tema Nº 03:** **Estacionamento Rotativo**
- **Entidade Principal (`Comparable`):** `TicketEstacionamento` (ordenação natural por horário de entrada com `Collections.sort()`).
- **Interface de Negócio Obrigatória:** `Tarifavel` (método abstrato `calcularPrecoEstadia(long minutosPermanencia)` acompanhado de métodos `default` e `static` do Java moderno).
- **Checked Exception Customizada:** `VagaNaoDisponivelException` (herda de `java.lang.Exception`, disparada quando o pátio atinge a capacidade máxima).
- **Operações em Memória (`List<T>`):**
  - Registro de entrada de veículo;
  - Cálculo polimórfico e baixa de estadia;
  - Liberação de vaga no pátio;
  - Extrato dos veículos e relatório ordenado.

---

## 🏛️ 3. Arquitetura do Software e Estrutura de Pacotes

A aplicação foi estruturada rigorosamente segundo a separação em camadas e boas práticas de POO solicitadas no documento oficial:

```
src/
└── br/
    └── com/
        └── estacionamento/
            ├── Principal.java                     # Ponto de entrada (main) e inicialização da GUI
            ├── interfaces/
            │   └── Tarifavel.java                 # Interface com contrato de cálculo e métodos default/static
            ├── exception/
            │   └── VagaNaoDisponivelException.java # Checked Exception customizada (herda de Exception)
            ├── model/
            │   ├── TicketEstacionamento.java       # Entidade principal abstrata (Comparable, equals, hashCode)
            │   ├── TicketCarro.java               # Subclasse especializada para Automóveis
            │   └── TicketMoto.java                # Subclasse especializada para Motocicletas
            └── view/
                └── TelaEstacionamento.java        # Interface gráfica javax.swing.* (JFrame, Eventos e List)
```

### Detalhamento das Classes:

1. **`br.com.estacionamento.interfaces.Tarifavel`:**
   - Contrato contendo o método abstrato `double calcularPrecoEstadia(long minutosPermanencia)`.
   - Método `default` `String formatarValor(double valor)` para formatação em Real (`R$ 0,00`).
   - Método `default` `double aplicarDescontoConvenio(...)` para suporte a regras promocionais.
   - Método `static` `int obterToleranciaMinutos()` retornando 15 minutos de tolerância sem cobrança.

2. **`br.com.estacionamento.exception.VagaNaoDisponivelException`:**
   - Checked Exception customizada (estende `java.lang.Exception`) nos moldes de `SemSaldoException` (POO_08).
   - Lançada na tentativa de registrar a entrada de veículos quando o número de veículos ativos no pátio atinge o teto estipulado (`CAPACIDADE_MAXIMA = 5`).
   - A captura é tratada visualmente devolvendo alerta ao usuário com `JOptionPane.showMessageDialog()`.

3. **`br.com.estacionamento.model.TicketEstacionamento`:**
   - Entidade principal contendo atributos privados: `numero`, `placa`, `modelo`, `horaEntrada`, `horaSaida`, `valorPago`, `finalizado`.
   - Construtores sobrecarregados, getters e setters com encapsulamento rigoroso.
   - **`Comparable<TicketEstacionamento>`:** sobrescreve `compareTo()` ordenando pelo `horaEntrada` (horário de entrada), atendendo estritamente ao requisito do tema.
   - **`equals()` e `hashCode()`:** sobrescritos comparando `numero` do ticket e `placa`.
   - **`toString()`:** representação textual legível formatada para exibição em extratos.

4. **`br.com.estacionamento.model.TicketCarro` e `TicketMoto` (Polimorfismo por Interface):**
   - Subclasses concretas herdando de `TicketEstacionamento` e implementando a precificação:
     - **Carro:** 15 min de tolerância grátis; R$ 12,00 na 1ª hora; R$ 6,00 a cada hora (ou fração) adicional.
     - **Moto:** 15 min de tolerância grátis; R$ 7,00 na 1ª hora; R$ 3,50 a cada hora (ou fração) adicional.

5. **`br.com.estacionamento.view.TelaEstacionamento`:**
   - Estende `javax.swing.JFrame` e implementa `ActionListener` e `WindowListener`.
   - Mantém o armazenamento 100% em memória RAM por meio de `private final List<TicketEstacionamento> listaTickets = new ArrayList<>()`.
   - Possui formulários com `JTextField`, `JComboBox`, `JLabel`, painel com status em tempo real das vagas e área de texto `JTextArea` dentro de `JScrollPane`.
   - Botões obrigatórios implementados:
     - **Cadastrar (Entrada):** valida campos e vagas, instancia polimorficamente o ticket e armazena na lista em memória;
     - **Pesquisar:** pesquisa por número do ticket ou por placa, tratando `NumberFormatException`;
     - **Processar Operação (Baixa):** calcula o valor devido polimorficamente através de `ticket.calcularPrecoEstadia(...)`, emite o recibo, dá baixa e libera a vaga no pátio;
     - **Ordenar:** invoca `Collections.sort(listaTickets)` disparando a ordenação natural por horário de entrada;
     - **Sair:** exibe caixa de diálogo de confirmação antes de encerrar o programa.
   - Tratamento de fechamento de janela com `windowClosing` interceptando o clique no "X" e solicitando confirmação.

6. **`br.com.estacionamento.Principal`:**
   - Ponto de entrada (`main`) que define o Look and Feel Nimbus e instancia `new TelaEstacionamento().setVisible(true)` na fila de eventos do Swing (`SwingUtilities.invokeLater`).

---

## ⚙️ 4. Instruções de Compilação e Execução

### Pré-requisitos:
- **Java JDK 17** ou superior instalado (projeto testado e validado no Java 21).

### Execução Rápida via Script (Windows):
Basta dar um duplo clique no arquivo `executar.bat` presente na raiz do repositório, ou executar via PowerShell/Prompt de Comando:
```bash
.\executar.bat
```

### Compilação e Execução Manual via Terminal:
1. Abra o terminal na pasta raiz do projeto (`estacionamento-rotativo/`);
2. Crie o diretório de classes compiladas:
   ```bash
   mkdir bin
   ```
3. Compile todos os arquivos `.java`:
   ```bash
   javac -encoding UTF-8 -d bin src/br/com/estacionamento/interfaces/*.java src/br/com/estacionamento/exception/*.java src/br/com/estacionamento/model/*.java src/br/com/estacionamento/view/*.java src/br/com/estacionamento/*.java
   ```
4. Execute o programa:
   ```bash
   java -cp bin br.com.estacionamento.Principal
   ```

---

## 🤖 5. Histórico e Registro de Prompts de IA Generativa

Em conformidade com a **Regra 2 (Uso de IA Generativa e Registro de Prompts)** descrita nas diretrizes do trabalho, registramos a seguir todo o processo de interação, os pedidos efetuados, as soluções obtidas e os refinamentos realizados.

### 🔹 Interação 1: Definição de Arquitetura e Seleção de Tema
- **O que foi pedido à IA:**
  > "Estou iniciando a P2 da disciplina de Programação Orientada a Objetos com o Prof. Maromo. Seguem as regras do PDF: preciso escolher 1 dos 10 temas, trabalhar 100% em memória RAM com `List<Entidade> = new ArrayList<>()`, interface Swing com botões específicos, Checked Exception customizada, `Comparable<T>` com `Collections.sort()`, `equals()` e `hashCode()`, e interface de negócio com métodos abstratos e default/static. Qual o melhor tema e como modelar a arquitetura?"
- **Resposta obtida:**
  A IA analisou os 10 temas propostos e sugeriu como melhor escolha o **Tema 03: Estacionamento Rotativo** (`TicketEstacionamento`, `Tarifavel`, `VagaNaoDisponivelException`). A justificativa foi a clareza prática das regras de negócio, a facilidade de simular permanência de veículos, e a elegância de demonstrar polimorfismo entre categorias de veículos (ex: Carros vs Motos) aplicando tarifas diferenciadas.
- **Refinamento aplicado:**
  Aprovamos a escolha do Tema 03 e definimos que a capacidade máxima de vagas no pátio seria delimitada para 5 veículos ativos, permitindo disparar e testar facilmente a `VagaNaoDisponivelException` durante a apresentação/avaliação.

---

### 🔹 Interação 2: Estruturação dos Modelos e Polimorfismo
- **O que foi pedido à IA:**
  > "Gere a interface `Tarifavel` contendo método abstrato de cálculo e métodos default e static do Java moderno. Em seguida crie a entidade abstrata `TicketEstacionamento` implementando `Comparable` (ordenando pelo horário de entrada), `equals`, `hashCode` e `toString`, e duas subclasses: `TicketCarro` e `TicketMoto` com tarifas e tolerância."
- **Resposta obtida:**
  A IA gerou as classes com encapsulamento completo dos atributos, tratamento de nulos em `compareTo`, cálculo com arredondamento fracionário de horas e método de formatação monetária padrão `R$ %.2f`.
- **Refinamento aplicado:**
  Ajustamos a visibilidade do formatador de data (`FORMATADOR_DATA_HORA`) para permitir que a classe de visão (`TelaEstacionamento`) também reutilizasse a mesma máscara na montagem das colunas do relatório, garantindo alinhamento uniforme.

---

### 🔹 Interação 3: Interface Gráfica javax.swing.* e Eventos
- **O que foi pedido à IA:**
  > "Construa o formulário Swing `TelaEstacionamento` estendendo `JFrame` e implementando diretamente `ActionListener` e `WindowListener`. A tela deve possuir: campos para placa, modelo, tipo de veículo, botões `Cadastrar`, `Pesquisar`, `Processar Operação`, `Ordenar`, `Limpar` e `Sair`, exibição em tempo real de vagas e área de texto `JTextArea` dentro de `JScrollPane`. As mensagens de erro e alertas devem usar `JOptionPane.showMessageDialog()`."
- **Resposta obtida:**
  A IA entregou o código com os layouts `BorderLayout`, `GridLayout` e `FlowLayout`, tratamento de eventos nos botões com `e.getSource()`, confirmação de saída via `JOptionPane.showConfirmDialog` e captura de exceções `NumberFormatException` e `VagaNaoDisponivelException`.
- **Refinamento aplicado:**
  Adicionamos um campo extra facultativo chamado "Minutos de Estadia (Simulação para Baixa)". Isso permite ao professor/avaliador tanto testar o tempo real decorrido quanto digitar valores manuais (ex: 45 min, 90 min, 180 min) para validar de imediato as faixas de cobrança e tolerância sem precisar aguardar horas com o programa aberto.

---

### 🔹 Interação 4: Validação de Compilação e Testes
- **O que foi pedido à IA:**
  > "Valide a compilação do projeto com o JDK instalado e elabore um teste unitário de regras de negócio validando ordenação por horário, polimorfismo de cálculo e o disparo da checked exception."
- **Resposta obtida:**
  O projeto compilou com sucesso sem nenhum aviso, e o teste automatizado confirmou que:
  - 10 minutos permaneceu isento (R$ 0,00) pela tolerância de 15 minutos;
  - Carro pagou R$ 12,00 por 45 min e R$ 18,00 por 90 min;
  - Moto pagou R$ 7,00 por 45 min e R$ 10,50 por 90 min;
  - A ordenação cronológica colocou os tickets na sequência correta de entrada;
  - A `VagaNaoDisponivelException` foi devidamente disparada quando atingida a lotação máxima.
- **Refinamento aplicado:**
  Geração do script `executar.bat` com codificação UTF-8 (`chcp 65001`) para evitar problemas com caracteres acentuados no terminal do Windows.
