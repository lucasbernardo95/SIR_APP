package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.util.Calendar;

import static android.os.SystemClock.elapsedRealtime;

/**
 * Created by lber on 25/11/2016.
 */

public class FragmentControle extends Fragment {

    public static RequestQueue rq;

    private Switch botao;
    private Chronometer cron;
    private ImageView imgAcionamento;
    String TAG = "info";
    private Calendar calendario;

    public ProgressBar progressoUmidadeAmb;
    public TextView umidadeAmb;

    public ProgressBar progressoTemperaturaAmb;
    public TextView temperaturaAmb;

    public ProgressBar progressoUmidadeSolo;
    public TextView umidadeSolo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_valvulas, container, false);

        progressoUmidadeAmb = (ProgressBar) v.findViewById(R.id.progressUmidadeab);
        umidadeAmb = (TextView) v.findViewById(R.id.valorUmidadeab);

        progressoTemperaturaAmb = (ProgressBar) v.findViewById(R.id.progressTemperaturaab);
        temperaturaAmb = (TextView) v.findViewById(R.id.valorTemperaturaab);

        progressoUmidadeSolo = (ProgressBar) v.findViewById(R.id.progressUmidadesolo);
        umidadeSolo = (TextView) v.findViewById(R.id.valorUmidadeSolo);

        imgAcionamento = (ImageView) v.findViewById(R.id.imgAcionamento);
        cron = (Chronometer) v.findViewById(R.id.cronometro);
        //aplica o formato hora:minuto:segundo
        //cron.setText(DateFormat.format("00:mm:ss", 0));
        cron.setBase(elapsedRealtime() );
        cron.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //Seta o tempo de início - o tempo de execução do cronômetro
                cron.setText(DateFormat.format("00:mm:ss", elapsedRealtime() - chronometer.getBase()));
            }
        });

        botao = (Switch) v.findViewById(R.id.btAcionamento);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botao.isChecked()){
                    botao.setText(R.string.ativo);
                    enviarSinal("liga/");
                    Log.i(TAG, "liga");
                    imgAcionamento.setImageResource(R.drawable.gree);
                    iniciaCronometro();
                } else {
                    botao.setText(R.string.inativo);
                    //Envia o sinal de fechamento da válvula
                    enviarSinal("desliga/");
                    Log.i(TAG, "desliga");
                    imgAcionamento.setImageResource(R.drawable.red);
                    //Para o cronômetro
                    pararCronometro();
                    //armazena o tempo que a válvula passou ativa
                    String duracao = (String) DateFormat.format("00:mm:ss", elapsedRealtime() - cron.getBase());
                    //Captura a data do acionamento e hora
                    calendario = Calendar.getInstance(java.util.TimeZone.getTimeZone("Brazil/East"));
                    int ano = calendario.get(Calendar.YEAR);
                    int mes = calendario.get(Calendar.MONTH) + 1; // O mês vai de 0 a 11.
                    int dia = calendario.get(Calendar.DAY_OF_MONTH);
                    int hora = calendario.get(Calendar.HOUR_OF_DAY) - 1;//horário local
                    int minuto = calendario.get(Calendar.MINUTE);

                    Valvula sensor = new Valvula(1, duracao, dia+"/"+mes+"/"+ano+" ", hora + ":" +minuto);
                    Log.i(TAG, "hora: " + sensor.toString());
                    sensor.save();

                }
            }
        });

        return v;
    }

    /**
     * Método para enviar o sinal de acionamento da válvula.
     * @param sinal indica se a válvula deve ser aionada ou desligada. Quando seu valor for 1, esse valor
     * será enviado para o web service que irá acionar a irrigação, quando for 0, será encerrada a
     * atividade.
     */
    public void enviarSinal(final String sinal) {


        StringRequest request = new StringRequest(Request.Method.GET,
                    "https://5a74045f.ngrok.io/"+sinal, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, response + " resposta");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Se der erro, atualiza a imagem de status para desconectado
                    Log.i(TAG, "Deu erro: " + error);
                }
            }){ //Define o parâmetro a ser enviado para o web service,
               /* @Override
                public Map<String, String> getParams ()throws AuthFailureError {
                  MainActivity.params = new HashMap<String, String>();
                    MainActivity.params.put("sinal", sinal);//sinal é o parâmetro recebido de acordo com o botão pressionado
                    return (MainActivity.params);
              }*/
            };

            request.setTag("tag");
            MainActivity.rq.add(request);
    }

    //conta a duração de tempo que a válvula ficou ativa
    private void iniciaCronometro(){
        cron.setBase(elapsedRealtime());
        cron.start();
    }

    private void pararCronometro(){
        cron.stop();
    }

    //recebe os valores lidos dos sensores e seta na tela do usuário, seta o valor em forma de
    // string e depois converte para inteiro para usar o progressbar
    public void setValorSensor(String umidadeAmb, String temperaturaAmb, String umidadeSolo){
        this.umidadeAmb.setText(umidadeAmb);
        this.progressoUmidadeAmb.setProgress( (int) Double.parseDouble(umidadeAmb));

        this.temperaturaAmb.setText(temperaturaAmb);
        this.progressoTemperaturaAmb.setProgress( (int) Double.parseDouble(temperaturaAmb));

        this.umidadeSolo.setText(umidadeSolo);
        this.progressoUmidadeSolo.setProgress( (int) Double.parseDouble(umidadeAmb));
    }

}
