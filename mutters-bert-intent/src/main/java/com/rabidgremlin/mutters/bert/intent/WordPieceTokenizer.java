package com.rabidgremlin.mutters.bert.intent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;

/**
 * Wordpiece tokenizer implementation.
 * <p>
 * Based on: <a
 * href=https://github.com/google-research/bert/blob/master/tokenization.py>https://github.com/google-research/bert/blob/master/tokenization.py</a>
 * (class WordpieceTokenizer).
 *
 * @author LaurenceTews
 * @author wilmol
 */
public class WordPieceTokenizer
{
  private static final String CLS_TOKEN = "[CLS]";

  private static final String UNK_TOKEN = "[UNK]";

  private static final String SEP_TOKEN = "[SEP]";

  private static final int MAX_INPUT_CHARS_PER_WORD = 30;

  private final Logger log = LoggerFactory.getLogger(WordPieceTokenizer.class);

  private final DoccatModel model;

  public WordPieceTokenizer(DoccatModel model)
  {
    this.model = checkNotNull(model);
  }

  /**
   * Tokenizes the text using word piece tokenization.
   * <p>
   * Note: if the input text (number of words) exceeds the max seq length of the model the result will be truncated on
   * the right.
   *
   * @param text a single token or whitespace separated tokens
   * @return list of wordpiece tokens
   */
  public ImmutableList<String> tokenize(String text)
  {
    log.debug("Received: {}", text);
    checkArgument(model.maxSeqLength() > 2);

    String splitText = splitOnPunctuation(text.toLowerCase());

    List<String> outputTokens = new ArrayList<>();

    outputTokens.add(CLS_TOKEN);

    String[] tokens = splitText.split(" ");

    outer: for (String token : tokens)
    {
      if (token.length() > MAX_INPUT_CHARS_PER_WORD)
      {
        outputTokens.add(UNK_TOKEN);
        if (outputTokens.size() == model.maxSeqLength() - 1)
        {
          break outer;
        }
      }
      else
      {
        // TODO(wilmol) some strange things here... can simplify
        StringBuilder chars = new StringBuilder(token);
        int start = 0;

        while (start < chars.length())
        {
          int end = chars.length();
          String currentSubString = null;

          while (start < end)
          {
            String subString = chars.substring(start, end);

            if (start > 0)
            {
              subString = "##" + subString;
            }

            if (model.vocab().containsKey(subString))
            {
              currentSubString = subString;
              break;
            }
            end -= 1;
          }

          if (currentSubString != null)
          {
            outputTokens.add(currentSubString);
            if (outputTokens.size() == model.maxSeqLength() - 1)
            {
              break outer;
            }
            start = end;
          }
          else
          {
            outputTokens.add(UNK_TOKEN);
            if (outputTokens.size() == model.maxSeqLength() - 1)
            {
              break outer;
            }
            break;
          }
        }
      }
    }

    outputTokens.add(SEP_TOKEN);

    log.debug("Result: {}", outputTokens);
    return ImmutableList.copyOf(outputTokens);
  }

  private static String splitOnPunctuation(String text)
  {
    char[] chars = text.toCharArray();

    int i = 0;
    StringBuilder output = new StringBuilder();

    while (i < chars.length)
    {
      char currChar = chars[i];

      if (isPunctuation(currChar))
      {
        output.append(" ").append(currChar).append(" ");
      }
      else
      {
        output.append(currChar);
      }

      i += 1;
    }
    return output.toString();
  }

  private static boolean isPunctuation(char cp)
  {
    // treat all non ascii as punctuation
    if (!CharMatcher.ascii().matches(cp))
    {
      return true;
    }
    return (cp >= 33 && cp <= 47) || (cp >= 58 && cp <= 64) || (cp >= 91 && cp <= 96) || (cp >= 123 && cp <= 126);
  }
}
