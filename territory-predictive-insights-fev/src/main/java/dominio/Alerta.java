package dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Alerta emitido quando uma previsão atinge um limiar. Guarda o "porquê":
 * sensor, valor previsto, limite estourado e estratégia usada.
 */
public class Alerta {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Long id;
    private final String sensorCodigo;
    private final TipoSensor tipo;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limite;
    private final String estrategia;
    private final String mensagem;
    private final LocalDateTime dataHora;

    public Alerta(Long id, String sensorCodigo, TipoSensor tipo, NivelAlerta nivel, double valorPrevisto,
                  double limite, String estrategia, String mensagem, LocalDateTime dataHora) {
        this.id = id;
        this.sensorCodigo = Objects.requireNonNull(sensorCodigo, "sensorCodigo");
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.nivel = Objects.requireNonNull(nivel, "nivel");
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia");
        this.mensagem = Objects.requireNonNull(mensagem, "mensagem");
        this.dataHora = Objects.requireNonNull(dataHora, "dataHora");
    }

    public Alerta comId(long novoId) {
        return new Alerta(novoId, sensorCodigo, tipo, nivel, valorPrevisto, limite, estrategia, mensagem, dataHora);
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

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDataHoraFormatada() {
        return dataHora.format(FORMATO);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s%n    %s", nivel.getRotulo(), getDataHoraFormatada(), sensorCodigo, mensagem);
    }
}
