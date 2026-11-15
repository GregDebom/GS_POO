package dominio;

import excecao.DadosInvalidosException;

import java.util.Random;

/**
 * Mede a umidade do solo (%) em encostas. Solo saturado indica risco de
 * deslizamento e de enxurrada (a água deixa de infiltrar).
 */
public class SensorUmidadeSolo extends SensorSimulado {

    private static final double UMIDADE_MEDIA = 65.0;

    public SensorUmidadeSolo(String codigo, String nome, String regiao, Random random)
            throws DadosInvalidosException {
        super(codigo, nome, regiao, TipoSensor.UMIDADE_SOLO, random, 55.0, 0.05, 0.04);
    }

    @Override
    protected double gerarProximoValor(double anterior, Random random) {
        return anterior + random.nextGaussian() * 4.0 + 0.05 * (UMIDADE_MEDIA - anterior);
    }

    @Override
    protected double gerarLeituraComDefeito(Random random) {
        return 120.0 + random.nextDouble() * 50.0; // mais de 100%: impossível
    }
}
