/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.core.session;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class TestSession
{
  @Test
  void testAttributeLifeCycle()
  {
    Session session = new Session();

    session.setAttribute("bob", "alice");
    assertThat(session.getAttribute("bob"), is("alice"));

    session.removeAttribute("bob");
    assertThat(session.getAttribute("bob"), is(nullValue()));
  }

  @Test
  void testLongTermAttributeLifeCycle()
  {
    Session session = new Session();

    session.setLongTermAttribute("bobLT", "aliceLT");
    assertThat(session.getLongTermAttribute("bobLT"), is("aliceLT"));

    session.removeLongTermAttribute("bobLT");
    assertThat(session.getLongTermAttribute("bobLT"), is(nullValue()));
  }

  @Test
  void testReset()
  {
    Session session = new Session();

    session.setAttribute("bob", "alice");
    session.setLongTermAttribute("bobLT", "aliceLT");

    session.reset();

    assertThat(session.getLongTermAttribute("bob"), is(nullValue()));
    assertThat(session.getLongTermAttribute("bobLT"), is("aliceLT"));
  }

  @Test
  void testResetAll()
  {
    Session session = new Session();

    session.setAttribute("bob", "alice");
    session.setLongTermAttribute("bobLT", "aliceLT");

    session.resetAll();

    assertThat(session.getLongTermAttribute("bob"), is(nullValue()));
    assertThat(session.getLongTermAttribute("bobLT"), is(nullValue()));
  }
}
