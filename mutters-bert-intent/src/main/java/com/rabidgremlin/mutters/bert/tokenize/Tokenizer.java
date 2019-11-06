package com.rabidgremlin.mutters.bert.tokenize;

import java.util.List;

/**
 * Tokenization class definition.
 *
 * @author wilmol
 */
public interface Tokenizer
{

  /**
   * Tokenizes the given text.
   *
   * @param text text to tokenize
   * @return list of tokens
   */
  List<String> tokenize(String text);

}
