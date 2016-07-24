package br.com.jonathanzanella.myexpenses.accounts;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddAccountTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(getContext());
	}

	@Test
	public void addNewAccount() {
		activityTestRule.launchActivity(new Intent());
		UIHelper.openMenuAndClickItem(R.string.accounts);

		final String accountsTitle = getContext().getString(R.string.accounts);
		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.view_accounts_fab)).perform(click());

		final String newAccountTitle = getContext().getString(R.string.new_account_title);
		UIHelper.matchToolbarTitle(newAccountTitle);

		final String accountTitle = "Test";
		onView(withId(R.id.act_edit_account_name)).perform(typeText(accountTitle));
		onView(withId(R.id.act_edit_account_balance)).perform(typeText("100"));
		onView(withId(R.id.action_save)).perform(click());

		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.row_account_name)).check(matches(withText(accountTitle)));
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
