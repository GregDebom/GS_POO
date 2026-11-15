package dominio;

import excecao.DadosInvalidosException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DadosTerritorioTest {

    private DadosTerritorio criarTerritorio() throws DadosInvalidosException {
        return new DadosTerritorio("Bacia Teste", "Zona Teste", 1000, 50, "segredo");
    }

    @Test
    @DisplayName("População negativa lança DadosInvalidosException e mantém o valor anterior")
    void deveRejeitarPopulacaoNegativa() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = criarTerritorio();

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> territorio.setPopulacaoExposta(-10));

        // Assert
        assertTrue(erro.getMessage().contains("população exposta negativa"), erro.getMessage());
        assertEquals(1000, territorio.getPopulacaoExposta());
    }

    @Test
    @DisplayName("Código de acesso errado bloqueia a alteração de limiares")
    void deveBloquearCodigoDeAcessoErrado() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = criarTerritorio();

        // Act / Assert
        assertThrows(DadosInvalidosException.class, () -> territorio.autorizarAlteracaoDeLimiares("errado"));
        assertDoesNotThrow(() -> territorio.autorizarAlteracaoDeLimiares("segredo"));
    }

    @Test
    @DisplayName("Limiares fora de ordem (ALERTA menor que ATENÇÃO) são rejeitados")
    void deveRejeitarLimiaresForaDeOrdem() {
        // Arrange / Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Limiares(30, 10, 50));

        // Assert
        assertTrue(erro.getMessage().contains("limiar inválido"), erro.getMessage());
    }

    @Test
    @DisplayName("Classifica o valor no nível mais grave atingido")
    void deveClassificarNivelMaisGrave() throws DadosInvalidosException {
        // Arrange
        Limiares limiares = new Limiares(10, 30, 50);

        // Act
        Optional<NivelAlerta> abaixo = limiares.classificar(9.99);
        Optional<NivelAlerta> alerta = limiares.classificar(30.0);
        Optional<NivelAlerta> critico = limiares.classificar(75.0);

        // Assert
        assertTrue(abaixo.isEmpty());
        assertEquals(NivelAlerta.ALERTA, alerta.orElseThrow());
        assertEquals(NivelAlerta.CRITICO, critico.orElseThrow());
    }
}
