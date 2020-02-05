/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.templated.SimpleTokenizer;
import com.rabidgremlin.mutters.templated.TemplatedIntent;
import com.rabidgremlin.mutters.templated.TemplatedIntentMatcher;

public class TestTemplatedIntentFiltering
{
  private final SimpleTokenizer tokenizer = new SimpleTokenizer();
  TemplatedIntentMatcher matcher;

  @Before
  public void setUpMatcher()
  {
    matcher = new TemplatedIntentMatcher(tokenizer);

    TemplatedIntent intent = matcher.addIntent("HelloIntent");
    intent.addUtterance("hello");
    intent.addUtterance("hi");
    intent.addUtterance("hiya");

    intent = matcher.addIntent("GoodbyeIntent");
    intent.addUtterance("goodbye");
    intent.addUtterance("bye");
  }

  @Test
  public void testNoFiltering()
  {
    // should match on hello intent
    IntentMatch intentMatch = matcher.match("hello", new Context(), null);

    assertThat(intentMatch, is(notNullValue()));
    assertThat(intentMatch.getIntent(), is(notNullValue()));
    assertThat(intentMatch.getIntent().getName(), is("HelloIntent"));

    // should match on goodbye intent
    intentMatch = matcher.match("bye", new Context(), null);

    assertThat(intentMatch, is(notNullValue()));
    assertThat(intentMatch.getIntent(), is(notNullValue()));
    assertThat(intentMatch.getIntent().getName(), is("GoodbyeIntent"));
  }

  @Test
  public void testFiltering()
  {
    HashSet<String> expectedIntents = new HashSet<>();
    expectedIntents.add("HelloIntent");

    // should match on hello intent
    IntentMatch intentMatch = matcher.match("hello", new Context(), expectedIntents);

    assertThat(intentMatch, is(notNullValue()));
    assertThat(intentMatch.getIntent(), is(notNullValue()));
    assertThat(intentMatch.getIntent().getName(), is("HelloIntent"));

    // should not match on goodbye intent (its not in expected intents)
    intentMatch = matcher.match("bye", new Context(), expectedIntents);

    assertThat(intentMatch, is(notNullValue()));
    assertThat(intentMatch.matched(), is(false));
  }
}
