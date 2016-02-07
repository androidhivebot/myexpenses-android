package br.com.jonathanzanella.myexpenses.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.EditBillActivity;
import br.com.jonathanzanella.myexpenses.adapter.BillsAdapter;
import br.com.jonathanzanella.myexpenses.model.Bill;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class BillView extends BaseView {
	private static final int REQUEST_ADD_BILL = 1003;
	private BillsAdapter adapter;

	@Bind(R.id.view_bills_list)
	RecyclerView bills;

	public BillView(Context context) {
		super(context);
	}

	public BillView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BillView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_bills, this);
		ButterKnife.bind(this);

		adapter = new BillsAdapter();
		adapter.loadData();

		bills.setAdapter(adapter);
		bills.setLayoutManager(new GridLayoutManager(getContext(), 1));
		bills.setItemAnimator(new DefaultItemAnimator());
	}

	@OnClick(R.id.view_bills_fab)
	void onFab() {
		Context ctx = getContext();
		Intent i = new Intent(getContext(), EditBillActivity.class);
		if(ctx instanceof Activity) {
			((Activity) ctx).startActivityForResult(i, REQUEST_ADD_BILL);
		} else {
			ctx.startActivity(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUEST_ADD_BILL:
				if(resultCode == Activity.RESULT_OK) {
					Bill b = Bill.find(data.getLongExtra(EditBillActivity.KEY_BILL_ID, 0L));
					if(b != null)
						adapter.addBill(b);
				}
				break;
		}
	}

	@Override
	public void refreshData() {
		super.refreshData();

		adapter.loadData();
		adapter.notifyDataSetChanged();
	}
}
