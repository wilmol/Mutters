/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.Slots;
import com.rabidgremlin.mutters.slots.DateSlot;
import com.rabidgremlin.mutters.templated.SimpleTokenizer;
import com.rabidgremlin.mutters.templated.TemplatedUtterance;
import com.rabidgremlin.mutters.templated.TemplatedUtteranceMatch;

public class TestDateSlot
{
  private final SimpleTokenizer tokenizer = new SimpleTokenizer();

  @Test
  public void testBasicMatch()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("for the {date}"));

    String[] input = tokenizer.tokenize("for the 30th May 1974");
    Slots slots = new Slots();
    Context context = new Context();

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));
    assertThat(match.getSlotMatches().size(), is(1));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    assertThat(slotMatch, is(notNullValue()));
    assertThat(slotMatch.getOriginalValue(), is("30th May 1974"));
    assertThat(slotMatch.getValue(), is(LocalDate.of(1974, 5, 30)));
  }

  @Test
  public void testMatchWithTimeZone()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("for the {date}"));

    String[] input = tokenizer.tokenize("for the 30th May 1974");
    Slots slots = new Slots();
    Context context = new Context();
    context.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));
    assertThat(match.getSlotMatches().size(), is(1));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    assertThat(slotMatch, is(notNullValue()));
    assertThat(slotMatch.getOriginalValue(), is("30th May 1974"));
    assertThat(slotMatch.getValue(), is(LocalDate.of(1974, 5, 30)));
  }

  @Test
  public void testDontMatchOnJustTime()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("10pm");
    Slots slots = new Slots();
    Context context = new Context();

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(false));
  }

  @Test
  public void testNZDateFullNumeric()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("20/5/2016");
    Slots slots = new Slots();
    Context context = new Context();
    context.setLocale(new Locale("en", "NZ"));

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();
    assertThat(dateMatch.getDayOfMonth(), is(20));
    assertThat(dateMatch.getMonth().getValue(), is(5));
    assertThat(dateMatch.getYear(), is(2016));
  }

  @Test
  public void testNZDateShortYear()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("20/5/16");
    Slots slots = new Slots();
    Context context = new Context();
    context.setLocale(new Locale("en", "NZ"));

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();
    assertThat(dateMatch.getDayOfMonth(), is(20));
    assertThat(dateMatch.getMonth().getValue(), is(5));
    assertThat(dateMatch.getYear(), is(2016));
  }

  @Test
  public void testNZDateDayMonthOnly()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("20/5");
    Slots slots = new Slots();
    Context context = new Context();
    context.setLocale(new Locale("en", "NZ"));

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();
    assertThat(dateMatch.getDayOfMonth(), is(20));
    assertThat(dateMatch.getMonth().getValue(), is(5));
    assertThat(dateMatch.getYear(), is(LocalDate.now().getYear()));
  }

  @Test
  public void testNZDateDayMonthAsTextOnly()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("1 dec");
    Slots slots = new Slots();
    Context context = new Context();
    context.setLocale(new Locale("en", "NZ"));

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();
    assertThat(dateMatch.getDayOfMonth(), is(1));
    assertThat(dateMatch.getMonth().getValue(), is(12));
    assertThat(dateMatch.getYear(), is(LocalDate.now().getYear()));
  }

  // TODO handle different TZ in context
  @Test
  public void testToday()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("today");
    Slots slots = new Slots();
    Context context = new Context();

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();

    LocalDate today = LocalDate.now();
    assertThat(dateMatch, is(today));
  }

  // TODO handle different TZ in context
  @Test
  public void testTomorrow()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("tomorrow");
    Slots slots = new Slots();
    Context context = new Context();

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(true));

    SlotMatch<?> slotMatch = match.getSlotMatches().get(slot);
    LocalDate dateMatch = (LocalDate) slotMatch.getValue();

    LocalDate tommorrow = LocalDate.now().plusDays(1);
    assertThat(dateMatch, is(tommorrow));
  }

  @Test
  public void testMismatch()
  {
    TemplatedUtterance utterance = new TemplatedUtterance(tokenizer.tokenize("{date}"));

    String[] input = tokenizer.tokenize("book auckland to wellington for friday");
    Slots slots = new Slots();
    Context context = new Context();

    DateSlot slot = new DateSlot("date");
    slots.add(slot);

    TemplatedUtteranceMatch match = utterance.matches(input, slots, context);

    assertThat(match, is(notNullValue()));
    assertThat(match.isMatched(), is(false));
  }

}
