package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lber on 25/11/2016.
 * ViewHolder é uma classe conjunto de Views que foram uma parte da tela.
 * responsável por setar objeto a uma view.
 */

public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView id;
    public TextView duracao;
    public TextView data;
    public TextView hora;

    public ViewHolder(View v) {
        super(v);
        id = (TextView) v.findViewById(R.id.idValvula);
        duracao = (TextView) v.findViewById(R.id.duracaoValvula);
        data = (TextView) v.findViewById(R.id.dataValvula);
        hora = (TextView) v.findViewById(R.id.horaValvula);
    }
}
