package br.com.jonathanzanella.myexpenses.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.models.Bill;
import br.com.jonathanzanella.myexpenses.models.Chargeable;
import br.com.jonathanzanella.myexpenses.models.ChargeableType;
import br.com.jonathanzanella.myexpenses.models.Expense;
import br.com.jonathanzanella.myexpenses.models.Receipt;
import br.com.jonathanzanella.myexpenses.services.CashierService;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditExpenseActivity extends BaseActivity {
	public static final String KEY_EXPENSE_ID = "KeyReceiptId";
	private static final int REQUEST_SELECT_CHARGEABLE = 1003;
	private static final int REQUEST_SELECT_BILL = 1004;

	@Bind(R.id.act_edit_expense_name)
	EditText editName;
	@Bind(R.id.act_edit_expense_date)
	EditText editDate;
	@Bind(R.id.act_edit_expense_value)
	EditText editValue;
	@Bind(R.id.act_edit_expense_repayment)
	CheckBox checkRepayment;
	@Bind(R.id.act_edit_expense_chargeable)
	EditText editChargeable;
	@Bind(R.id.act_edit_expense_bill)
	EditText editBill;
	@Bind(R.id.act_edit_expense_pay_next_month)
	CheckBox checkPayNextMonth;
	@Bind(R.id.act_edit_expense_repetition)
	EditText editRepetition;
	@Bind(R.id.act_edit_expense_installment)
	EditText editInstallment;

	private Expense expense;
	private DateTime date;
	private Chargeable chargeable;
	private Bill bill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_expense);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		editValue.addTextChangedListener(new CurrencyTextWatch(editValue));

		if(expense != null) {
			editName.setText(expense.getName());
			editValue.setText(NumberFormat.getCurrencyInstance().format(Math.abs(expense.getValue()) / 100.0));
			if(expense.getValue() < 0)
				checkRepayment.setChecked(true);
			chargeable = expense.getChargeable();
			if(chargeable != null) {
				editChargeable.setText(chargeable.getName());
				onChargeableSelected();
			}
			checkPayNextMonth.setChecked(expense.isChargeNextMonth());
			date = expense.getDate();
			if(date == null)
				date = DateTime.now();
			onBalanceDateChanged();
		} else {
			date = DateTime.now();
			onBalanceDateChanged();
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
			chargeable = Expense.findChargeable((ChargeableType) extras.getSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
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
			outState.putSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, chargeable.getChargeableType());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_CHARGEABLE: {
				if(resultCode == RESULT_OK) {
					chargeable = Expense.findChargeable(
							(ChargeableType) data.getSerializableExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
							data.getLongExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_ID, 0L));
					if(chargeable != null)
						onChargeableSelected();
				}
				break;
			}
			case REQUEST_SELECT_BILL: {
				if(resultCode == RESULT_OK) {
					bill = Bill.find(data.getLongExtra(ListBillActivity.KEY_BILL_SELECTED_ID, 0L));
					onBillSelected();
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
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onBalanceDateChanged();
			}
		}, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth()).show();
	}

	private void onBalanceDateChanged() {
		editDate.setText(Receipt.sdf.format(date.toDate()));
	}

	private void onChargeableSelected() {
		editChargeable.setText(chargeable.getName());
		checkPayNextMonth.setVisibility(chargeable.canBePaidNextMonth() ? View.VISIBLE : View.GONE);
	}

	@OnClick(R.id.act_edit_expense_chargeable)
	void onChargeable() {
		if(expense == null || expense.getChargeable() == null)
			startActivityForResult(new Intent(this, ListChargeableActivity.class), REQUEST_SELECT_CHARGEABLE);
	}

	private void onBillSelected() {
		if(bill != null) {
			editBill.setText(bill.getName());
			if(editName.getText().toString().isEmpty())
				editName.setText(bill.getName());
			if(editValue.getText().toString().isEmpty())
				editValue.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
		} else {
			editBill.setText("");
		}
	}

	@OnClick(R.id.act_edit_expense_bill)
	void onBill() {
		if(expense == null)
			startActivityForResult(new Intent(this, ListBillActivity.class), REQUEST_SELECT_BILL);
	}

	private void save() {
		int installment = Integer.parseInt(editInstallment.getText().toString());
		if(expense == null)
			expense = new Expense();
		String originalName = editName.getText().toString();
		if(installment == 1)
			expense.setName(originalName);
		else
			expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, 1, installment));
		expense.setDate(date);
		int value = Integer.parseInt(editValue.getText().toString().replaceAll("[^\\d]", "")) / installment;
		if(checkRepayment.isChecked())
			value *= -1;
		expense.setValue(value);
		expense.setChargeable(chargeable);
		expense.setBill(bill);
		expense.setChargeNextMonth(checkPayNextMonth.isChecked());
		expense.save();

		int repetition = installment;
		if(repetition == 1)
			repetition = Integer.parseInt(editRepetition.getText().toString());
		for(int i = 1; i < repetition; i++) {
			if(installment != 1)
				expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i + 1, installment));
			expense.repeat();
			expense.save();
		}

		startService(new Intent(this, CashierService.class));

		Intent i = new Intent();
		i.putExtra(KEY_EXPENSE_ID, expense.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
