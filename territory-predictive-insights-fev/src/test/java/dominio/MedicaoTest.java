package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicaoTest {

    @Test
    @DisplayName("Cria uma medição válida de chuva")
    void deveCriarMedicaoValida() throws DadosInvalidosException {
        // Arrange
        LocalDateTime agora = LocalDateTime.of(2026, 11, 14, 10, 30);

        // Act
        Medicao medicao = new Medicao(null, "PLV-01", TipoSensor.PLUVIOMETRO, 12.5, agora);

        // Assert
        assertEquals("PLV-01", medicao.getSensorCodigo());
        assertEquals(12.5, medicao.getValor(), 0.0001);
        assertEquals(agora, medicao.getDataHora());
        assertNull(medicao.getId());
    }

    @Test
    @DisplayName("Chuva negativa lança DadosInvalidosException")
    void deveRejeitarChuvaNegativa() {
        // Arrange
        double chuvaNegativa = -3.0;

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> Medicao.nova("PLV-01", TipoSensor.PLUVIOMETRO, chuvaNegativa));

        // Assert
        assertTrue(erro.getMessage().contains("chuva negativa"), erro.getMessage());
    }

    @Test
    @DisplayName("Nível do córrego acima da altura da régua lança DadosInvalidosException")
    void deveRejeitarNivelAcimaDaFaixa() {
        // Arrange
        double nivelImpossivel = 25.0; // a régua mede até 10 m

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> Medicao.nova("NIV-01", TipoSensor.NIVEL_CORREGO, nivelImpossivel));

        // Assert
        assertTrue(erro.getMessage().contains("acima da faixa"), erro.getMessage());
    }

    @Test
    @DisplayName("Umidade do solo acima de 100% é rejeitada")
    void deveRejeitarUmidadeAcimaDeCemPorCento() {
        // Arrange / Act / Assert
        assertThrows(DadosInvalidosException.class, () -> Medicao.nova("UMI-01", TipoSensor.UMIDADE_SOLO, 130.0));
    }

    @Test
    @DisplayName("Medição sem código de sensor é rejeitada")
    void deveRejeitarMedicaoSemSensor() {
        // Arrange / Act / Assert
        assertThrows(DadosInvalidosException.class, () -> Medicao.nova("  ", TipoSensor.PLUVIOMETRO, 5.0));
    }
}
