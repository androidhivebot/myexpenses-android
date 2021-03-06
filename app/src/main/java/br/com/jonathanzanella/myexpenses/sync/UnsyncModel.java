package br.com.jonathanzanella.myexpenses.sync;

import android.content.Context;

/**
 * Created by jzanella on 6/6/16.
 */
public interface UnsyncModel {
	boolean isSaved();
	String getServerId();
	String getUuid();
	void setServerId(String serverId);
	long getCreatedAt();
	void setCreatedAt(long createdAt);
	long getUpdatedAt();
	void setUpdatedAt(long updatedAt);
	String getData();
	void syncAndSave(UnsyncModel serverModel);

	String getHeader(Context ctx);

	UnsyncModelApi<UnsyncModel> getServerApi();
}