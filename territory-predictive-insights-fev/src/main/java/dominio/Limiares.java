package dominio;

import excecao.DadosInvalidosException;

import java.util.Optional;

/**
 * Os três limites de um tipo de sensor (ATENÇÃO < ALERTA < CRÍTICO).
 * Objeto imutável: para mudar um limiar, cria-se um novo objeto, validado no construtor.
 */
public final class Limiares {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        if (Double.isNaN(atencao) || Double.isNaN(alerta) || Double.isNaN(critico)) {
            throw new DadosInvalidosException("limiar inválido: todos os limites precisam ser números.");
        }
        if (atencao < 0) {
            throw new DadosInvalidosException("limiar inválido: o limite de ATENÇÃO não pode ser negativo ("
                    + atencao + ").");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(String.format(
                    "limiar inválido: é preciso ATENÇÃO < ALERTA < CRÍTICO (recebido %.2f / %.2f / %.2f).",
                    atencao, alerta, critico));
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    /**
     * Devolve o nível mais grave cujo limite foi atingido, ou vazio se o valor
     * está abaixo de todos os limites.
     */
    public Optional<NivelAlerta> classificar(double valor) {
        NivelAlerta[] niveis = NivelAlerta.values();
        for (int i = niveis.length - 1; i >= 0; i--) {
            if (valor >= limiteDe(niveis[i])) {
                return Optional.of(niveis[i]);
            }
        }
        return Optional.empty();
    }

    public double limiteDe(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }

    @Override
    public String toString() {
        return String.format("ATENÇÃO >= %.2f | ALERTA >= %.2f | CRÍTICO >= %.2f", atencao, alerta, critico);
    }
}
