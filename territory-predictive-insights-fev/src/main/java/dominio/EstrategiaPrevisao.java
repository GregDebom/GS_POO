package dominio;

import java.util.List;
import java.util.Objects;

/**
 * Estratégia de previsão sobre o histórico de UM sensor (ordem cronológica,
 * da leitura mais antiga para a mais recente).
 *
 * O {@link #prever(List)} desta classe base faz o trabalho comum a todas as
 * estratégias: valida o histórico, decide se há leituras suficientes e, se houver,
 * produz uma previsão ingênua (o último valor lido). As filhas sobrescrevem
 * {@code prever()}, chamam {@code super.prever()} para reaproveitar essa validação
 * e então aplicam o próprio cálculo.
 */
public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int janela;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int janela, int minimoLeituras) {
        if (janela < 1) {
            throw new IllegalArgumentException("A janela de previsão precisa ser de pelo menos 1 leitura.");
        }
        this.nome = Objects.requireNonNull(nome, "nome");
        this.janela = janela;
        this.minimoLeituras = Math.max(minimoLeituras, janela);
    }

    /** Explicação curta do método, mostrada no menu e no relatório. */
    public abstract String getDescricaoMetodo();

    public ResultadoPrevisao prever(List<Medicao> historico) {
        Objects.requireNonNull(historico, "histórico");
        if (historico.isEmpty()) {
            return ResultadoPrevisao.insuficiente("-", null, nome, 0, minimoLeituras);
        }
        Medicao ultima = historico.get(historico.size() - 1);
        boolean variosSensores = historico.stream()
                .anyMatch(m -> !m.getSensorCodigo().equals(ultima.getSensorCodigo()));
        if (variosSensores) {
            throw new IllegalArgumentException("O histórico de uma previsão deve ser de um único sensor.");
        }
        if (historico.size() < minimoLeituras) {
            return ResultadoPrevisao.insuficiente(ultima.getSensorCodigo(), ultima.getTipo(), nome,
                    historico.size(), minimoLeituras);
        }
        return new ResultadoPrevisao(ultima.getSensorCodigo(), ultima.getTipo(), nome, ultima.getValor(),
                historico.size(), true, "Previsão ingênua: repete a última leitura.");
    }

    /** Média aritmética dos valores. */
    protected static double media(List<Medicao> medicoes) {
        return medicoes.stream().mapToDouble(Medicao::getValor).average().orElse(Double.NaN);
    }

    /** As {@code quantidade} medições mais recentes, mantendo a ordem cronológica. */
    protected static List<Medicao> ultimas(List<Medicao> historico, int quantidade) {
        return historico.subList(Math.max(0, historico.size() - quantidade), historico.size());
    }

    public String getNome() {
        return nome;
    }

    public int getJanela() {
        return janela;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    @Override
    public String toString() {
        return nome + " - " + getDescricaoMetodo();
    }
}
