/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.opennlp.intent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import org.junit.jupiter.api.Test;

class TestCategorization
{

  @Test
  void testModelLoad() throws Exception
  {
    URL modelUrl = Thread.currentThread().getContextClassLoader().getResource("models/en-cat-taxi-intents.bin");
    assertThat(modelUrl, is(notNullValue()));

    DoccatModel model = new DoccatModel(modelUrl);
    assertThat(model, is(notNullValue()));
  }

  @Test
  void testCategorization() throws Exception
  {
    URL modelUrl = Thread.currentThread().getContextClassLoader().getResource("models/en-cat-taxi-intents.bin");
    assertThat(modelUrl, is(notNullValue()));

    DoccatModel model = new DoccatModel(modelUrl);
    assertThat(model, is(notNullValue()));

    DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
    // model was built with OpenNLP whitespace tokenizer
    OpenNLPTokenizer tokenizer = new OpenNLPTokenizer(WhitespaceTokenizer.INSTANCE);

    String category = myCategorizer.getBestCategory(myCategorizer.categorize(tokenizer.tokenize("Order me a taxi")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("OrderTaxi"));

    category = myCategorizer.getBestCategory(myCategorizer.categorize(tokenizer.tokenize("Send me a taxi")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("OrderTaxi"));

    category = myCategorizer
        .getBestCategory(myCategorizer.categorize(tokenizer.tokenize("Send a taxi to 12 Pleasent Street")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("OrderTaxi"));

    category = myCategorizer.getBestCategory(myCategorizer.categorize(tokenizer.tokenize("Cancel my cab")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("CancelTaxi"));

    category = myCategorizer.getBestCategory(myCategorizer.categorize(tokenizer.tokenize("Where is my taxi ?")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("WhereTaxi"));

    category = myCategorizer
        .getBestCategory(myCategorizer.categorize(tokenizer.tokenize("The address is 136 River Road")));
    assertThat(category, is(notNullValue()));
    assertThat(category, is("GaveAddress"));
  }

}
