package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.views.BaseView;

/**
 * Created by Jonathan Zanella on 03/02/16.
 */
public class WeeklyPagerAdapter extends PagerAdapter {
	public class Period {
		public DateTime init;
		public DateTime end;

		public String titleize() {
			return String.format(Environment.PTBR_LOCALE, "%02d - %02d", init.getDayOfMonth(), end.getDayOfMonth());
		}
	}
    private Context context;
    private List<Period> periods = new ArrayList<>();
	private WeeklyPagerAdapterBuilder builder;

    public WeeklyPagerAdapter(Context context, DateTime month, WeeklyPagerAdapterBuilder builder) {
        this.context = context;
	    this.builder = builder;

	    DateTime init = month.withDayOfMonth(1);

	    while(init.getMonthOfYear() == month.getMonthOfYear()) {
		    Period period = new Period();
		    period.init = init;
		    period.end = init.plusDays(6).withTime(23, 59, 59, 999);
		    if(period.end.getMonthOfYear() > month.getMonthOfYear()) {
			    period.end.minusMonths(1);
			    period.end = init.dayOfMonth().withMaximumValue();
		    }
		    init = init.plusDays(7);
		    periods.add(period);
	    }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        BaseView view = builder.buildView(context, periods.get(position));
        collection.addView(view);
        view.refreshData();
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return periods.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return periods.get(position).titleize();
    }

	public Period getPeriod(int position) {
		return periods.get(position);
	}
}
