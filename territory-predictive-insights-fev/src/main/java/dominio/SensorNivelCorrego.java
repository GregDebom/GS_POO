package dominio;

import excecao.DadosInvalidosException;

import java.util.Random;

/**
 * Régua de nível eletrônica instalada em um córrego (metros acima do leito).
 * O nível oscila em torno de um patamar normal e pode subir em sequência.
 */
public class SensorNivelCorrego extends SensorSimulado {

    private static final double NIVEL_NORMAL = 1.5;

    public SensorNivelCorrego(String codigo, String nome, String regiao, Random random)
            throws DadosInvalidosException {
        super(codigo, nome, regiao, TipoSensor.NIVEL_CORREGO, random, 1.2, 0.05, 0.04);
    }

    @Override
    protected double gerarProximoValor(double anterior, Random random) {
        return anterior + random.nextGaussian() * 0.25 + 0.06 * (NIVEL_NORMAL - anterior);
    }

    @Override
    protected double gerarLeituraComDefeito(Random random) {
        return 25.0 + random.nextDouble() * 10.0; // acima da altura física da régua
    }
}
