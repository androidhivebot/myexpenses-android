package br.com.jonathanzanella.myexpenses;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.model.Account;
import br.com.jonathanzanella.myexpenses.model.Card;
import br.com.jonathanzanella.myexpenses.model.CardType;
import br.com.jonathanzanella.myexpenses.model.Source;

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		FlowManager.init(this);
		JodaTimeAndroid.init(this);

		//noinspection PointlessBooleanExpression
		if(Environment.IS_DEBUG && Account.all().size() == 0) {
			Account bankAcc = new Account();
			bankAcc.setBalance(100000);
			bankAcc.setBalanceDate(DateTime.now());
			bankAcc.setName("Banco");
			bankAcc.save();
			Account a = new Account();
			a.setBalance(1000);
			a.setBalanceDate(DateTime.now());
			a.setName("Bolso");
			a.save();
			a = new Account();
			a.setBalance(300000);
			a.setBalanceDate(DateTime.now());
			a.setName("Amigo");
			a.save();

			Source s = new Source();
			s.setName("Empresa");
			s.save();
			s = new Source();
			s.setName("Namorada");
			s.save();
			s = new Source();
			s.setName("Sogra");
			s.save();
			s = new Source();
			s.setName("Irmã");
			s.save();
			s = new Source();
			s.setName("Amigo");
			s.save();

			Card c = new Card();
			c.setName("Crédito");
			c.setType(CardType.CREDIT);
			c.setAccount(bankAcc);
			c.save();
			c = new Card();
			c.setName("Débito");
			c.setType(CardType.DEBIT);
			c.setAccount(bankAcc);
			c.save();
		}
	}
}