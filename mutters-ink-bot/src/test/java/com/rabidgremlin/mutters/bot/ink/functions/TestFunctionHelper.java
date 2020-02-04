/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bot.ink.functions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test for parsing of function strings.
 * 
 * @author rabidgremlin
 *
 */
class TestFunctionHelper
{

  @Test
  void testParameterParsing()
  {
    FunctionDetails details = FunctionHelper
        .parseFunctionString("type::link url::https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot title::Here is the link");

    assertThat(details, is(notNullValue()));

    assertThat(details.getFunctionData(),
        is("type::link url::https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot title::Here is the link"));

    assertThat(details.getFunctionParams(), is(notNullValue()));
    assertThat(details.getFunctionParams().get("type"), is("link"));
    assertThat(details.getFunctionParams().get("url"), is("https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot"));
    assertThat(details.getFunctionParams().get("title"), is("Here is the link"));
  }

  @Test
  void testParameterParsingValueWithSpaces() throws Exception
  {
    FunctionDetails details = FunctionHelper
        .parseFunctionString("title::Here is the link type::link url::https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot ");

    assertThat(details, is(notNullValue()));

    assertThat(details.getFunctionData(),
        is("title::Here is the link type::link url::https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot"));

    assertThat(details.getFunctionParams(), is(notNullValue()));
    assertThat(details.getFunctionParams().get("type"), is("link"));
    assertThat(details.getFunctionParams().get("url"), is("https:\\\\/\\\\/en.wikipedia.org/wiki/Chatbot"));
    assertThat(details.getFunctionParams().get("title"), is("Here is the link"));
  }

  @Test
  void testParameterNoParams()
  {
    FunctionDetails details = FunctionHelper.parseFunctionString(" dd/mm or next friday ");

    assertThat(details, is(notNullValue()));

    assertThat(details.getFunctionData(), is("dd/mm or next friday"));

    assertThat(details.getFunctionParams(), is(nullValue()));
  }

  @Test
  void testBadParams()
  {
    assertThrows(IllegalArgumentException.class, () -> FunctionHelper.parseFunctionString("junk::ob:::"));
  }

}
