package excecao;

/**
 * Lançada quando um dado do território não pode ser aceito:
 * medição fora da faixa física do sensor, população negativa, limiar inválido etc.
 *
 * É uma exceção verificada (checked): quem chama é obrigado a tratar o problema,
 * o que impede que um dado inválido "passe batido" até o banco.
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
