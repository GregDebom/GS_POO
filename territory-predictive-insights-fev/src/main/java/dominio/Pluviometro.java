package dominio;

import excecao.DadosInvalidosException;

import java.util.Random;

/**
 * Mede a intensidade da chuva (mm/h). A simulação alterna períodos de chuva fraca
 * com "pancadas" de chuva forte, que se dissipam aos poucos.
 */
public class Pluviometro extends SensorSimulado {

    public Pluviometro(String codigo, String nome, String regiao, Random random) throws DadosInvalidosException {
        super(codigo, nome, regiao, TipoSensor.PLUVIOMETRO, random, 4.0, 0.05, 0.04);
    }

    @Override
    protected double gerarProximoValor(double anterior, Random random) {
        double valor = anterior * 0.6 + Math.abs(random.nextGaussian()) * 2.0;
        if (random.nextDouble() < 0.15) {
            valor += 8.0 + random.nextDouble() * 30.0; // pancada de chuva
        }
        return valor;
    }

    @Override
    protected double gerarLeituraComDefeito(Random random) {
        return -(1.0 + random.nextDouble() * 5.0); // chuva negativa: impossível
    }
}
