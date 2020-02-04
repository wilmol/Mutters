/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.slots;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.rabidgremlin.mutters.core.SlotMatch;

/**
 * @author wilmol
 */
class FuzzySlotTest
{

  @Test
  void testConstructWithArrayDefaultTolerance()
  {
    FuzzySlot fuzzySlot = new FuzzySlot("fuzzy-slot", "optionA", "optionB");

    Optional<SlotMatch<String>> match = fuzzySlot.match("optiona", null);

    assertTrue(match.isPresent());
    assertThat(match.get().getOriginalValue(), is("optiona"));
    assertThat(match.get().getValue(), is("optionA"));
    assertThat(match.get().getSlot(), is(fuzzySlot));
  }

  @Test
  void testConstructWithListDefaultTolerance()
  {
    List<String> options = Arrays.asList("optionA", "optionB");
    FuzzySlot fuzzySlot = new FuzzySlot("fuzzy-slot", options);

    Optional<SlotMatch<String>> match = fuzzySlot.match("optiona", null);

    assertTrue(match.isPresent());
    assertThat(match.get().getOriginalValue(), is("optiona"));
    assertThat(match.get().getValue(), is("optionA"));
    assertThat(match.get().getSlot(), is(fuzzySlot));
  }

  @Test
  void testConstructWithArrayCustomTolerance()
  {
    String[] options = { "optionA", "optionB" };
    FuzzySlot fuzzySlot = new FuzzySlot("fuzzy-slot", options, 0.99);

    Optional<SlotMatch<String>> match = fuzzySlot.match("optiona", null);

    assertTrue(match.isPresent());
    assertThat(match.get().getOriginalValue(), is("optiona"));
    assertThat(match.get().getValue(), is("optionA"));
    assertThat(match.get().getSlot(), is(fuzzySlot));
  }

  @Test
  void testConstructWithListCustomTolerance()
  {
    List<String> options = Arrays.asList("optionA", "optionB");
    FuzzySlot fuzzySlot = new FuzzySlot("fuzzy-slot", options, 0.99);

    Optional<SlotMatch<String>> match = fuzzySlot.match("optiona", null);

    assertTrue(match.isPresent());
    assertThat(match.get().getOriginalValue(), is("optiona"));
    assertThat(match.get().getValue(), is("optionA"));
    assertThat(match.get().getSlot(), is(fuzzySlot));
  }

  @Test
  void testConstructWithArrayRejectsToleranceBelow10Percent()
  {
    try
    {
      String[] options = { "optionA", "optionB" };
      new FuzzySlot("fuzzy-slot", options, 0.09);
      fail();
    }
    catch (IllegalArgumentException expected)
    {
      assertThat(expected.getMessage(), is("Invalid tolerance: 0.09"));
    }
  }

  @Test
  void testConstructWithListRejectsToleranceBelow10Percent()
  {
    try
    {
      List<String> options = Arrays.asList("optionA", "optionB");
      new FuzzySlot("fuzzy-slot", options, 0.09);
      fail();
    }
    catch (IllegalArgumentException expected)
    {
      assertThat(expected.getMessage(), is("Invalid tolerance: 0.09"));
    }
  }

  @Test
  void testConstructWithArrayRejectsToleranceAbove100Percent()
  {
    try
    {
      String[] options = { "optionA", "optionB" };
      new FuzzySlot("fuzzy-slot", options, 1.01);
      fail();
    }
    catch (IllegalArgumentException expected)
    {
      assertThat(expected.getMessage(), is("Invalid tolerance: 1.01"));
    }
  }

  @Test
  void testConstructWithListRejectsToleranceAbove100Percent()
  {
    try
    {
      List<String> options = Arrays.asList("optionA", "optionB");
      new FuzzySlot("fuzzy-slot", options, 1.01);
      fail();
    }
    catch (IllegalArgumentException expected)
    {
      assertThat(expected.getMessage(), is("Invalid tolerance: 1.01"));
    }
  }

  @Test
  void testToString()
  {
    List<String> options = Arrays.asList("optionA", "optionB");
    FuzzySlot fuzzySlot = new FuzzySlot("fuzzy-slot", options);

    assertThat(fuzzySlot.toString(), is("FuzzySlot [name=fuzzy-slot, options=[optionA, optionB], tolerance=0.95]"));
  }
}
