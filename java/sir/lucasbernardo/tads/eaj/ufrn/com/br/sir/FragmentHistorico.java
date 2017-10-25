package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lber on 25/11/2016.
 */

public class FragmentHistorico extends Fragment {

    private List<Valvula> valvulas;
    private Valvula valvula;
    private ValvulaAdapter valvulaAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button button;
    private EditText data;

    private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //carrega a lista de valvulas salvas no banco de dados
        //valvulas = Valvula.listAll(Valvula.class);
        List<Valvula> lista = new ArrayList<>();
        lista.add(new Valvula(1,"10:20:15","12/12/12", "15:24:15"));
        valvulas = lista;
        Log.i("info","onCreate");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
        //incla o layout que contém o recycler view a ser inflado, que conterá a lista com o histórico
        final View v = inflater.inflate(R.layout.fragmet_historico, container, false);

        button = (Button) v.findViewById(R.id.btBuscar);
        data = (EditText) v.findViewById(R.id.campoData);

        //Desse mesmo layout a ser inflado, aponta o recyclerview da classe ao do layout
        recyclerView = (RecyclerView) v.findViewById(R.id.recylerhistorico);

        //recupera o swipeRefreshLayout para fazer as modificações necessárias para o funcionamento
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        //Prepara um novo layout para a activity
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //Seta um leyout para a activity
        recyclerView.setLayoutManager(layoutManager);
        //Passa a lista de valvulas e o contexto da activity para adaptar os dados a serem exibidos
        valvulaAdapter = new ValvulaAdapter(valvulas, getActivity());
        //seta os dados adaptados ao recyclerview
        recyclerView.setAdapter(valvulaAdapter);
        //evento para escutar a ação de refresh na tela,executado quando o usuário deslizar o dedo para atualizar os dados
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//chamado quando for solicitado o refresh
                atualizaDados();
                recyclerView.setAdapter(valvulaAdapter);
                //Esconde o indicador visual
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Botao de busca, esse método irá buscar os dados no banco de acordo com a data que for digitada no campo 'edititext'
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info", data.getText().toString()+ "valor digitado");
                //Atualiza a lista dos dados. O select trará todos os dados do histórico de acionamento que corresponderem à data que foi digitada no campo de busca
                valvulas = Valvula.findWithQuery(Valvula.class, "SELECT * FROM valvula WHERE data like '%"+data.getText().toString()+"%'");
                //atualiza a lista do adapter
                valvulaAdapter = new ValvulaAdapter(valvulas, getActivity());
                //após atualizar os dados, informa que houve mudança na lista, ou seja, precisa atualizá-la
                valvulaAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(valvulaAdapter);
                //Esconde o indicador visual
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //implementação do click para os elementos do recyclerview
        recyclerView.addOnItemTouchListener( new ItemClickListener(getActivity(), recyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            //Quando houver um clicklongo na tela irá deletar o objeto valvula selecionado
            @Override
            public void onItemLongClick(View view, int position) {

                Log.i("info", "Índice = "+(position));
                //recebe a válvula que será deletada
                valvula = valvulas.get(position);//subtrai 2 do índice passado, pois não passa a posição exata do elemento, é sempre com dois valores a mais
                //deleta do banco e da lista
                Valvula.delete(valvula);
                valvulas.remove(valvula);
                atualizaDados();
                //Snacbar para implementar a opção do usuário desfazer a esclusão de algum dado do histórico
                Snackbar
                        .make(v, R.string.conteudoSnackbar, Snackbar.LENGTH_LONG)
                       .setAction(R.string.opcaoSnackbar, new View.OnClickListener() {
                           //Quando clicar na opção desfazer, o dado que foi excluído será restaurado
                           @Override
                           public void onClick(View v) {
                               Valvula.save(valvula);
                               atualizaDados();
                           }
                       }).show();
            }
        }));

        return v;
    }

    public void atualizaDados(){
        //Atualiza a lista dos dados
        valvulas = Valvula.listAll(Valvula.class);
        //atualiza a lista do adapter
        valvulaAdapter = new ValvulaAdapter(valvulas, getActivity());
        //após atualizar os dados, informa que houve mudança na lista, ou seja, precisa atualizá-la
        valvulaAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(valvulaAdapter);
        //Esconde o indicador visual do layout
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //SugarContext.terminate();
    }
}
