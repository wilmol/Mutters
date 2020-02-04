/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bot.ink;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.bot.BotException;
import com.rabidgremlin.mutters.core.session.Session;
import com.rabidgremlin.mutters.core.util.SessionUtils;

class TestBadStoryState
{
  private static TaxiInkBot taxiBot;

  // copied from InkBotSessionUtils logic
  private static final String INK_STORY_STATE_KEY = SessionUtils.SLOT_PREFIX + "0987654321STORYSTATE1234567890";

  @BeforeAll
  static void setUpBot()
  {
    taxiBot = new TaxiInkBot(new TaxiInkBotConfiguration());
  }

  @Test
  void testCorruptStoryState() throws BotException
  {
    Session session = new Session();
    Context context = new Context();

    // do a call that results in state
    taxiBot.respond(session, context, "Order me a taxi");

    String stateJson = (String) session.getAttribute(INK_STORY_STATE_KEY);
    assertThat(stateJson).isNotNull();

    // corrupt the state
    session.setAttribute(INK_STORY_STATE_KEY, "{junk}");

    try
    {
      // this call should fail
      taxiBot.respond(session, context, "136 River Road");
      fail("Exception should have been thrown");
    }
    catch (BotException e)
    {
      // check that cause was BadInkStoryState
      if (!e.getCause().getClass().equals(BadInkStoryState.class))
      {
        fail("BadInkStoryState exception expected");
      }
    }

  }
}
