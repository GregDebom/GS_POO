package dominio;

/** Situação operacional de um componente do território (sensor ou ponto de monitoramento). */
public enum StatusComponente {
    OPERACIONAL("operacional"),
    DEGRADADO("degradado (funcionando parcialmente)"),
    INOPERANTE("inoperante"),
    NAO_VERIFICADO("ainda não verificado");

    private final String descricao;

    StatusComponente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
