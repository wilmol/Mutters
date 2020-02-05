/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.templated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.MatcherScores;
import com.rabidgremlin.mutters.core.NoIntentMatch;
import com.rabidgremlin.mutters.core.Tokenizer;

/**
 * This is an IntentMatcher that matches against utterance templates.
 * 
 * @author rabidgremlin
 *
 */
public class TemplatedIntentMatcher implements IntentMatcher
{
  private final List<TemplatedIntent> intents = new ArrayList<>();

  private final Tokenizer tokenizer;

  /**
   * Constructor.
   * 
   * @param tokenizer The tokenizer to use for parsing users inpout and utterance
   *                  templates.
   */
  public TemplatedIntentMatcher(Tokenizer tokenizer)
  {
    this.tokenizer = tokenizer;

    // Check that tokenizer preserves slot identifiers
    String[] tokens = tokenizer.tokenize("{City} {Date}");
    if (tokens == null || tokens.length != 2 || !tokens[0].equalsIgnoreCase("{city}")
        || !tokens[1].equalsIgnoreCase("{date}"))
    {
      throw new IllegalArgumentException("Invalid tokenizer. It removes slot identifiers in {}s");
    }
  }

  /**
   * Adds a new intent to the matcher.
   * 
   * @param name The name of the intent.
   * @return The new TemplatedIntent.
   */
  public TemplatedIntent addIntent(String name)
  {
    TemplatedIntent intent = new TemplatedIntent(name, tokenizer);
    intents.add(intent);
    return intent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.rabidgremlin.mutters.core.IntentMatcher#match(java.lang.String,
   * com.rabidgremlin.mutters.core.Context, Set<String> expectedIntents)
   */
  @Override
  public IntentMatch match(String utterance, Context context, Set<String> expectedIntents)
  {
    // utterance is blank, nothing to match on
    if (StringUtils.isBlank(utterance))
    {
      return new NoIntentMatch(utterance);
    }

    String[] cleanedUtterance = tokenizer.tokenize(utterance);

    // do we have some tokens after cleaning ?
    if (cleanedUtterance.length == 0)
    {
      return new NoIntentMatch(utterance);
    }

    for (TemplatedIntent intent : intents)
    {
      // If given null list of expected intents, expected to try match regardless
      if (expectedIntents == null || expectedIntents.contains(intent.getName()))
      {
        TemplatedUtteranceMatch utteranceMatch = intent.matches(cleanedUtterance, context);
        if (utteranceMatch.isMatched())
        {
          // if match with a templated match then the score is 100%
          MatcherScores scores = new MatcherScores();
          scores.addScore(intent.getName(), 1.0);

          return new IntentMatch(intent, utteranceMatch.getSlotMatches(), utterance, scores);
        }
      }
    }

    return new NoIntentMatch(utterance);
  }

}
