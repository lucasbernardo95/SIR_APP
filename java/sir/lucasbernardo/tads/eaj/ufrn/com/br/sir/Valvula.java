package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import com.orm.SugarRecord;

/**
 * Created by lber on 25/11/2016.
 */

public class Valvula extends SugarRecord {

    private Integer idValvula;
    private String duracao;
    private String data;
    private String hora;

    public Valvula(){}

    public Valvula(Integer idValvula, String duracao, String data, String hora) {
        this.idValvula = idValvula;
        this.duracao = duracao;
        this.data = data;
        this.hora = hora;
    }

    @Override
    public String toString() {
        return "Válvula: " +getIdValvula()+", Duração: "+getDuracao()+ ", Data: "+ getData() + " Hora: "+getHora();
    }

    public Integer getIdValvula() {
        return idValvula;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }
}
