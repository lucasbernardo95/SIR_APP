package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

/**
 * Created by lber on 25/11/2016.
 */

public class ValvulaAdapter extends RecyclerView.Adapter {

    private List<Valvula> valvulas;
    private Context context;

    public ValvulaAdapter(List<Valvula> valvulas, Context context) {
        this.context = context;
        this.valvulas = valvulas;
    }

    //Cria o ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //infla o layout que contém os textViews a serem adaptados
        View view = LayoutInflater.from(context).inflate(R.layout.valvulaadapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //onBindViewHolder: Recebe o ViewHolder para setar os atributos da view.
    // holder vai receber o nome do bairro posição que será passada
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).id.setText(valvulas.get(position).getIdValvula().toString());
        ((ViewHolder) holder).hora.setText(valvulas.get(position).getHora());
        ((ViewHolder) holder).data.setText(valvulas.get(position).getData());
        ((ViewHolder) holder).duracao.setText(valvulas.get(position).getDuracao());
    }

    // Número total de itens na lista
    @Override
    public int getItemCount() {
        return valvulas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}