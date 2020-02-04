/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.Slots;
import com.rabidgremlin.mutters.slots.LiteralSlot;
import com.rabidgremlin.mutters.templated.SimpleTokenizer;
import com.rabidgremlin.mutters.templated.TemplatedUtterance;
import com.rabidgremlin.mutters.templated.TemplatedUtteranceMatch;

class TestLiteralSlot
{
  private final SimpleTokenizer tokenizer = new SimpleTokenizer();

  @Test
  void testBasicMatch()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("My name is {name}"));

    String[] input = tokenizer.tokenize("My Name is Kilroy Jones");
    Slots slots = new Slots();
    Context context = new Context();

    LiteralSlot slot = new LiteralSlot("name");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));
    assertThat(match.getSlotMatches().size(), is(1));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    assertThat(slotMatch, is(notNullValue()));
    assertThat(slotMatch.getOriginalValue(), is("Kilroy Jones"));
    assertThat(slotMatch.getValue(), is("kilroy jones"));
  }

  @Test
  void testMidUtteranceMatch()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("The {something} is good"));

    String[] input = tokenizer.tokenize("The pinot noir is good");
    Slots slots = new Slots();
    Context context = new Context();

    LiteralSlot slot = new LiteralSlot("something");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));
    assertThat(match.getSlotMatches().size(), is(1));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    assertThat(slotMatch, is(notNullValue()));
    assertThat(slotMatch.getOriginalValue(), is("pinot noir"));
    assertThat(slotMatch.getValue(), is("pinot noir"));
  }

}
