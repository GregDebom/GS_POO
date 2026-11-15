package dominio;

import excecao.FonteIndisponivelException;

/**
 * Contrato de qualquer sensor do território. O resto do sistema só conhece esta
 * interface, então trocar um sensor simulado por um real não muda o serviço.
 */
public interface Sensor {

    /**
     * Lê o valor atual do sensor (valor bruto, ainda não validado).
     *
     * @throws FonteIndisponivelException se o sensor não responder ou estiver em manutenção
     */
    double lerValor() throws FonteIndisponivelException;

    /** Indica se o sensor está em condições de fazer leituras. */
    boolean verificarFuncionamento();

    /** Tipo de grandeza medida (chuva, nível do córrego, umidade do solo). */
    TipoSensor getTipo();

    /** Código único do sensor, usado como chave no histórico do banco. */
    String getCodigo();

    /** Região do território onde o sensor está instalado. */
    String getLocalizacao();
}
