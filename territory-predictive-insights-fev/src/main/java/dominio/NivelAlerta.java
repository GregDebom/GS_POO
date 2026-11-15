package dominio;

/**
 * Níveis de alerta em ordem crescente de gravidade. Cada nível traz uma
 * recomendação de ação: o sistema sugere, quem decide é a pessoa responsável.
 */
public enum NivelAlerta {
    ATENCAO("ATENÇÃO",
            "Acompanhar a evolução e verificar bueiros e bocas de lobo dos pontos vulneráveis."),
    ALERTA("ALERTA",
            "Avisar os moradores das áreas de risco e deixar abrigos e equipes de apoio de prontidão."),
    CRITICO("CRÍTICO",
            "Acionar a Defesa Civil e orientar a saída preventiva das áreas de risco.");

    private final String rotulo;
    private final String recomendacao;

    NivelAlerta(String rotulo, String recomendacao) {
        this.rotulo = rotulo;
        this.recomendacao = recomendacao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getRecomendacao() {
        return recomendacao;
    }
}
