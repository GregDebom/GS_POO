import dominio.Alerta;
import dominio.DadosTerritorio;
import dominio.EstrategiaPrevisao;
import dominio.Limiares;
import dominio.Medicao;
import dominio.Pluviometro;
import dominio.PontoMonitoramento;
import dominio.ResultadoPrevisao;
import dominio.Sensor;
import dominio.SensorNivelCorrego;
import dominio.SensorSimulado;
import dominio.SensorUmidadeSolo;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import persistencia.AlertaDAO;
import persistencia.ConexaoBanco;
import persistencia.MedicaoDAO;
import servico.ConfiguracaoSistema;
import servico.ExportadorCsv;
import servico.FabricaPrevisao;
import servico.ResultadoLeitura;
import servico.RodadaPrevisao;
import servico.ServicoMonitoramento;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Territory Predictive Insights - monitoramento de chuva e risco de alagamento.
 * Menu principal de console (loop com Scanner).
 *
 * Integrantes: Gregory Debom Ferreira (RM 562346) e Luiz Henrique Barbosa Dias (RM 562399).
 */
public class SistemaMonitoramento {

    private static final int MAX_LINHAS_HISTORICO = 50;

    private final ServicoMonitoramento servico;
    private final List<SensorSimulado> sensoresSimulados;
    private final Scanner entrada;
    private final ExportadorCsv exportador = new ExportadorCsv();

    public SistemaMonitoramento(ServicoMonitoramento servico, List<SensorSimulado> sensoresSimulados, Scanner entrada) {
        this.servico = servico;
        this.sensoresSimulados = sensoresSimulados;
        this.entrada = entrada;
    }

    public static void main(String[] args) {
        Charset charsetConsole = configurarAcentosNoConsole();
        System.out.println("Iniciando o Territory Predictive Insights...");
        try (Scanner entrada = new Scanner(System.in, charsetConsole)) {
            ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstance();
            SistemaMonitoramento sistema = montar(configuracao, entrada);
            sistema.executar();
        } catch (FonteIndisponivelException e) {
            System.err.println("[ERRO] O banco de dados não está disponível: " + e.getMessage());
            System.err.println("Dica: verifique se outra janela do sistema já está aberta (o H2 em arquivo "
                    + "aceita um programa por vez) e rode a partir da pasta do projeto.");
            System.exit(1);
        } catch (DadosInvalidosException e) {
            System.err.println("[ERRO] Configuração inicial inválida: " + e.getMessage());
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println("[ERRO] Problema no config.properties: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * No Windows o terminal usa uma página de código própria (ex.: CP850). Escrevendo
     * com o charset do console, os acentos aparecem certos também no JDK 17.
     */
    private static Charset configurarAcentosNoConsole() {
        Console console = System.console();
        Charset charset = console != null ? console.charset() : Charset.defaultCharset();
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, charset));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, charset));
        return charset;
    }

    /** Cria o banco, os DAOs, os sensores, o território e o serviço. */
    private static SistemaMonitoramento montar(ConfiguracaoSistema config, Scanner entrada)
            throws FonteIndisponivelException, DadosInvalidosException {
        ConexaoBanco conexao = new ConexaoBanco(config.valor("db.url", "jdbc:h2:file:./dados/territorio"),
                config.valor("db.usuario", "sa"), config.valor("db.senha", ""));
        conexao.inicializarEsquema();

        Random random = new Random();
        Pluviometro chuvaBaixada = new Pluviometro("PLV-01", "Pluviômetro da Baixada", "Baixada do Água Rasa", random);
        SensorNivelCorrego nivelCorrego = new SensorNivelCorrego("NIV-01", "Régua do Córrego Água Rasa",
                "Baixada do Água Rasa", random);
        Pluviometro chuvaMorro = new Pluviometro("PLV-02", "Pluviômetro do Morro", "Encosta do Morro Verde", random);
        SensorUmidadeSolo umidadeEncosta = new SensorUmidadeSolo("UMI-01", "Umidade da Encosta",
                "Encosta do Morro Verde", random);

        PontoMonitoramento baixada = new PontoMonitoramento("PT-01", "Baixada do Córrego Água Rasa",
                "Baixada do Água Rasa", "alagamento de ruas e casas quando o córrego transborda");
        baixada.adicionarSensor(chuvaBaixada);
        baixada.adicionarSensor(nivelCorrego);
        PontoMonitoramento encosta = new PontoMonitoramento("PT-02", "Encosta do Morro Verde",
                "Encosta do Morro Verde", "enxurrada e deslizamento com o solo saturado");
        encosta.adicionarSensor(chuvaMorro);
        encosta.adicionarSensor(umidadeEncosta);

        DadosTerritorio territorio = new DadosTerritorio(
                config.valor("territorio.nome", "Território monitorado"),
                config.valor("territorio.regiao", "Região não informada"),
                config.valorInteiro("territorio.populacao", 0),
                config.valorInteiro("territorio.domicilios", 0),
                config.valor("territorio.codigoAcesso", "fiap2026"));

        ServicoMonitoramento servico = new ServicoMonitoramento(new MedicaoDAO(conexao), new AlertaDAO(conexao),
                List.of(baixada, encosta), territorio, config, new FabricaPrevisao());
        System.out.println("Banco de dados: " + conexao.getUrl());
        return new SistemaMonitoramento(servico, List.of(chuvaBaixada, nivelCorrego, chuvaMorro, umidadeEncosta),
                entrada);
    }

    // =================================================================== menu principal

    public void executar() {
        System.out.println();
        System.out.println("##############################################################");
        System.out.println("#   TERRITORY PREDICTIVE INSIGHTS - chuva e alagamento        #");
        System.out.println("#   " + servico.getTerritorio().getNomeTerritorio());
        System.out.println("##############################################################");
        boolean continuar = true;
        while (continuar) {
            System.out.println();
            System.out.println("=============== MENU PRINCIPAL ===============");
            System.out.println("1 - Verificar sensores (ler e gravar medições)");
            System.out.println("2 - Rodar previsões e emitir alertas");
            System.out.println("3 - Ver histórico (medições, alertas, exportar CSV)");
            System.out.println("4 - Gerenciar dados do território");
            System.out.println("5 - Status completo");
            System.out.println("0 - Sair");
            int opcao = lerInteiro("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> menuHistorico();
                    case 4 -> menuTerritorio();
                    case 5 -> System.out.println(servico.gerarStatusCompleto().formatar());
                    case 0 -> continuar = false;
                    default -> System.out.println("Opção inválida: digite um número de 0 a 5.");
                }
            } catch (FonteIndisponivelException e) {
                System.out.println("[ERRO] Fonte de dados indisponível: " + e.getMessage());
            } catch (DadosInvalidosException e) {
                System.out.println("[DADO INVÁLIDO] " + e.getMessage());
            }
        }
        System.out.println("Sistema encerrado. O histórico continua salvo no banco de dados.");
    }

    // =================================================================== 1 - sensores

    private void verificarSensores() {
        int ciclos = lerInteiroComPadrao("Quantos ciclos de leitura de todos os sensores (1 a 50)? [1]: ", 1);
        if (ciclos < 1 || ciclos > 50) {
            System.out.println("Quantidade inválida: use um número de 1 a 50.");
            return;
        }
        int gravadas = 0;
        int invalidas = 0;
        int falhas = 0;
        for (int ciclo = 1; ciclo <= ciclos; ciclo++) {
            System.out.println("-- Ciclo " + ciclo + " de " + ciclos);
            for (ResultadoLeitura resultado : servico.verificarSensores()) {
                System.out.println("   " + resultado);
                switch (resultado.situacao()) {
                    case GRAVADA -> gravadas++;
                    case INVALIDA -> invalidas++;
                    case FALHA -> falhas++;
                }
            }
        }
        System.out.printf("Resumo: %d gravada(s), %d descartada(s) por valor inválido, %d falha(s) de sensor.%n",
                gravadas, invalidas, falhas);
        System.out.println("Status dos sensores:");
        for (SensorSimulado sensor : sensoresSimulados) {
            sensor.verificarStatus();
            System.out.println("   " + sensor.descrever());
        }
    }

    // =================================================================== 2 - previsões

    private void rodarPrevisoes() throws FonteIndisponivelException {
        System.out.println("Estratégias de previsão ativas:");
        for (EstrategiaPrevisao estrategia : servico.getEstrategias()) {
            System.out.println("   - " + estrategia);
        }
        RodadaPrevisao rodada = servico.rodarPrevisoes();
        System.out.println();
        System.out.println("PREVISÕES");
        for (ResultadoPrevisao previsao : rodada.previsoes()) {
            System.out.println("   " + previsao);
        }
        boolean algumaInsuficiente = rodada.previsoes().stream().anyMatch(p -> !p.isConfiavel());
        if (algumaInsuficiente) {
            System.out.println("   (Dica: use a opção 1 com vários ciclos para acumular histórico.)");
        }
        System.out.println();
        if (rodada.alertas().isEmpty()) {
            System.out.println("ALERTAS: nenhum. Nenhuma previsão confiável atingiu os limiares.");
        } else {
            System.out.println("ALERTAS EMITIDOS E GRAVADOS (" + rodada.alertas().size() + ")");
            rodada.alertas().forEach(a -> System.out.println("   " + a));
            System.out.println("   O alerta apoia a decisão: confirme a situação em campo antes de agir.");
        }
    }

    // =================================================================== 3 - histórico

    private void menuHistorico() throws FonteIndisponivelException, DadosInvalidosException {
        System.out.println();
        System.out.println("---- HISTÓRICO ----");
        System.out.println("1 - Medições de um sensor");
        System.out.println("2 - Alertas já emitidos");
        System.out.println("3 - Exportar histórico de um sensor para CSV");
        System.out.println("0 - Voltar");
        switch (lerInteiro("Escolha: ")) {
            case 1 -> listarHistoricoSensor();
            case 2 -> listarAlertas();
            case 3 -> exportarCsv();
            case 0 -> { /* volta ao menu principal */ }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void listarHistoricoSensor() throws FonteIndisponivelException, DadosInvalidosException {
        Sensor sensor = escolherSensor();
        List<Medicao> historico = servico.buscarHistoricoPorSensor(sensor.getCodigo());
        if (historico.isEmpty()) {
            System.out.println("Nenhuma medição gravada para " + sensor.getCodigo() + " ainda.");
            return;
        }
        int inicio = Math.max(0, historico.size() - MAX_LINHAS_HISTORICO);
        System.out.printf("Histórico de %s (%s) - %d medição(ões)%s:%n", sensor.getCodigo(),
                sensor.getTipo().getNomeExibicao(), historico.size(),
                inicio > 0 ? ", exibindo as " + MAX_LINHAS_HISTORICO + " mais recentes" : "");
        historico.subList(inicio, historico.size()).forEach(m -> System.out.println("   " + m));
    }

    private void listarAlertas() throws FonteIndisponivelException {
        List<Alerta> alertas = servico.listarAlertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido até agora.");
            return;
        }
        System.out.println("Alertas emitidos (" + alertas.size() + "), do mais recente para o mais antigo:");
        alertas.forEach(a -> System.out.println("   " + a));
    }

    private void exportarCsv() throws FonteIndisponivelException, DadosInvalidosException {
        Sensor sensor = escolherSensor();
        List<Medicao> historico = servico.buscarHistoricoPorSensor(sensor.getCodigo());
        if (historico.isEmpty()) {
            System.out.println("Nada para exportar: o sensor " + sensor.getCodigo() + " ainda não tem medições.");
            return;
        }
        String carimbo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path destino = Path.of("exportacoes", "historico_" + sensor.getCodigo() + "_" + carimbo + ".csv");
        try {
            Path gerado = exportador.exportar(historico, destino);
            System.out.println(historico.size() + " medição(ões) exportada(s) para: " + gerado);
        } catch (IOException e) {
            System.out.println("[ERRO] Não foi possível gravar o arquivo CSV em " + destino + ": " + e.getMessage());
        }
    }

    // =================================================================== 4 - território

    private void menuTerritorio() throws DadosInvalidosException {
        DadosTerritorio territorio = servico.getTerritorio();
        System.out.println();
        System.out.println("---- DADOS DO TERRITÓRIO ----");
        System.out.println("1 - Ver dados e limiares");
        System.out.println("2 - Alterar população exposta");
        System.out.println("3 - Alterar domicílios em área de risco");
        System.out.println("4 - Alterar limiares de alerta (exige código de acesso)");
        System.out.println("5 - Colocar/retirar sensor de manutenção");
        System.out.println("0 - Voltar");
        switch (lerInteiro("Escolha: ")) {
            case 1 -> {
                System.out.println(territorio);
                ConfiguracaoSistema.getInstance().getTodosLimiares().forEach((tipo, l) ->
                        System.out.printf("   %-16s (%s): %s%n", tipo.getNomeExibicao(), tipo.getUnidade(), l));
            }
            case 2 -> {
                territorio.setPopulacaoExposta(lerInteiro("Nova população exposta: "));
                System.out.println("Atualizado: " + territorio);
            }
            case 3 -> {
                territorio.setDomiciliosEmAreaDeRisco(lerInteiro("Novo número de domicílios em área de risco: "));
                System.out.println("Atualizado: " + territorio);
            }
            case 4 -> alterarLimiares();
            case 5 -> alternarManutencao();
            case 0 -> { /* volta ao menu principal */ }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void alterarLimiares() throws DadosInvalidosException {
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("%d - %s (%s) - atual: %s%n", i + 1, tipos[i].getNomeExibicao(), tipos[i].getUnidade(),
                    ConfiguracaoSistema.getInstance().getLimiares(tipos[i]));
        }
        int escolha = lerInteiro("Tipo de sensor: ");
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Tipo inválido.");
            return;
        }
        TipoSensor tipo = tipos[escolha - 1];
        String codigo = lerTexto("Código de acesso da coordenação: ");
        servico.getTerritorio().autorizarAlteracaoDeLimiares(codigo); // confere antes de pedir os valores
        double atencao = lerDouble("Limite de ATENÇÃO (" + tipo.getUnidade() + "): ");
        double alerta = lerDouble("Limite de ALERTA (" + tipo.getUnidade() + "): ");
        double critico = lerDouble("Limite CRÍTICO (" + tipo.getUnidade() + "): ");
        Limiares novos = servico.alterarLimiares(codigo, tipo, atencao, alerta, critico);
        System.out.println("Limiares de " + tipo.getNomeExibicao() + " atualizados: " + novos);
    }

    private void alternarManutencao() {
        for (int i = 0; i < sensoresSimulados.size(); i++) {
            SensorSimulado s = sensoresSimulados.get(i);
            System.out.printf("%d - %s %s - %s%n", i + 1, s.getCodigo(), s.getNome(),
                    s.isEmManutencao() ? "EM MANUTENÇÃO" : "funcionando");
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensoresSimulados.size()) {
            System.out.println("Sensor inválido.");
            return;
        }
        SensorSimulado sensor = sensoresSimulados.get(escolha - 1);
        sensor.colocarEmManutencao(!sensor.isEmManutencao());
        sensor.verificarStatus();
        System.out.println(sensor.getCodigo() + (sensor.isEmManutencao()
                ? " colocado em manutenção: as leituras vão falhar com FonteIndisponivelException."
                : " voltou a funcionar."));
    }

    // =================================================================== leitura do teclado

    private Sensor escolherSensor() throws DadosInvalidosException {
        System.out.println("Sensores disponíveis:");
        for (Sensor s : servico.getSensores()) {
            System.out.printf("   %s - %s (%s)%n", s.getCodigo(), s.getTipo().getNomeExibicao(), s.getLocalizacao());
        }
        return servico.buscarSensor(lerTexto("Código do sensor (ex.: PLV-01): "));
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!entrada.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada encerrada. Saindo do sistema.");
            System.exit(0);
        }
        return entrada.nextLine().trim();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("'" + texto + "' não é um número inteiro. Tente novamente.");
            }
        }
    }

    private int lerInteiroComPadrao(String mensagem, int padrao) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty()) {
                return padrao;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("'" + texto + "' não é um número inteiro. Tente novamente.");
            }
        }
    }

    private double lerDouble(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem).replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("'" + texto + "' não é um número. Use, por exemplo, 12,5.");
            }
        }
    }
}
