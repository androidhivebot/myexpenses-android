package br.com.jonathanzanella.myexpenses.models;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.server.SourceApi;
import br.com.jonathanzanella.myexpenses.server.UnsyncModelApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Source extends BaseModel implements UnsyncModel {
    private static final SourceApi sourceApi = new SourceApi();

	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose @SerializedName("_id")
	String serverId;

    @Column @Getter @Setter @Expose @SerializedName("created_at")
    long createdAt;

    @Column @Getter @Setter @Expose @SerializedName("updated_at")
    long updatedAt;

	@Column @Getter @Setter
    boolean sync;

	private static From<Source> initQuery() {
		return SQLite.select().from(Source.class);
	}

	public static List<Source> all() {
		return initQuery().queryList();
	}

    public static List<Source> unsync() {
        return initQuery().where(Source_Table.sync.eq(false)).queryList();
    }

	public static Source find(long id) {
		return initQuery().where(Source_Table.id.eq(id)).querySingle();
	}

    public static long greaterUpdatedAt() {
        Source source = initQuery().orderBy(Source_Table.updatedAt, false).limit(1).querySingle();
        if(source == null)
            return 0L;
        return source.getUpdatedAt();
    }

	@Override
	public String getData() {
		return "name=" + getName();
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.source);
	}

	@Override
    public UnsyncModelApi getServerApi() {
        return sourceApi;
    }
}