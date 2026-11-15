package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrevisaoMediaMovelTest {

    /** Cria um histórico em ordem cronológica, uma leitura por hora. */
    static List<Medicao> historico(String sensor, TipoSensor tipo, double... valores) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 14, 0, 0);
        for (int i = 0; i < valores.length; i++) {
            lista.add(new Medicao((long) i + 1, sensor, tipo, valores[i], inicio.plusHours(i)));
        }
        return lista;
    }

    @Test
    @DisplayName("Calcula a média das últimas 5 leituras, ignorando as mais antigas")
    void deveCalcularMediaDasUltimasCincoLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);
        List<Medicao> leituras = historico("PLV-01", TipoSensor.PLUVIOMETRO, 100, 100, 2, 4, 6, 8, 10);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert: (2 + 4 + 6 + 8 + 10) / 5 = 6
        assertTrue(resultado.isConfiavel());
        assertEquals(6.0, resultado.getValorPrevisto(), 0.0001);
        assertEquals("PLV-01", resultado.getSensorCodigo());
    }

    @Test
    @DisplayName("Com menos leituras que a janela, o sistema se cala (resultado não confiável)")
    void deveSeCalarComPoucasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);
        List<Medicao> leituras = historico("PLV-01", TipoSensor.PLUVIOMETRO, 40, 45, 50);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert
        assertFalse(resultado.isConfiavel());
        assertTrue(Double.isNaN(resultado.getValorPrevisto()));
        assertTrue(resultado.getInterpretacao().contains("3 leitura"), resultado.getInterpretacao());
    }

    @Test
    @DisplayName("Média móvel é diferente da previsão ingênua da classe mãe")
    void deveSobrescreverPrevisaoDaClasseMae() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);
        List<Medicao> leituras = historico("NIV-01", TipoSensor.NIVEL_CORREGO, 1.0, 2.0, 6.0);

        // Act
        ResultadoPrevisao resultado = estrategia.prever(leituras);

        // Assert: a última leitura é 6,0, mas a média é 3,0
        assertEquals(3.0, resultado.getValorPrevisto(), 0.0001);
        assertEquals(PrevisaoMediaMovel.NOME, resultado.getEstrategia());
    }

    @Test
    @DisplayName("Histórico misturando sensores é rejeitado")
    void deveRejeitarHistoricoDeSensoresDiferentes() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(2);
        List<Medicao> leituras = new ArrayList<>(historico("PLV-01", TipoSensor.PLUVIOMETRO, 5, 6));
        leituras.addAll(historico("PLV-02", TipoSensor.PLUVIOMETRO, 7));

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> estrategia.prever(leituras));
    }
}
