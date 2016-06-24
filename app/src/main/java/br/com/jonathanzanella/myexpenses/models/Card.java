package br.com.jonathanzanella.myexpenses.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.server.CardApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Card extends BaseModel implements Chargeable, UnsyncModel {
	private static final String LOG_TAG = "Card";
	private static final CardApi cardApi = new CardApi();
	@Column
	@PrimaryKey(autoincrement = true)
	long id;

	@Column @Getter @Setter @Expose
	String uuid;

	@Column @Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	CardType type;

	@Column @Getter @Setter @Expose
	String accountUuid;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column @Getter @Setter
	boolean sync;

	public static List<Card> all() {
		return initQuery().queryList();
	}
	public static List<Card> creditCards() {
		return initQuery()
				.where(Card_Table.type.eq(CardType.CREDIT))
				.queryList();
	}

	private static From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	public static Card find(String uuid) {
		return initQuery().where(Card_Table.uuid.eq(uuid)).querySingle();
	}

	public static long greaterUpdatedAt() {
		Card card = initQuery().orderBy(Card_Table.updatedAt, false).limit(1).querySingle();
		if(card == null)
			return 0L;
		return card.getUpdatedAt();
	}

	public static List<Card> unsync() {
		return initQuery().where(Card_Table.sync.eq(false)).queryList();
	}

	public static Card accountDebitCard(Account acc) {
		return initQuery()
				.where(Card_Table.accountUuid.eq(acc.getUuid()))
				.and(Card_Table.type.eq(CardType.DEBIT))
				.querySingle();
	}

	public Account getAccount() {
		return Account.find(accountUuid);
	}

	public void setAccount(Account account) {
		accountUuid = account.getUuid();
	}

	@Override
	public ChargeableType getChargeableType() {
		switch (type) {
			case CREDIT:
				return ChargeableType.CARD;
			case DEBIT:
				return ChargeableType.DEBIT_CARD;
		}

		Log.e(LOG_TAG, "new card type?");
		return ChargeableType.DEBIT_CARD;
	}

	@Override
	public boolean canBePaidNextMonth() {
		return (type == CardType.CREDIT);
	}

	@Override
	public void debit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.debit(value);
			a.save();
		}
	}

	@Override
	public void credit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.credit(value);
			a.save();
		}
	}

	public int getInvoiceValue(DateTime month) {
		int total = 0;
		for (Expense expense : creditCardBills(month))
			total += expense.getValue();

		return total;
	}

	private static From<Expense> initExpenseQuery() {
		return SQLite.select().from(Expense.class);
	}

	public List<Expense> creditCardBills(DateTime date) {
		date = date.withDayOfMonth(1).withMillisOfDay(0);
		DateTime initOfMonth = date.minusMonths(1);
		DateTime endOfMonth = date;

		List<Expense> bills = initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.charged.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = endOfMonth;
		endOfMonth = date.plusMonths(1);

		bills.addAll(initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.charged.eq(false))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return bills;
	}

	@Override
	public void save() {
		if(id == 0 && uuid == null)
			uuid = UUID.randomUUID().toString();
		super.save();
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + name +
				", type=" + type + "" +
				", account=" + accountUuid;
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.cards);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return cardApi;
	}
}
