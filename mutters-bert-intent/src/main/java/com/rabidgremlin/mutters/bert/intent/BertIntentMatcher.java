/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bert.intent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSortedMap.toImmutableSortedMap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.rabidgremlin.mutters.bert.doccat.DocumentCategorizer;
import com.rabidgremlin.mutters.bert.model.LabelAndScore;
import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.MatcherScores;
import com.rabidgremlin.mutters.core.NoIntentMatch;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.SlotMatcher;

/**
 * Bert intent matcher implementation. Essentially adapts a
 * {@link DocumentCategorizer} into a {@link IntentMatcher}.
 *
 * @author LaurenceTews
 * @author wilmol
 */
public class BertIntentMatcher implements IntentMatcher
{
  private final Logger log = LoggerFactory.getLogger(BertIntentMatcher.class);

  private final SlotMatcher slotMatcher;

  private final float botConfidence;

  private final DocumentCategorizer documentCategorizer;

  public BertIntentMatcher(SlotMatcher slotMatcher, float botConfidence, DocumentCategorizer documentCategorizer)
  {
    this.slotMatcher = checkNotNull(slotMatcher);
    checkArgument(Range.closed(0f, 1f).contains(botConfidence));
    this.botConfidence = botConfidence;
    this.documentCategorizer = checkNotNull(documentCategorizer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IntentMatch match(String utterance, Context context, Set<String> expectedIntents)
  {
    // very similar to
    // com.rabidgremlin.mutters.core.ml.AbstractMachineLearningIntentMatcher

    if (StringUtils.isBlank(utterance))
    {
      return new NoIntentMatch("");
    }

    ImmutableSortedMap<Double, SortedSet<String>> scores = documentCategorizer.categorize(utterance).stream()
        .collect(toImmutableSortedMap(Comparator.reverseOrder(), LabelAndScore::predictionScore,
            labelAndScore -> ImmutableSortedSet.of(labelAndScore.label()),
            (BinaryOperator<SortedSet<String>>) ((BiFunction<SortedSet<String>, SortedSet<String>, Set<String>>) Sets::union)
                .andThen((Function<Set<String>, SortedSet<String>>) ImmutableSortedSet::copyOf)));

    if (scores.isEmpty())
    {
      log.warn("No scores received from Bert model");
      return new NoIntentMatch(utterance);
    }

    String topScoringIntent;
    Double highestScore;
    if (expectedIntents == null)
    {
      // if expectedIntents is null, then take highest score regardless
      highestScore = scores.firstKey();
      // TODO what if multiple intents share the same score?
      topScoringIntent = scores.get(highestScore).iterator().next();
    }
    else
    {
      Optional<LabelAndScore> highestIntentScorePair = getHighestScoringExpectedIntent(expectedIntents, scores);
      topScoringIntent = highestIntentScorePair.map(LabelAndScore::label).orElse("");
      highestScore = highestIntentScorePair.map(LabelAndScore::predictionScore).orElse(0d);
    }

    if (highestScore < botConfidence || topScoringIntent.isEmpty())
    {
      log.info("No match found. Highest score was: " + topScoringIntent + " " + highestScore);
      return new NoIntentMatch(utterance, new MatcherScores(scores));
    }

    Intent bestIntent = new Intent(topScoringIntent);

    // do NER
    Map<Slot<?>, SlotMatch<?>> matchedSlots = slotMatcher.match(context, bestIntent, utterance);

    log.info("Matched Intent: " + bestIntent.getName() + " " + highestScore);
    return new IntentMatch(bestIntent, matchedSlots, utterance, new MatcherScores(scores));
  }

  private Optional<LabelAndScore> getHighestScoringExpectedIntent(Set<String> expectedIntents,
      SortedMap<Double, SortedSet<String>> scores)
  {
    for (Map.Entry<Double, SortedSet<String>> entry : scores.entrySet())
    {
      double score = entry.getKey();
      // TODO what if multiple intents share the same score?
      for (String intent : entry.getValue())
      {
        if (expectedIntents.contains(intent))
        {
          return Optional.of(new LabelAndScore(intent, score));
        }
      }
    }
    return Optional.empty();
  }
}
