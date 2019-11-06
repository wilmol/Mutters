package com.rabidgremlin.mutters.bert.intent;

import com.rabidgremlin.mutters.core.Tokenizer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adapts a bert {@link com.rabidgremlin.mutters.bert.tokenize.Tokenizer} into a mutters {@link Tokenizer}.
 *
 * @author wilmol
 */
public class BertTokenizer
    implements Tokenizer
{
  private final com.rabidgremlin.mutters.bert.tokenize.Tokenizer bertTokenizer;

  public BertTokenizer(com.rabidgremlin.mutters.bert.tokenize.Tokenizer bertTokenizer)
  {
    this.bertTokenizer = checkNotNull(bertTokenizer);
  }

  @Override
  public String[] tokenize(String text)
  {
    return bertTokenizer.tokenize(text).toArray(new String[0]);
  }
}
