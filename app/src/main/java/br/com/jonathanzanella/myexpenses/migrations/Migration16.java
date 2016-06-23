package br.com.jonathanzanella.myexpenses.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.models.Expense;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 16, database = MyDatabase.class)
public class Migration16 extends AlterTableMigration<Expense> {

	public Migration16() {
		super(Expense.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "chargeableUuid");
	}
}