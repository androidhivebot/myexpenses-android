package br.com.jonathanzanella.myexpenses.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.adapters.MonthlyPagerAdapterBuilder;
import br.com.jonathanzanella.myexpenses.models.Card;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import br.com.jonathanzanella.myexpenses.views.CreditCardInvoiceView;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 08/02/16.
 */
public class CreditCardInvoiceActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_ID = "KeyCreateCardId";
	public static final String KEY_INIT_DATE = "KeyInitDate";

	@Bind(R.id.tabs)
	TabLayout tabs;
	@Bind(R.id.act_credit_card_invoice_pager)
	ViewPager pager;

	private Card card;
	private DateTime initDate;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit_card_invoice);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_CREDIT_CARD_ID))
			card = Card.find(extras.getLong(KEY_CREDIT_CARD_ID));
		if(extras.containsKey(KEY_INIT_DATE))
			initDate = (DateTime) extras.getSerializable(KEY_INIT_DATE);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		MonthlyPagerAdapter adapter = new MonthlyPagerAdapter(this, new MonthlyPagerAdapterBuilder() {
			@Override
			public BaseView buildView(Context ctx, DateTime date) {
				return new CreditCardInvoiceView(ctx, card, date);
			}
		});
		pager.setAdapter(adapter);
		pager.setCurrentItem(adapter.getDatePosition(initDate));
		tabs.setupWithViewPager(pager);
	}
}
