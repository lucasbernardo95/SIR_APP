package sir.lucasbernardo.tads.eaj.ufrn.com.br.sir;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by lber on 25/11/2016.
 */

public class TabAdapter extends FragmentPagerAdapter {
    private Context context;

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentControle();
            case 1:
                return new FragmentHistorico();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return this.context.getString(R.string.controle);
            case 1:
                return  this.context.getString(R.string.historico);
            default:
                return null;
        }
    }
}
