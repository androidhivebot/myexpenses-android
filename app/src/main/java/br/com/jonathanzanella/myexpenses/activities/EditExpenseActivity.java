package br.com.jonathanzanella.myexpenses.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helper.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.model.Chargeable;
import br.com.jonathanzanella.myexpenses.model.Expense;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import br.com.jonathanzanella.myexpenses.services.CashierService;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditExpenseActivity extends BaseActivity {
	public static final String KEY_EXPENSE_ID = "KeyReceiptId";
	private static final int REQUEST_SELECT_CHARGEABLE = 1003;

	@Bind(R.id.act_edit_expense_name)
	EditText editName;
	@Bind(R.id.act_edit_expense_date)
	EditText editDate;
	@Bind(R.id.act_edit_expense_value)
	EditText editValue;
	@Bind(R.id.act_edit_expense_chargeable)
	EditText editChargeable;

	private Expense expense;
	private DateTime date;
	private Chargeable chargeable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_expense);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		date = DateTime.now();
		onBalanceDateChanged();
		editValue.addTextChangedListener(new CurrencyTextWatch(editValue));

		if(expense != null) {
			editName.setText(expense.getName());
			editValue.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			chargeable = expense.getChargeable();
			editChargeable.setText(chargeable.getName());
			onChargeableSelected();
		} else {
			if(chargeable != null)
				onChargeableSelected();
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras == null)
			return;

		if(extras.containsKey(KEY_EXPENSE_ID))
			expense = Expense.find(extras.getLong(KEY_EXPENSE_ID));

		if(extras.containsKey(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE)) {
			chargeable = Expense.findChargeable(extras.getString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
										extras.getLong(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_ID));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(expense != null)
			outState.putLong(KEY_EXPENSE_ID, expense.getId());
		if(chargeable != null) {
			outState.putLong(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_ID, chargeable.getId());
			outState.putString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, chargeable.getClass().getName());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_CHARGEABLE: {
				if(resultCode == RESULT_OK) {
					chargeable = Expense.findChargeable(
							data.getStringExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
							data.getLongExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_ID, 0L));
					if(chargeable != null)
						onChargeableSelected();
				}
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick(R.id.act_edit_expense_date)
	void onBalanceDate() {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfYear(dayOfMonth);
				onBalanceDateChanged();
			}
		}, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth()).show();
	}

	private void onBalanceDateChanged() {
		editDate.setText(Receipt.sdf.format(date.toDate()));
	}

	private void onChargeableSelected() {
		editChargeable.setText(chargeable.getName());
	}

	@OnClick(R.id.act_edit_expense_chargeable)
	void onChargeable() {
		if(expense == null)
			startActivityForResult(new Intent(this, ListChargeableActivity.class), REQUEST_SELECT_CHARGEABLE);
	}

	private void save() {
		if(expense == null)
			expense = new Expense();
		expense.setName(editName.getText().toString());
		expense.setDate(date);
		expense.setValue(Integer.parseInt(editValue.getText().toString().replaceAll("[^\\d]", "")));
		expense.setChargeable(chargeable);
		expense.save();

		startService(new Intent(this, CashierService.class));

		Intent i = new Intent();
		i.putExtra(KEY_EXPENSE_ID, expense.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
