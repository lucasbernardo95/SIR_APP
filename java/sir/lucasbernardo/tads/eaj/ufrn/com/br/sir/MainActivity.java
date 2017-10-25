package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static TabLayout tabLayout;
    private ViewPager vp;

    public static String ip = "192.168.137.1";

    public static Map<String, String> params;
    public static Tarefa tarefa;
    public static RequestQueue rq;
    public String url;
//sgzsd
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tarefa = new Tarefa();
        url = "https://5a74045f.ngrok.io/coleta/";

        //inicia ao sugar passando o context da activity
        SugarContext.init(this);

        vp = (ViewPager) findViewById(R.id.viewPager);

        PagerAdapter pagerAdapter = new TabAdapter(getSupportFragmentManager(), this);
        //Obtém o conteúdo do viewPager através do adapter
        vp.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(vp);

        tabLayout.getTabAt(0).setIcon(android.R.drawable.presence_offline);
        tabLayout.getTabAt(1).setIcon(android.R.drawable.star_off);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(android.R.drawable.presence_online);
                    case 1:
                        tabLayout.getTabAt(1).setIcon(android.R.drawable.ic_search_category_default);
                    default:
                        return;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("info", "onResume tarefa = " + tarefa.isCancelled());
        //inicializa o objeto Requestqueue para realizar as requizições ao web service
        rq = Volley.newRequestQueue(MainActivity.this);
        if ( !tarefa.isCancelled() ) {
            tarefa.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("info", "onPause");
        encerraProcesso();
    }

    //encerra a execução da tarefa em segundo plano, referente às requisições dos valores dos sensores ao arduino
    private void encerraProcesso(){
        rq.cancelAll("tag");
        //cancela a execução da classe tarefa
        if (!tarefa.isCancelled()){
            tarefa.cancel(true);
        }
    }

    //quando a aplicação for ser encerrada fecha a conexão com o banco
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
        Log.i("info", "onDestroy");
        encerraProcesso();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("info", "onStop");
        encerraProcesso();
    }

    public class Tarefa extends AsyncTask<String, String, String> {

        private Map<String, String> params;

        public Tarefa() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("info", "onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            //Nesse trecho, o código ficará requisitando a cada 2 segundos os valores dos sensores do arduino
            while (true) {

                //Dá um delay de dois segundos a cada nova requisição
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //recebe o json do servido especificado na url do parâmetro
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("info", "Requisição: " + response);
                        try {
                            publishProgress(response.getString("temperatura")+","+response.getString("umidade")+","+response.getString("umidade_solo"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("erro", "Requisição" + error);
                    }
                });
                /**
                 *   Ao adicionar esse TAG ao objeto jsonrequest é possível usá-lo em outros lugares
                 *   para cancelar as requisitções ou iniciá-las como é feito no método onPause
                */
                jsonRequest.setTag("tag");
                rq.add(jsonRequest);
            }

        }// while

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            /**
             * Chama o método para recuperar o fragment pela tag e depois passa o valor a ser atualizado
             * na tela para o fragment.
             */

            //updateFragments(values[0], values[0], values[0]);
            String sensores[] = values[0].split(",");
            Log.i("info", "Sensores: "+ values[0]);
            if(sensores[0] != null && sensores[1] != null && sensores[2] != null ){
                updateFragments(sensores[0], sensores[1], sensores[2]);//lembrete
            }
        }

        @Override
        protected void onPostExecute(String aLong) {
            super.onPostExecute(aLong);
        }

    }

    //Método para recuperar o fragment para poder atualizar seus dados
    public static String getFragmentTag(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

    //Método para recuperar o fragment e passar os dados que será alterados no layout do fragment
    public void updateFragments(String umidadeAmb, String temperaturaAmb, String umidadeSolo){
        //FragmentManager: Classe que gerencia os fragments pela API
        FragmentManager fm = getSupportFragmentManager();
        Fragment fc = fm.findFragmentByTag(getFragmentTag(vp.getId(), 0));
        ((FragmentControle) fc).setValorSensor(umidadeAmb, temperaturaAmb, umidadeSolo);
    }

}