/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bot.statemachine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.bot.BotException;
import com.rabidgremlin.mutters.core.bot.BotResponse;
import com.rabidgremlin.mutters.core.session.Session;

class TestTaxiStateMachineBot
{
  private static TaxiStateMachineBot taxiBot;

  @BeforeAll
  static void setUpBot()
  {
    taxiBot = new TaxiStateMachineBot(new TaxiStateMachineBotConfiguration());
  }

  @Test
  void testOrderTaxiWithAddress() throws BotException
  {
    Session session = new Session();
    Context context = new Context();

    BotResponse response = taxiBot.respond(session, context, "Send a taxi to 56 Kilm Steet");

    assertThat(response, is(notNullValue()));
    assertThat(response.getResponse(), is("Taxi 1e1f is on its way"));
    assertThat(response.isAskResponse(), is(false));
  }

  @Test
  void testOrderTaxiWithOutAddress() throws BotException
  {
    Session session = new Session();
    Context context = new Context();

    BotResponse response = taxiBot.respond(session, context, "Order me a taxi");

    assertThat(response, is(notNullValue()));
    assertThat(response.getResponse(), is("What is the pick up address ?"));
    assertThat(response.isAskResponse(), is(true));

    response = taxiBot.respond(session, context, "136 River Road");

    assertThat(response, is(notNullValue()));
    assertThat(response.getResponse(), is("Taxi 1983 is on its way"));
    assertThat(response.isAskResponse(), is(false));
  }

  @Test
  void testCancelTaxi() throws BotException
  {
    Session session = new Session();
    Context context = new Context();

    BotResponse response = taxiBot.respond(session, context, "Cancel my cab order");

    assertThat(response, is(notNullValue()));
    assertThat(response.getResponse(), is("Your taxi has been cancelled"));
    assertThat(response.isAskResponse(), is(false));
  }

  @Test
  void testTaxiStatus() throws BotException
  {
    Session session = new Session();
    Context context = new Context();

    BotResponse response = taxiBot.respond(session, context, "Where is my ride ?");

    assertThat(response, is(notNullValue()));
    assertThat(response.getResponse(), is("Your taxi is about 7 minutes away"));
    assertThat(response.isAskResponse(), is(false));
  }
}
