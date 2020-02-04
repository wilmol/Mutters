/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * @author wilmol
 */
class AbstractSlotTest
{
  private static final class TestSlot extends AbstractSlot<String>
  {
    protected TestSlot(String name)
    {
      super(name);
    }

    @Override
    public Optional<SlotMatch<String>> match(String token, Context context)
    {
      return Optional.empty();
    }
  }

  @Test
  void testGetName()
  {
    Slot<String> slot = new TestSlot("my-slot");
    assertThat(slot.getName(), is("my-slot"));
  }

  @Test
  void testToString()
  {
    Slot<String> slot = new TestSlot("my-slot");
    assertThat(slot.toString(), is("TestSlot [name=my-slot]"));
  }

  @Test
  void testEquals()
  {
    Slot<String> slot = new TestSlot("my-slot");
    Slot<String> slotWithSameName = new TestSlot("my-slot");
    Slot<String> slotWithDifferentName = new TestSlot("my-slot-2");
    assertEquals(slot, slotWithSameName);
    assertNotEquals(slot, slotWithDifferentName);
  }

  @Test
  void testHashCode()
  {
    Slot<String> slot = new TestSlot("my-slot");
    assertEquals(slot.hashCode(), Objects.hash("my-slot"));
  }
}
