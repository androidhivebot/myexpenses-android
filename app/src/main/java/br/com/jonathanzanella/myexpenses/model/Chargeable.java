package br.com.jonathanzanella.myexpenses.model;

/**
 * Created by jzanella on 2/2/16.
 */
public interface Chargeable {
	long getId();
	String getName();
	void debit(int value);
	void save();
}
