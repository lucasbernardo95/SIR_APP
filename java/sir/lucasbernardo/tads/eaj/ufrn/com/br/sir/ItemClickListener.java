package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by lber on 27/10/2016.
 * Para implementar a identificação do toque em um RecyclerView é necessário implementar uma classe
 * que estenda RecyclerView.OnItemTouchListener e seu construtor.
 */

public class ItemClickListener implements RecyclerView.OnItemTouchListener{

    OnItemClickListener mListener;
    GestureDetector mGestureDetector;//usado para detectar toques e eventos

    /**
     * O segundo passo é criar uma interface para os métodos que queremos disponibilizar ao RecyclerView
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }



    public ItemClickListener(Context context, final RecyclerView view ,OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                super.onSingleTapUp(e);
                View childView = view.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null ) {
                    /**
                     * findChildViewUnder Esse método retorna a View filha que está sob as
                     * coordenadas X e Y da tela que são passadas.
                     *
                     * usando esse método em conjunto com os métodos getX e getY do motionEvent é
                     * possível detectar qual view o usuário clicou
                     */
                    mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                }
                return true;
            }
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                View childView = view.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && mListener != null){
                    mListener.onItemLongClick(childView,view.getChildAdapterPosition(childView));
                }
            }
        });
    }

    //O terceiro passo é implementar os métodos de RecyclerView.OnItemTouchListener

    /**
     *  Intercepta um evento de toque na tela.
     *  Se retornar falso o evento também deverá ser tratado pela View que está por baixo.
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    //Chamado quando um toque na View é detectado

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    //Chamado quando uma View filha não quer que os eventos sejam interceptados.

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}