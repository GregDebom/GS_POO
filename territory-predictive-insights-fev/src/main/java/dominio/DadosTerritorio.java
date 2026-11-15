package dominio;

import excecao.DadosInvalidosException;

/**
 * Dados do território monitorado. Todos os atributos são privados e só mudam
 * pelos métodos de acesso, que validam cada valor (encapsulamento).
 *
 * A alteração de limiares exige um código de acesso: só a coordenação
 * responsável pode mudar o que conta como "risco" no território.
 * Não há dados pessoais aqui: o território é descrito por região e números agregados.
 */
public class DadosTerritorio {

    private String nomeTerritorio;
    private String regiao;
    private int populacaoExposta;
    private int domiciliosEmAreaDeRisco;
    private final String codigoAcesso;

    public DadosTerritorio(String nomeTerritorio, String regiao, int populacaoExposta,
                           int domiciliosEmAreaDeRisco, String codigoAcesso) throws DadosInvalidosException {
        setNomeTerritorio(nomeTerritorio);
        setRegiao(regiao);
        setPopulacaoExposta(populacaoExposta);
        setDomiciliosEmAreaDeRisco(domiciliosEmAreaDeRisco);
        if (codigoAcesso == null || codigoAcesso.length() < 4) {
            throw new DadosInvalidosException("o código de acesso precisa ter pelo menos 4 caracteres.");
        }
        this.codigoAcesso = codigoAcesso;
    }

    /**
     * Confere o código antes de qualquer alteração de limiares.
     *
     * @throws DadosInvalidosException se o código não confere
     */
    public void autorizarAlteracaoDeLimiares(String codigoInformado) throws DadosInvalidosException {
        if (codigoInformado == null || !codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException("código de acesso inválido: alteração de limiares não autorizada.");
        }
    }

    public String getNomeTerritorio() {
        return nomeTerritorio;
    }

    public void setNomeTerritorio(String nomeTerritorio) throws DadosInvalidosException {
        if (nomeTerritorio == null || nomeTerritorio.isBlank()) {
            throw new DadosInvalidosException("o nome do território não pode ficar vazio.");
        }
        this.nomeTerritorio = nomeTerritorio.trim();
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("a região do território não pode ficar vazia.");
        }
        this.regiao = regiao.trim();
    }

    public int getPopulacaoExposta() {
        return populacaoExposta;
    }

    public void setPopulacaoExposta(int populacaoExposta) throws DadosInvalidosException {
        if (populacaoExposta < 0) {
            throw new DadosInvalidosException("população exposta negativa (" + populacaoExposta + ").");
        }
        this.populacaoExposta = populacaoExposta;
    }

    public int getDomiciliosEmAreaDeRisco() {
        return domiciliosEmAreaDeRisco;
    }

    public void setDomiciliosEmAreaDeRisco(int domiciliosEmAreaDeRisco) throws DadosInvalidosException {
        if (domiciliosEmAreaDeRisco < 0) {
            throw new DadosInvalidosException(
                    "número de domicílios em área de risco negativo (" + domiciliosEmAreaDeRisco + ").");
        }
        this.domiciliosEmAreaDeRisco = domiciliosEmAreaDeRisco;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - população exposta: %d pessoas - domicílios em área de risco: %d",
                nomeTerritorio, regiao, populacaoExposta, domiciliosEmAreaDeRisco);
    }
}
