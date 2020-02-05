/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bert.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Range;

/**
 * Represents a label and its score. Immutable.
 *
 * @author wilmol
 */
public final class LabelAndScore implements Comparable<LabelAndScore>
{
  private final String label;

  private final double predictionScore;

  public LabelAndScore(String label, double predictionScore)
  {
    this.label = checkNotNull(label);
    checkArgument(Range.closed(0d, 1d).contains(predictionScore), "Expected prediction score between 0 and 1.");
    this.predictionScore = predictionScore;
  }

  @Override
  public int compareTo(LabelAndScore that)
  {
    return Double.compare(this.predictionScore, that.predictionScore);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    LabelAndScore that = (LabelAndScore) o;
    return Double.compare(that.predictionScore, predictionScore) == 0 && Objects.equals(label, that.label);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(label, predictionScore);
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper("").add("label", label).add("predictionScore", predictionScore).toString();
  }

  public String label()
  {
    return label;
  }

  public double predictionScore()
  {
    return predictionScore;
  }
}
