package br.com.jonathanzanella.myexpenses.adapters;

import br.com.jonathanzanella.myexpenses.models.Account;

/**
 * Created by jzanella on 2/1/16.
 */
public interface AccountAdapterCallback {
	void onAccountSelected(Account account);
}