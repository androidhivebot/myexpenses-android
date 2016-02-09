package br.com.jonathanzanella.myexpenses.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 2/2/16.
 */
@Table(database = MyDatabase.class)
public class Expense extends BaseModel {
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column(typeConverter = DateTimeConverter.class) @Getter @Setter
	DateTime date;

	@Column
	int value;

	@Column
	int newValue;

	@Column
	long chargeableId;

	@Column
	ChargeableType chargeableType;

	@Column
	long billId;

	@Column @Getter @Setter
	boolean charged;

	@Column @Getter @Setter
	boolean chargeNextMonth;

	@Getter
	private Card creditCard;

	public static List<Expense> all() {
		return initQuery().queryList();
	}

	public static List<Expense> uncharged() {
		return initQuery()
				.where(Expense_Table.charged.eq(false))
				.and(Expense_Table.date.lessThanOrEq(DateTime.now()))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.queryList();
	}

	public static List<Expense> changed() {
		return initQuery()
				.where(Expense_Table.charged.eq(true))
				.and(Expense_Table.newValue.greaterThan(0))
				.queryList();
	}

	public static List<Expense> creditCardBills(Card creditCard, DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initQuery()
									.where(Expense_Table.chargeableId.eq(creditCard.getId()))
									.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
									.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
									.and(Expense_Table.chargeNextMonth.eq(true))
									.and(Expense_Table.charged.eq(false))
									.orderBy(Expense_Table.date, true)
									.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
					.where(Expense_Table.chargeableId.eq(creditCard.getId()))
					.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
					.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
					.and(Expense_Table.chargeNextMonth.eq(false))
					.and(Expense_Table.charged.eq(false))
					.orderBy(Expense_Table.date, true)
					.queryList());

		return bills;
	}

	public static List<Expense> monthly(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return bills;
	}

	public static List<Expense> expenses(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initQuery()
				.where(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeableType.notEq(ChargeableType.CARD))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		DateTime creditCardMonth = date.minusMonths(1);
		for (Card card : Card.creditCards()) {
			int total = 0;
			for (Expense expense : creditCardBills(card, creditCardMonth))
				total += expense.getValue();

			Expense expense = new Expense();
			expense.setChargeable(card);
			expense.setName(MyApplication.getContext().getString(R.string.invoice));
			expense.setDate(creditCardMonth);
			expense.setValue(total);
			expense.creditCard = card;
			bills.add(expense);
		}

		return bills;
	}

	private static From<Expense> initQuery() {
		return SQLite.select().from(Expense.class);
	}

	public static Expense find(long id) {
		return initQuery().where(Expense_Table.id.eq(id)).querySingle();
	}

	public int getValue() {
		if(newValue != 0)
			return newValue;
		return value;
	}

	public void setValue(int value) {
		if(charged && this.value != value)
			newValue = value;
		else
			this.value = value;
	}

	public void resetNewValue() {
		this.value = newValue;
		newValue = 0;
	}

	public int changedValue() {
		return newValue - value;
	}

	public void setChargeable(Chargeable chargeable) {
		chargeableType = chargeable.getChargeableType();
		chargeableId = chargeable.getId();
	}

	public Chargeable getChargeable() {
		return Expense.findChargeable(chargeableType, chargeableId);
	}

	public void setBill(Bill bill) {
		if(bill != null)
			billId = bill.getId();
		else
			billId = 0;
	}

	public Bill getBill() {
		return Bill.find(billId);
	}

	public static Chargeable findChargeable(ChargeableType type, long id) {
		switch (type) {
			case ACCOUNT:
				return Account.find(id);
			case DEBIT_CARD:
			case CARD:
				return Card.find(id);
		}
		return null;
	}

	public void repeat() {
		id = 0;
		date = date.plusMonths(1);
	}
}
