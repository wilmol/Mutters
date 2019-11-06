package com.rabidgremlin.mutters.bert.intent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSortedMap.toImmutableSortedMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.rabidgremlin.mutters.bert.doccat.DocumentCategorizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.rabidgremlin.mutters.core.Context;
import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatch;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.SlotMatch;
import com.rabidgremlin.mutters.core.SlotMatcher;

/**
 * Bert intent matcher implementation. Essentially adapts a {@link DocumentCategorizer} into a {@link IntentMatcher}.
 *
 * @author LaurenceTews
 * @author wilmol
 */
public class BertIntentMatcher
    implements IntentMatcher
{
  /** Debug value key for intent matching scores. */
  private static final String DEBUG_MATCHING_SCORES = "intentMatchingScores";

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
  public IntentMatch match(String utterance, Context context, Set<String> expectedIntents, HashMap<String, Object> debugValues)
  {
    // very similar to AbstractMachineLearningIntentMatcher

    if (StringUtils.isBlank(utterance))
    {
      return null;
    }

    ImmutableSortedMap<Double, Set<String>> scores = documentCategorizer.categorize(utterance)
        .stream()
        .collect(toImmutableSortedMap(
            Comparator.reverseOrder(),
            DocumentCategorizer.LabelAndScore::predictionScore,
            labelAndScore -> ImmutableSet.of(labelAndScore.label()),
            Sets::union));

    if (debugValues != null)
    {
      debugValues.put(DEBUG_MATCHING_SCORES, scores);
    }

    String topScoringIntent;
    Double highestScore;

    if (expectedIntents == null)
    {
      highestScore = scores.firstKey();
      topScoringIntent = scores.get(highestScore).iterator().next();
    }
    else
    {
      Map<String, Double> highestIntentScorePair = getHighestScoringIntent(expectedIntents, scores);
      topScoringIntent = highestIntentScorePair.keySet().iterator().next();
      highestScore = highestIntentScorePair.get(topScoringIntent);
    }

    if (highestScore < botConfidence)
    {
      log.info("No match found. Highest score was: " + topScoringIntent + " " + highestScore);
      return null;
    }

    Intent bestIntent = new Intent(topScoringIntent);

    // do NER
    HashMap<Slot, SlotMatch> matchedSlots = slotMatcher.match(context, bestIntent, utterance);

    log.info("Matched Intent: " + bestIntent.getName() + " " + highestScore);

    return new IntentMatch(bestIntent, matchedSlots, utterance);
  }

  private Map<String, Double> getHighestScoringIntent(Set<String> expectedIntents, SortedMap<Double, Set<String>> scores)
  {
    Map<String, Double> results = new HashMap<>();
    String topScoringIntent = null;
    Double highestScore = 0d;

    for (Map.Entry<Double, Set<String>> entry : scores.entrySet())
    {
      highestScore = entry.getKey();
      topScoringIntent = entry.getValue().iterator().next();

      if (expectedIntents.contains(topScoringIntent))
      {
        break;
      }
    }

    results.put(topScoringIntent, highestScore);
    return results;
  }
}
