package dominio;

import java.util.List;

/**
 * Comparação com o período anterior: compara a média das últimas N leituras com a
 * média das N leituras anteriores e projeta a mesma variação para o próximo período.
 * Diferente da média móvel, reage à TENDÊNCIA (subindo ou descendo).
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    public static final String NOME = "Comparação de períodos";

    /** Variações menores que 2% da faixa do sensor são consideradas estabilidade. */
    private static final double SENSIBILIDADE_FAIXA = 0.02;

    public PrevisaoComparacao(int janela) {
        super(NOME, janela, janela * 2);
    }

    @Override
    public String getDescricaoMetodo() {
        return "compara as últimas " + getJanela() + " leituras com as " + getJanela()
                + " anteriores e projeta a tendência";
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) {
        ResultadoPrevisao base = super.prever(historico); // validação comum da classe mãe
        if (!base.isConfiavel()) {
            return base;
        }
        TipoSensor tipo = base.getTipo();
        int n = getJanela();
        List<Medicao> doisPeriodos = ultimas(historico, n * 2);
        double mediaAnterior = media(doisPeriodos.subList(0, n));
        double mediaRecente = media(doisPeriodos.subList(n, n * 2));
        double variacao = mediaRecente - mediaAnterior;
        double projecao = tipo.limitarNaFaixa(mediaRecente + variacao);

        double sensibilidade = (tipo.getMaximo() - tipo.getMinimo()) * SENSIBILIDADE_FAIXA;
        String tendencia;
        if (variacao > sensibilidade) {
            tendencia = "tendência de ALTA";
        } else if (variacao < -sensibilidade) {
            tendencia = "tendência de queda";
        } else {
            tendencia = "estável";
        }
        String interpretacao = String.format(
                "Período recente: %s; período anterior: %s (%s, variação de %+.2f %s). "
                        + "Se a variação continuar, o próximo período deve ficar em torno de %s.",
                tipo.formatar(mediaRecente), tipo.formatar(mediaAnterior), tendencia, variacao,
                tipo.getUnidade(), tipo.formatar(projecao));
        return new ResultadoPrevisao(base.getSensorCodigo(), tipo, getNome(), projecao,
                historico.size(), true, interpretacao);
    }
}
