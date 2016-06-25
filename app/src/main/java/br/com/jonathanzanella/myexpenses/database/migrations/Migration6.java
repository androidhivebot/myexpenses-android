package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.source.Source;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 6, database = MyDatabase.class)
public class Migration6 extends AlterTableMigration<Source> {

	public Migration6() {
		super(Source.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "serverId");
		addColumn(SQLiteType.INTEGER, "createdAt");
		addColumn(SQLiteType.INTEGER, "updatedAt");
		addColumn(SQLiteType.INTEGER, "sync");
	}
}