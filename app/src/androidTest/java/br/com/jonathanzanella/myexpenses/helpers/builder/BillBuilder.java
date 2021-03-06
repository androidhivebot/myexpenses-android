package br.com.jonathanzanella.myexpenses.helpers.builder;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.bill.Bill;

/**
 * Created by jzanella on 8/28/16.
 */

public class BillBuilder {
	private String name = "billTest";
	private int amount = 1;
	private int dueDate = 1;
	private DateTime initDate = DateTime.now();
	private DateTime endDate = DateTime.now().plusMonths(1);

	public BillBuilder name(String name) {
		this.name = name;
		return this;
	}

	public BillBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	public BillBuilder dueDate(int dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public BillBuilder initDate(DateTime initDate) {
		this.initDate = initDate;
		return this;
	}

	public BillBuilder endDate(DateTime endDate) {
		this.endDate = endDate;
		return this;
	}

	public Bill build() {
		Bill bill = new Bill();
		bill.setName(name);
		bill.setAmount(amount);
		bill.setDueDate(dueDate);
		bill.setInitDate(initDate);
		bill.setEndDate(endDate);
		return bill;
	}
}
