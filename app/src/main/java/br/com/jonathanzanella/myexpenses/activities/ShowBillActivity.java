package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Bill;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowBillActivity extends BaseActivity {
	public static final String KEY_BILL_ID = "KeyBillId";

	@Bind(R.id.act_show_bill_name)
	TextView billName;
	@Bind(R.id.act_show_bill_amount)
	TextView billAmount;
	@Bind(R.id.act_show_bill_due_date)
	TextView billDueDate;

	private Bill bill;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		if (bill != null) {
			billName.setText(bill.getName());
			billAmount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
			billDueDate.setText(String.valueOf(bill.getDueDate()));
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_BILL_ID))
			bill = Bill.find(extras.getLong(KEY_BILL_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_BILL_ID, bill.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(bill != null) {
			bill = Bill.find(bill.getId());
			setData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditBillActivity.class);
				i.putExtra(EditBillActivity.KEY_BILL_ID, bill.getId());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}