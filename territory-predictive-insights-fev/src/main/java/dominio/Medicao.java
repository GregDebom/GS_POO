package dominio;

import excecao.DadosInvalidosException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Uma leitura de sensor. A validação acontece no construtor: não existe
 * objeto Medicao com valor fora da faixa física do sensor.
 */
public class Medicao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Long id;
    private final String sensorCodigo;
    private final TipoSensor tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    /** Construtor completo, usado também ao recarregar uma medição do banco. */
    public Medicao(Long id, String sensorCodigo, TipoSensor tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorCodigo == null || sensorCodigo.isBlank()) {
            throw new DadosInvalidosException("medição sem código de sensor.");
        }
        if (tipo == null) {
            throw new DadosInvalidosException("medição do sensor " + sensorCodigo + " sem tipo de sensor.");
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("medição do sensor " + sensorCodigo + " sem data e hora.");
        }
        tipo.validar(valor);
        this.id = id;
        this.sensorCodigo = sensorCodigo;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    /** Cria uma medição nova (ainda sem id do banco) com a data e hora atuais. */
    public static Medicao nova(String sensorCodigo, TipoSensor tipo, double valor) throws DadosInvalidosException {
        return new Medicao(null, sensorCodigo, tipo, valor, LocalDateTime.now());
    }

    /** Devolve uma cópia com o id gerado pelo banco. */
    public Medicao comId(long novoId) {
        try {
            return new Medicao(novoId, sensorCodigo, tipo, valor, dataHora);
        } catch (DadosInvalidosException e) {
            // impossível: os dados já foram validados quando este objeto foi criado
            throw new IllegalStateException("Medição válida ficou inválida ao receber id: " + e.getMessage(), e);
        }
    }

    public Long getId() {
        return id;
    }

    public String getSensorCodigo() {
        return sensorCodigo;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDataHoraFormatada() {
        return dataHora.format(FORMATO);
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", getDataHoraFormatada(), sensorCodigo, tipo.formatar(valor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicao outra)) return false;
        return Double.compare(valor, outra.valor) == 0 && Objects.equals(id, outra.id)
                && sensorCodigo.equals(outra.sensorCodigo) && tipo == outra.tipo && dataHora.equals(outra.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sensorCodigo, tipo, valor, dataHora);
    }
}
