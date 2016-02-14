package br.com.jonathanzanella.myexpenses.models;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.converter.DateTimeConverter;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
@Table(database = MyDatabase.class)
public class Account extends BaseModel implements Chargeable {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    @Column
    @PrimaryKey(autoincrement = true) @Getter
    long id;

    @Column @Getter @Setter
    String name;

    @Column @Getter @Setter
    int balance;

    @Column(typeConverter = DateTimeConverter.class) @Getter @Setter
    DateTime balanceDate;

    @Column @Getter @Setter
    boolean accountToPayCreditCard;

    public static List<Account> all() {
        return initQuery().queryList();
    }

    private static From<Account> initQuery() {
        return SQLite.select().from(Account.class);
    }

    public static Account find(long id) {
        return initQuery().where(Account_Table.id.eq(id)).querySingle();
    }

    public void credit(int value) {
        balance += value;
    }

    @Override
    public ChargeableType getChargeableType() {
        return ChargeableType.ACCOUNT;
    }

    @Override
    public boolean canBePaidNextMonth() {
        return false;
    }

    @Override
    public void debit(int value) {
        balance -= value;
    }

    public @Nullable Card getDebitCard() {
        return Card.accountDebitCard(this);
    }
}
