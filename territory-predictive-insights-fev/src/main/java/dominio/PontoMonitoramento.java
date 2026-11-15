package dominio;

import excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ponto vulnerável do território (ex.: uma baixada que costuma alagar) com os
 * sensores instalados nele. O status do ponto depende de quantos sensores funcionam.
 */
public class PontoMonitoramento extends ComponenteTerritorial {

    private final String descricaoRisco;
    private final List<Sensor> sensores = new ArrayList<>();

    public PontoMonitoramento(String id, String nome, String regiao, String descricaoRisco)
            throws DadosInvalidosException {
        super(id, nome, regiao);
        this.descricaoRisco = exigirTexto(descricaoRisco, "descrição do risco");
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    @Override
    public StatusComponente verificarStatus() {
        long funcionando = sensores.stream().filter(Sensor::verificarFuncionamento).count();
        StatusComponente novo;
        if (funcionando == 0) {
            novo = StatusComponente.INOPERANTE;
        } else if (funcionando < sensores.size()) {
            novo = StatusComponente.DEGRADADO;
        } else {
            novo = StatusComponente.OPERACIONAL;
        }
        setStatus(novo);
        return novo;
    }

    @Override
    public String descrever() {
        return super.descrever() + " - " + sensores.size() + " sensor(es) - risco: " + descricaoRisco;
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }

    public String getDescricaoRisco() {
        return descricaoRisco;
    }
}
