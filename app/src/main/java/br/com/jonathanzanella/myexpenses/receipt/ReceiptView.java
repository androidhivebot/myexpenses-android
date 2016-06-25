package br.com.jonathanzanella.myexpenses.receipt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.overview.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.overview.MonthlyPagerAdapterBuilder;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 03/02/16.
 */
public class ReceiptView extends BaseView implements ViewPager.OnPageChangeListener {
	private static final int REQUEST_ADD_RECEIPT = 1007;
	@Bind(R.id.view_receipts_pager)
    ViewPager pager;

	private MonthlyPagerAdapter adapter;

	private Map<DateTime, WeakReference<ReceiptMonthlyView>> views = new HashMap<>();

    public ReceiptView(Context context) {
        super(context);
    }

    public ReceiptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReceiptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_receipts, this);
        ButterKnife.bind(this);

        adapter = new MonthlyPagerAdapter(getContext(), new MonthlyPagerAdapterBuilder() {
	        @Override
	        public BaseView buildView(Context ctx, DateTime date) {
		        ReceiptMonthlyView view = new ReceiptMonthlyView(ctx, date);
		        views.put(date, new WeakReference<>(view));
		        return view;
	        }
        });
        pager.setAdapter(adapter);
        pager.setCurrentItem(MonthlyPagerAdapter.INIT_MONTH_VISIBLE);
	    pager.addOnPageChangeListener(this);
    }

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		filter(filter);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void setTabs(TabLayout tabs) {
		tabs.setupWithViewPager(pager);
		tabs.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.view_receipts_fab)
	void onFab() {
		Context ctx = getContext();
		Intent i = new Intent(getContext(), EditReceiptActivity.class);
		if(ctx instanceof Activity) {
			((Activity) ctx).startActivityForResult(i, REQUEST_ADD_RECEIPT);
		} else {
			ctx.startActivity(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUEST_ADD_RECEIPT:
				if(resultCode == Activity.RESULT_OK) {
					Receipt r = Receipt.find(data.getStringExtra(EditReceiptActivity.KEY_RECEIPT_UUID));
					if(r != null) {
						WeakReference<ReceiptMonthlyView> viewRef = views.get(r.getDate());
						if (viewRef != null) {
							ReceiptMonthlyView view = viewRef.get();
							if (view != null)
								view.addReceipt(r);
						}
					}
				}
				break;
		}
	}

	@Override
	public void filter(String s) {
		super.filter(s);
		DateTime date = adapter.getDate(pager.getCurrentItem());
		ReceiptMonthlyView view = getMonthView(date);
		if (view != null)
			view.filter(filter);
	}

	private @Nullable ReceiptMonthlyView getMonthView(DateTime date) {
		WeakReference<ReceiptMonthlyView> viewRef = views.get(date);
		if (viewRef != null)
			return viewRef.get();
		return null;
	}
}