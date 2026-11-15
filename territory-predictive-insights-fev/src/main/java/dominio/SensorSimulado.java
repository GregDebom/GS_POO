package dominio;

import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Base dos sensores simulados: é um {@link ComponenteTerritorial} (tem id, nome,
 * região e status) e é um {@link Sensor} (sabe ler valores).
 *
 * A simulação usa {@link Random}: cada leitura parte da anterior (passeio aleatório),
 * e existem pequenas chances de o sensor não responder (falha de comunicação) ou de
 * devolver um valor absurdo (defeito), para exercitar as exceções do sistema.
 */
public abstract class SensorSimulado extends ComponenteTerritorial implements Sensor {

    private static final int FALHAS_PARA_DEGRADAR = 3;

    private final TipoSensor tipo;
    private final Random random;
    private final double probabilidadeFalha;
    private final double probabilidadeDefeito;
    private double ultimoValor;
    private boolean emManutencao;
    private int falhasConsecutivas;

    protected SensorSimulado(String codigo, String nome, String regiao, TipoSensor tipo, Random random,
                             double valorInicial, double probabilidadeFalha, double probabilidadeDefeito)
            throws DadosInvalidosException {
        super(codigo, nome, regiao);
        tipo.validar(valorInicial);
        this.tipo = tipo;
        this.random = random;
        this.ultimoValor = valorInicial;
        this.probabilidadeFalha = probabilidadeFalha;
        this.probabilidadeDefeito = probabilidadeDefeito;
    }

    /** Próximo valor da simulação, a partir do valor anterior. */
    protected abstract double gerarProximoValor(double anterior, Random random);

    /** Valor impossível que um sensor com defeito poderia devolver. */
    protected abstract double gerarLeituraComDefeito(Random random);

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (emManutencao) {
            falhasConsecutivas++;
            throw new FonteIndisponivelException(
                    "Sensor " + getCodigo() + " (" + tipo.getNomeExibicao() + ") está em manutenção.");
        }
        if (random.nextDouble() < probabilidadeFalha) {
            falhasConsecutivas++;
            throw new FonteIndisponivelException("Sensor " + getCodigo() + " (" + tipo.getNomeExibicao()
                    + ") não respondeu à leitura (tempo de resposta esgotado).");
        }
        falhasConsecutivas = 0;
        if (random.nextDouble() < probabilidadeDefeito) {
            return gerarLeituraComDefeito(random);
        }
        ultimoValor = tipo.limitarNaFaixa(gerarProximoValor(ultimoValor, random));
        return ultimoValor;
    }

    @Override
    public boolean verificarFuncionamento() {
        return !emManutencao;
    }

    @Override
    public StatusComponente verificarStatus() {
        StatusComponente novo;
        if (emManutencao) {
            novo = StatusComponente.INOPERANTE;
        } else if (falhasConsecutivas >= FALHAS_PARA_DEGRADAR) {
            novo = StatusComponente.DEGRADADO;
        } else {
            novo = StatusComponente.OPERACIONAL;
        }
        setStatus(novo);
        return novo;
    }

    public void colocarEmManutencao(boolean emManutencao) {
        this.emManutencao = emManutencao;
    }

    public boolean isEmManutencao() {
        return emManutencao;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }

    @Override
    public String getCodigo() {
        return getId();
    }

    @Override
    public String getLocalizacao() {
        return getRegiao();
    }
}
