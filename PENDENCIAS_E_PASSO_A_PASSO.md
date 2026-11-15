# Pendências e passo a passo para concluir a GS de POO

**Integrantes:** Gregory Debom Ferreira (RM 562346) · Luiz Henrique Barbosa Dias (RM 562399)
**Projeto:** `territory-predictive-insights-fev/` (nesta mesma pasta)

## ✅ O que já está pronto e testado

- Todo o código Java pedido no PDF: domínio, exceções, JDBC + H2, Factory/Singleton/Builder, menu e serviço.
- **28 testes passando** (`.\mvnw.cmd test`), com AAA, `assertThrows` e Mockito.
- Programa executado de ponta a ponta. Os dados **sobrevivem ao reiniciar**: testado com duas sessões seguidas.
- Bônus: exportação do histórico para CSV.
- `README.md` com tudo que o PDF pede (público, decisões, padrões justificados, reflexão crítica, uso de IA, divisão de tarefas).
- Maven Wrapper incluído: o professor só precisa de Java 17+.

## ⚠️ O que eu não consegui fazer, porque depende de vocês

| # | Pendência | Por que eu não fiz |
|---|---|---|
| 1 | Criar o repositório no GitHub e enviar o código | Precisa da conta de vocês, e publicar é uma ação que vocês devem decidir |
| 2 | **Commits dos dois integrantes** | O PDF exige commits de todos; cada um precisa commitar com a própria conta |
| 3 | Completar as seções **"Uso de IA"** e **"Divisão de tarefas"** do README com a verdade | Só vocês sabem como revisaram e quem fez o quê |
| 4 | **Estudar o código** para a conversa de correção | O PDF avisa: qualquer integrante pode ser chamado a explicar qualquer trecho |

---

## Passo a passo

### Passo 1: Conferir que tudo roda na máquina de vocês (5 min)
Abra um terminal **dentro da pasta** `territory-predictive-insights-fev` (no VS Code: botão direito na pasta → *Open in Integrated Terminal*):

```powershell
java -version                 # precisa ser 17 ou mais novo
.\mvnw.cmd clean package      # deve terminar com "BUILD SUCCESS" e "Tests run: 28, Failures: 0"
java -jar target\territory-predictive-insights.jar
```

No menu: opção **1** com **12** ciclos → opção **2** → opção **5** → **0**. Abra de novo e use a opção **3 → 1** para ver que o histórico continua lá.

> Pelo VS Code também dá: abra `src/main/java/SistemaMonitoramento.java` e clique em **Run** acima do `main`, ou rode os testes pelo ícone de **tubo de ensaio (Testing)** na barra lateral.

### Passo 2: Ajustar o README (10 min)
Em `territory-predictive-insights-fev/README.md`:
1. **Seção 6, "Uso de IA":** troque o texto de exemplo em itálico pelo que vocês realmente fizeram para revisar (ex.: "lemos cada classe, rodamos os testes, mudamos o cálculo da média para ver o teste falhar...").
2. **Seção 7, "Divisão de tarefas":** ajuste a tabela para refletir quem fez o quê.

### Passo 3: Criar o repositório no GitHub (Gregory, 10 min)
1. Acesse https://github.com/new
2. Nome sugerido: `territory-predictive-insights-fev` · **Public** (ou privado, adicionando o professor) · **não** marque "Add a README" (já existe um).
3. Clique em **Create repository** e copie a URL (ex.: `https://github.com/SEU_USUARIO/territory-predictive-insights-fev.git`).
4. No terminal, dentro da pasta do projeto:

```powershell
git init
git config user.name "Gregory Debom Ferreira"
git config user.email "SEU_EMAIL_DO_GITHUB"
git add .
git commit -m "Estrutura do projeto: domínio, exceções, persistência JDBC e serviço de monitoramento"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/territory-predictive-insights-fev.git
git push -u origin main
```

> O `.gitignore` já impede que `target/`, `dados/` (banco local) e `exportacoes/` subam para o GitHub.

5. No GitHub: **Settings → Collaborators → Add people** e adicione o usuário do Luiz.

### Passo 4: Commit do Luiz (Luiz, 10 min)
```powershell
git clone https://github.com/SEU_USUARIO/territory-predictive-insights-fev.git
cd territory-predictive-insights-fev
git config user.name "Luiz Henrique Barbosa Dias"
git config user.email "EMAIL_DO_GITHUB_DO_LUIZ"
# faça a parte dele (ex.: ajustar a seção "Divisão de tarefas" ou "Uso de IA" do README)
git add README.md
git commit -m "Atualiza divisão de tarefas e uso de IA no README"
git push
```

> O ideal é que cada um commite as partes que explicou/revisou, com mensagens claras. Commits pequenos e frequentes contam mais do que um commit gigante.

### Passo 5: Estudar para a conversa de correção (o mais importante)
Cada integrante deve saber explicar, com o código aberto:

| Pergunta provável | Onde olhar |
|---|---|
| Onde está a classe abstrata e qual o método abstrato? | `dominio/ComponenteTerritorial.java` → `verificarStatus()` |
| Mostre o polimorfismo das previsões. Onde está o `super()`? | `PrevisaoMediaMovel.prever()` e `PrevisaoComparacao.prever()` chamam `super.prever(historico)` |
| Como a estratégia é escolhida sem `if`? | `servico/FabricaPrevisao.java`: mapa nome → construtor |
| Por que Singleton no `ConfiguracaoSistema`? | README, seção 3.4 |
| Onde a `DadosInvalidosException` é lançada e capturada? | lançada em `TipoSensor.validar()`; capturada em `ServicoMonitoramento.lerESalvar()` e no `switch` do menu |
| Por que `PreparedStatement` e não concatenar String? | evita SQL Injection e erros de aspas (`persistencia/MedicaoDAO.java`) |
| O que o try-with-resources faz? | fecha `Connection`, `PreparedStatement` e `ResultSet` automaticamente, mesmo com erro |
| Como o teste com Mockito funciona sem banco? | `ServicoMonitoramentoTest`: `@Mock MedicaoDAO`, `when(...).thenReturn(...)`, `verify(alertaDAO).salvar(...)` |
| Quando o sistema se cala? | `EstrategiaPrevisao.prever()`: menos leituras que o mínimo → `ResultadoPrevisao.insuficiente` |

**Exercício recomendado:** em `PrevisaoMediaMovel`, troque `media(janela)` por `media(janela) + 1`, rode `.\mvnw.cmd test`, veja os testes ficarem vermelhos e desfaça a alteração. Isso prova que os testes são "de verdade" e ajuda a explicar.

### Passo 6: Entregar
- Envie o **link do GitHub** na plataforma da FIAP (só um integrante entrega, indicando o outro).
- Antes de entregar, clone o repositório em outra pasta e rode o Passo 1 de novo. Se não compilar seguindo o README, a equipe **perde 3 pontos**.

---

## Possíveis problemas e soluções

| Sintoma | Solução |
|---|---|
| `'java' não é reconhecido` | Instale o JDK 17+ (ex.: https://adoptium.net) e reabra o terminal |
| `mvnw.cmd não é reconhecido` | Use `.\mvnw.cmd` (com `.\` na frente) e confira se está na pasta do `pom.xml` |
| `Database may be already in use` | Já existe outra janela do sistema aberta. Feche-a (o H2 em arquivo aceita um programa por vez) |
| Acentos estranhos no terminal | Use o terminal do VS Code ou o Windows Terminal; o programa ajusta a codificação ao console |
| Quero zerar o histórico | Apague a pasta `dados/` dentro do projeto |
| Primeira compilação demorando | Normal: o wrapper baixa o Maven e as bibliotecas uma única vez (precisa de internet) |
