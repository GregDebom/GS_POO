package dominio;

import excecao.DadosInvalidosException;

/**
 * Abstração de qualquer elemento físico do território monitorado
 * (um sensor, um ponto vulnerável etc.).
 *
 * Cada filho decide COMO verifica o próprio status: por isso
 * {@link #verificarStatus()} é abstrato (polimorfismo).
 */
public abstract class ComponenteTerritorial {

    private final String id;
    private String nome;
    private String regiao;
    private StatusComponente status = StatusComponente.NAO_VERIFICADO;

    protected ComponenteTerritorial(String id, String nome, String regiao) throws DadosInvalidosException {
        this.id = exigirTexto(id, "id do componente");
        setNome(nome);
        setRegiao(regiao);
    }

    /** Recalcula o status do componente, atualiza o atributo e o devolve. */
    public abstract StatusComponente verificarStatus();

    /** Texto curto que identifica o componente nos menus e relatórios. */
    public String descrever() {
        return String.format("[%s] %s - região %s - status: %s", id, nome, regiao, status.getDescricao());
    }

    protected void setStatus(StatusComponente status) {
        this.status = status;
    }

    protected static String exigirTexto(String valor, String campo) throws DadosInvalidosException {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo '" + campo + "' não pode ficar vazio.");
        }
        return valor.trim();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        this.nome = exigirTexto(nome, "nome");
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        this.regiao = exigirTexto(regiao, "região");
    }

    public StatusComponente getStatus() {
        return status;
    }
}
