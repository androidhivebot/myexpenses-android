package br.com.jonathanzanella.myexpenses.source;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SourceRepositoryTest {
	private SourceRepository repository = new SourceRepository();

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(InstrumentationRegistry.getTargetContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_source() throws Exception {
		Source source = new Source();
		source.setName("test");
		repository.save(source);

		assertThat(source.id, is(not(0L)));
		assertThat(source.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_source() throws Exception {
		Source source = new Source();
		source.setName("test");
		repository.save(source);

		Source loadSource = repository.find(source.getUuid());
		assertThat(loadSource, is(source));
	}

	@Test
	public void load_only_user_sources() throws Exception {
		Source correctSource = new SourceBuilder().name("test").build();
		correctSource.setUserUuid(Environment.CURRENT_USER_UUID);
		repository.save(correctSource);

		Source wrongSource = new SourceBuilder().name("test2").build();
		wrongSource.setUserUuid("wrong");
		repository.save(wrongSource);

		List<Source> sources = repository.userSources();
		assertThat(sources.size(), is(1));
		assertTrue(sources.contains(correctSource));
		assertFalse(sources.contains(wrongSource));
	}

	@Test
	public void load_user_sources_in_alphabetical_order() throws Exception {
		Source sourceB = new SourceBuilder().name("b").build();
		repository.save(sourceB);

		Source sourceA = new SourceBuilder().name("a").build();
		repository.save(sourceA);

		List<Source> sources = repository.userSources();
		assertThat(sources.get(0), is(sourceA));
		assertThat(sources.get(1), is(sourceB));
	}
}