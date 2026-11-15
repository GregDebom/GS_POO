-- Esquema do banco (H2). Executado automaticamente na inicialização do sistema.
-- "IF NOT EXISTS" garante que o histórico existente nunca é apagado ao reiniciar.

CREATE TABLE IF NOT EXISTS medicao (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_codigo VARCHAR(30)      NOT NULL,
    tipo          VARCHAR(30)      NOT NULL,
    valor         DOUBLE PRECISION NOT NULL,
    data_hora     TIMESTAMP        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_medicao_sensor_data ON medicao (sensor_codigo, data_hora);

CREATE TABLE IF NOT EXISTS alerta (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_codigo  VARCHAR(30)      NOT NULL,
    tipo           VARCHAR(30)      NOT NULL,
    nivel          VARCHAR(15)      NOT NULL,
    valor_previsto DOUBLE PRECISION NOT NULL,
    limite         DOUBLE PRECISION NOT NULL,
    estrategia     VARCHAR(60)      NOT NULL,
    mensagem       VARCHAR(1000)    NOT NULL,
    data_hora      TIMESTAMP        NOT NULL
);
