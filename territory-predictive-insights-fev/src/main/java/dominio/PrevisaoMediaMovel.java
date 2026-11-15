package dominio;

import java.util.List;

/**
 * Média móvel: a previsão é a média das últimas N leituras.
 * Suaviza picos isolados e indica o patamar em que o sensor está.
 */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public static final String NOME = "Média móvel";

    public PrevisaoMediaMovel(int janela) {
        super(NOME, janela, janela);
    }

    @Override
    public String getDescricaoMetodo() {
        return "média das últimas " + getJanela() + " leituras";
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) {
        ResultadoPrevisao base = super.prever(historico); // validação comum da classe mãe
        if (!base.isConfiavel()) {
            return base;
        }
        List<Medicao> janela = ultimas(historico, getJanela());
        double media = media(janela);
        TipoSensor tipo = base.getTipo();
        String interpretacao = String.format(
                "Média das últimas %d leituras de %s: %s. É o patamar esperado para as próximas horas se o "
                        + "comportamento recente se mantiver (picos isolados são suavizados).",
                janela.size(), tipo.getGrandeza(), tipo.formatar(media));
        return new ResultadoPrevisao(base.getSensorCodigo(), tipo, getNome(), media,
                historico.size(), true, interpretacao);
    }
}
