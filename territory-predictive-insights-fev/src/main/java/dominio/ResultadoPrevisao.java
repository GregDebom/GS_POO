package dominio;

/**
 * Resultado de uma estratégia de previsão: o valor previsto e o que ele significa.
 * Quando não há leituras suficientes o resultado é "não confiável" e o sistema se cala
 * (não gera alerta).
 */
public class ResultadoPrevisao {

    private final String sensorCodigo;
    private final TipoSensor tipo;
    private final String estrategia;
    private final double valorPrevisto;
    private final int quantidadeLeituras;
    private final boolean confiavel;
    private final String interpretacao;

    public ResultadoPrevisao(String sensorCodigo, TipoSensor tipo, String estrategia, double valorPrevisto,
                             int quantidadeLeituras, boolean confiavel, String interpretacao) {
        this.sensorCodigo = sensorCodigo;
        this.tipo = tipo;
        this.estrategia = estrategia;
        this.valorPrevisto = valorPrevisto;
        this.quantidadeLeituras = quantidadeLeituras;
        this.confiavel = confiavel;
        this.interpretacao = interpretacao;
    }

    /** Resultado "silencioso": poucas leituras para prever com segurança. */
    public static ResultadoPrevisao insuficiente(String sensorCodigo, TipoSensor tipo, String estrategia,
                                                 int quantidadeLeituras, int minimoNecessario) {
        String texto = String.format("Sem previsão: há %d leitura(s) no histórico e esta estratégia precisa de "
                + "pelo menos %d. O sistema não emite alerta com tão poucos dados.", quantidadeLeituras, minimoNecessario);
        return new ResultadoPrevisao(sensorCodigo, tipo, estrategia, Double.NaN, quantidadeLeituras, false, texto);
    }

    public String getSensorCodigo() {
        return sensorCodigo;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public int getQuantidadeLeituras() {
        return quantidadeLeituras;
    }

    public boolean isConfiavel() {
        return confiavel;
    }

    public String getInterpretacao() {
        return interpretacao;
    }

    @Override
    public String toString() {
        String valor = confiavel && tipo != null ? tipo.formatar(valorPrevisto) : "-";
        return String.format("%s | %-24s | previsto: %-12s%n    %s", sensorCodigo, estrategia, valor, interpretacao);
    }
}
