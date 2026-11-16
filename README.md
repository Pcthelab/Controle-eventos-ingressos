# Sistema de Controle de Eventos e Venda de Ingressos (Swing + Java Collections)

Projeto Prático referente à **Avaliação P2** da disciplina de **Programação Orientada a Objetos (POO)**.  
**Professor:** Prof. Mestre Marcos Roberto de Moraes (Maromo)  
**Instituição:** FATEC Mogi Mirim  
**Materiais de Referência:** POO_07 a POO_10  

---

## 👥 1. Identificação do(s) Aluno(s)

- **Integrantes:**
  - **Nome:** Juan Diego [Completar Sobrenome se desejar] — **RA:** [Preencher RA]
  - *(Trabalho realizado individualmente / em dupla conforme autorização das diretrizes)*
- **Curso:** Análise e Desenvolvimento de Sistemas / Gestão em TI
- **Disciplina:** Programação Orientada a Objetos (POO)

---

## 🎟️ 2. Tema Escolhido

- **Tema Nº 08:** **Controle de Eventos e Venda de Ingressos**
- **Entidade Principal (`Comparable`):** `Ingresso` (ordenação natural por código sequencial do bilhete com `Collections.sort()`).
- **Interface de Negócio Obrigatória:** `Validavel` (método `validarAcessoCatraca()` acompanhado de métodos `default` e `static` do Java moderno).
- **Checked Exception Customizada:** `IngressoInvalidoException` (herda de `java.lang.Exception`, nos moldes da `SemSaldoException` do material POO_08).
- **Operações em Memória (`List<T>`):**
  - Venda de bilhetes (inteira e meia-entrada com cálculo automático de 50%);
  - Conferência na portaria com baixa via catraca polimórfica;
  - Cancelamento de ingressos com validação de elegibilidade;
  - Relatório consolidado de público e bilheteria em `JTextArea`.

---

## 🏛️ 3. Arquitetura do Software e Estrutura de Pacotes

O projeto segue rigorosamente a padronização e boas práticas vistas em aula e solicitadas na página 3 das diretrizes:

```
src/
└── br/
    └── com/
        └── eventos/
            ├── Principal.java                     # Ponto de entrada (main) com Look and Feel Nimbus
            ├── exception/
            │   └── IngressoInvalidoException.java # Checked Exception customizada (herda de Exception)
            ├── interfaces/
            │   └── Validavel.java                 # Interface com método abstrato, default e static
            ├── model/
            │   ├── Ingresso.java                  # Entidade principal abstrata (Comparable, equals, hashCode)
            │   ├── IngressoInteira.java           # Subclasse para ingressos inteiros
            │   └── IngressoMeia.java              # Subclasse para meia-entrada (comprovante estudantil)
            └── view/
                └── TelaEventos.java               # Interface gráfica Swing (JFrame, Eventos e List em RAM)
```

### Detalhamento das Classes e Requisitos Técnicos Atendidos:

1. **`br.com.eventos.interfaces.Validavel`:**
   - **Método Abstrato:** `boolean validarAcessoCatraca() throws IngressoInvalidoException;`
   - **Método Default:** `default String formatarValor(double valor)` (formatação monetária `R$ 0,00`).
   - **Método Default:** `default String obterInstrucaoCatraca()` (orientações específicas para a portaria).
   - **Método Static:** `static double calcularValorMeia(double valorInteira)` (aplica a regra de 50% de desconto).

2. **`br.com.eventos.exception.IngressoInvalidoException`:**
   - Checked Exception customizada estendendo `java.lang.Exception`.
   - Disparada ao tentar:
     - Realizar acesso com ingresso cancelado;
     - Realizar acesso duplo com ingresso já utilizado na catraca;
     - Cancelar um ingresso que já foi utilizado na portaria;
     - Cancelar um bilhete já cancelado anteriormente.
   - Mensagens de erro são capturadas e exibidas amigavelmente via `JOptionPane.showMessageDialog()`.

3. **`br.com.eventos.model.Ingresso`:**
   - Entidade principal contendo campos privados: `codigo`, `evento`, `nomeParticipante`, `valor`, `utilizado`, `cancelado`, `dataHoraAcesso`.
   - Construtores sobrecarregados, getters e setters com encapsulamento rigoroso.
   - **`Comparable<Ingresso>`:** sobrescreve `compareTo()` para permitir a ordenação natural da lista em memória por código sequencial com `Collections.sort()`.
   - **`equals()` e `hashCode()`:** sobrescritos considerando a unicidade do código do bilhete no evento.
   - **`toString()`:** representação textual legível formatada para geração de relatórios.

4. **`br.com.eventos.model.IngressoInteira` e `IngressoMeia` (Polimorfismo por Herança e Interface):**
   - `IngressoInteira`: acesso padrão e conferência simples de identidade na portaria.
   - `IngressoMeia`: especializa a regra armazenando o `documentoComprovante` (ex: Carteirinha de Estudante) e personalizando a validação na catraca (`obterInstrucaoCatraca()` e `validarAcessoCatraca()`).

5. **`br.com.eventos.view.TelaEventos`:**
   - Estende `javax.swing.JFrame` e implementa `ActionListener` e `WindowListener`.
   - Mantém todos os dados 100% em memória RAM: `private final List<Ingresso> listaIngressos = new ArrayList<>()`.
   - Contém todos os botões e componentes solicitados na Pág. 3 do PDF:
     - **Cadastrar (Venda):** registra nova emissão de bilhete com validações e captura de `NumberFormatException`;
     - **Pesquisar:** busca bilhete por código ou nome do participante;
     - **Processar Operação (Catraca):** realiza a conferência e baixa de acesso do participante;
     - **Cancelar Ingresso:** cancela bilhetes elegíveis com confirmação;
     - **Ordenar (Código):** executa `Collections.sort(listaIngressos)` e atualiza a interface;
     - **Relatório de Público:** gera estatísticas consolidadas no painel de texto;
     - **Limpar:** reseta os campos do formulário;
     - **Sair:** fecha a aplicação com diálogo de confirmação.
   - Exibição em abas integradas:
     - **Aba 📋 Tabela Dinâmica (`JTable`):** com ordenação visual, seleção e preenchimento de campos;
     - **Aba 📊 Relatório de Público (`JTextArea` dentro de `JScrollPane`):** extrato estatístico completo (público presente, receita total, taxa de comparecimento, listagem detalhada dos bilhetes).
   - Fechamento seguro interceptado por `windowClosing` via `WindowListener`.

6. **`br.com.eventos.Principal`:**
   - Contém o método `public static void main(String[] args)` que configura o Look and Feel Nimbus e exibe a tela na fila de eventos do Swing (`SwingUtilities.invokeLater`).

---

## ⚙️ 4. Instruções de Compilação e Execução

### Pré-requisitos:
- **Java JDK 17** ou superior instalado (projeto testado e homologado no Java 21).

### Execução em 1 Clique (Windows):
Basta dar duplo clique no arquivo `executar.bat` presente na raiz do projeto, ou executar via terminal:
```bat
.\executar.bat
```

### Compilação e Execução Manual via Terminal:
```bat
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\br\com\eventos\interfaces\*.java src\br\com\eventos\exception\*.java src\br\com\eventos\model\*.java src\br\com\eventos\view\*.java src\br\com\eventos\*.java
java -cp bin br.com.eventos.Principal
```

---

## 🤖 5. Histórico e Registro de Prompts de IA Generativa

Em cumprimento à **Regra 2 (Uso de IA Generativa e Registro de Prompts)** estipulada no documento oficial da P2 pelo Prof. Maromo, segue o histórico transparente das interações:

### 🔹 Interação 1: Análise e Adequação ao Tema 08
- **O que foi solicitado à IA:**
  > "Estou realizando a P2 com o Tema 08: Controle de Eventos e Venda de Ingressos. Faça um review completo do código existente no diretório `d:\Projetos_Pessoais\Controle-eventos-ingressos` e me aponte tudo o que falta para atender 100% aos requisitos dos materiais POO_07 a POO_10 do Prof. Maromo."
- **Resposta obtida:**
  A IA identificou que:
  1. A interface `Validavel` continha apenas o método abstrato, faltando métodos `default` e `static` exigidos pelo requisito 7 e página 3 do PDF;
  2. A classe `Ingresso` não sobrescrevia `equals()`, `hashCode()` nem `toString()`, além de não possuir os setters conforme padrão JavaBean;
  3. Faltava o botão explícito "Ordenar" acionando `Collections.sort()` com feedback ao usuário, bem como os nomes dos botões de ação exigidos na página 3 (`Cadastrar`, `Pesquisar`, `Processar Operação`, `Ordenar`, `Sair`);
  4. Faltava a área de texto `JTextArea` dentro de `JScrollPane` para emissão do relatório consolidado de público;
  5. O `README.md` não continha a identificação dos alunos nem o detalhamento dos prompts.
- **Refinamento aplicado:**
  Aprovadas as melhorias e autorizada a refatoração completa do projeto.

---

### 🔹 Interação 2: Implementação de Polimorfismo e Métodos na Interface
- **O que foi solicitado à IA:**
  > "Atualize a interface `Validavel` com métodos default (formatação de moeda e instruções da catraca) e método static para cálculo de meia-entrada. Atualize `IngressoInteira` e `IngressoMeia` para demonstrar polimorfismo real de regras de negócio, onde a meia exige documento comprobatório."
- **Resposta obtida:**
  A IA reestruturou a interface com `formatarValor()`, `obterInstrucaoCatraca()` e `calcularValorMeia()`. Na classe `IngressoMeia`, foi adicionado o atributo `documentoComprovante` e a sobrescrita de `validarAcessoCatraca()` e `obterInstrucaoCatraca()`, especializando o comportamento na portaria.
- **Refinamento aplicado:**
  Garantimos que o construtor sobrecarregado de `IngressoMeia` mantivesse um valor padrão caso nenhum documento fosse digitado, evitando exceções indevidas em vendas rápidas.

---

### 🔹 Interação 3: Interface Gráfica Swing com Abas, Botões Canônicos e Relatório em JTextArea
- **O que foi solicitado à IA:**
  > "Refatore a `TelaEventos` para implementar diretamente `ActionListener` e `WindowListener`. Mantenha a tabela de ingressos que é muito visual, mas adicione uma aba com `JTextArea` para o Relatório de Público detalhado. Adicione os botões com os nomes exatos exigidos pelo professor e garanta tratamento de `NumberFormatException` e da checked exception via `JOptionPane`."
- **Resposta obtida:**
  A IA criou um `JTabbedPane` com duas abas: a tabela visual e o relatório em texto monospaçado. Incluiu os botões `Cadastrar (Venda)`, `Pesquisar`, `Processar Operação (Catraca)`, `Cancelar Ingresso`, `Ordenar (Código)`, `Relatório de Público`, `Limpar` e `Sair`, com tratamento de eventos nos métodos canônicos.
- **Refinamento aplicado:**
  Inseridos 3 registros de demonstração na inicialização da aplicação (um disponível, um utilizado e um cancelado) para que o professor consiga testar imediatamente a ordenação, a portaria e o relatório logo ao iniciar o programa.

---

### 🔹 Interação 4: Testes Automatizados e Homologação
- **O que foi solicitado à IA:**
  > "Escreva um teste de integração das regras de negócio para verificar se `Collections.sort()` ordena corretamente pelo código, se `equals()` e `hashCode()` funcionam com as subclasses polimórficas, e se a `IngressoInvalidoException` é disparada ao tentar reutilizar ingresso ou entrar com ingresso cancelado."
- **Resposta obtida:**
  A IA executou os testes via terminal e detectou uma sutileza no `equals()`, que comparava `getClass() != o.getClass()` entre `IngressoMeia` e `IngressoInteira`. A IA refinou o método para usar `instanceof Ingresso`, permitindo a identificação polimórfica pelo código e evento. Todos os testes passaram com 100% de sucesso.
- **Refinamento aplicado:**
  Atualização do arquivo `README.md` com o histórico consolidado e compilação final bem-sucedida.
