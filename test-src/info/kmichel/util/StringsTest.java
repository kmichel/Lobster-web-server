package info.kmichel.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class StringsTest {

	private static final Collection<String> sample = Arrays.asList(
		"foo",
		"bar",
		"baz");

	@Test(expected=InstantiationException.class)
	public void testInstantiation()
			throws InstantiationException, IllegalAccessException {
		Strings.class.newInstance();
	}

	@Test
	public void testEmptyCollectionJoin() {
		final Collection<String> empty = new ArrayList<String>();
		assertEquals(Strings.join(empty, "SEPARATOR"), "");
	}

	@Test
	public void testSingletonCollectionJoin() {
		final Collection<String> singleton = Collections.singletonList("singleton");
		assertEquals(Strings.join(singleton, "SEPARATOR"), "singleton");
	}

	@Test
	public void testCollectionJoin() {
		assertEquals(Strings.join(sample, ""), "foobarbaz");
	}

	@Test
	public void testCollectionWithSeparatorJoin() {
		assertEquals(Strings.join(sample, " "), "foo bar baz");
	}

	@Test
	public void testNullSeparatorArgumentJoin() {
		assertEquals(Strings.join(sample, null), Strings.join(sample, ""));
	}

	@Test
	public void testConsume() throws IOException, UnsupportedEncodingException {
		final String test = "«1234567890→aeiuoy&éèà»";
		final byte[] buffer = test.getBytes("utf-8");
		final InputStream inputStream = new ByteArrayInputStream(buffer);
		final String result = Strings.consume(inputStream);
		assertEquals(result, test);
	}
}
