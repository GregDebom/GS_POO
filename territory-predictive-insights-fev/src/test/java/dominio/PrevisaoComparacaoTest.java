package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dominio.PrevisaoMediaMovelTest.historico;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrevisaoComparacaoTest {

    @Test
    @DisplayName("Projeta a tendência de alta: recente 20, anterior 10, próximo período 30")
    void deveProjetarTendenciaDeAlta() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao estrategia = new PrevisaoComparacao(3);
        List<Medicao> leituras = historico("PLV-01", TipoSensor.PLUVIOMETRO, 10, 10, 10, 20, 20, 20);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert
        assertTrue(resultado.isConfiavel());
        assertEquals(30.0, resultado.getValorPrevisto(), 0.0001);
        assertTrue(resultado.getInterpretacao().contains("ALTA"), resultado.getInterpretacao());
    }

    @Test
    @DisplayName("Uma queda forte não gera previsão negativa (respeita a faixa física)")
    void naoDeveProjetarValorNegativo() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao estrategia = new PrevisaoComparacao(2);
        List<Medicao> leituras = historico("PLV-01", TipoSensor.PLUVIOMETRO, 40, 40, 5, 5);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert: 5 + (5 - 40) = -30, limitado a 0 mm/h
        assertEquals(0.0, resultado.getValorPrevisto(), 0.0001);
    }

    @Test
    @DisplayName("Precisa de dois períodos completos: com só um período, se cala")
    void deveExigirDoisPeriodos() throws DadosInvalidosException {
        // Arrange
        PrevisaoComparacao estrategia = new PrevisaoComparacao(5);
        List<Medicao> leituras = historico("PLV-01", TipoSensor.PLUVIOMETRO, 1, 2, 3, 4, 5, 6, 7);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert
        assertFalse(resultado.isConfiavel());
        assertEquals(10, estrategia.getMinimoLeituras());
    }
}
