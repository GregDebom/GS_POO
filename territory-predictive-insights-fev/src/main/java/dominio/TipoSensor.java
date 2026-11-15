package dominio;

import excecao.DadosInvalidosException;

/**
 * Tipos de sensor usados no recorte "Clima local: chuva intensa e risco de alagamento".
 * Cada tipo conhece sua unidade e a faixa física aceitável de leitura.
 */
public enum TipoSensor {
    PLUVIOMETRO("Pluviômetro", "chuva", "mm/h", 0.0, 250.0),
    NIVEL_CORREGO("Régua de nível", "nível do córrego", "m", 0.0, 10.0),
    UMIDADE_SOLO("Umidade do solo", "umidade do solo", "%", 0.0, 100.0);

    private final String nomeExibicao;
    private final String grandeza;
    private final String unidade;
    private final double minimo;
    private final double maximo;

    TipoSensor(String nomeExibicao, String grandeza, String unidade, double minimo, double maximo) {
        this.nomeExibicao = nomeExibicao;
        this.grandeza = grandeza;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    /**
     * Garante que o valor está dentro da faixa física do sensor.
     * Ex.: "medição de chuva negativa (-3,00 mm/h)".
     */
    public void validar(double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("medição de " + grandeza + " não é um número válido.");
        }
        if (valor < minimo) {
            String motivo = minimo == 0.0 ? "negativa" : "abaixo do mínimo de " + formatar(minimo);
            throw new DadosInvalidosException("medição de " + grandeza + " " + motivo + " (" + formatar(valor) + ").");
        }
        if (valor > maximo) {
            throw new DadosInvalidosException("medição de " + grandeza + " acima da faixa física do sensor ("
                    + formatar(valor) + " > " + formatar(maximo) + ").");
        }
    }

    /** Mantém um valor calculado (ex.: uma projeção) dentro da faixa física. */
    public double limitarNaFaixa(double valor) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    public String formatar(double valor) {
        return String.format("%.2f %s", valor, unidade);
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public String getGrandeza() {
        return grandeza;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }
}
