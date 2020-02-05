/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bert.doccat;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.Int64List;

import com.google.common.primitives.Longs;
import com.rabidgremlin.mutters.bert.tokenize.Tokenizer;

/**
 * Extracts features from text for document categorization. Only extracts
 * features for tokens in the models vocab.
 * <p>
 * Based on: <a
 * href=https://github.com/google-research/bert/blob/master/extract_features.py>https://github.com/google-research/bert/blob/master/extract_features.py</a>
 *
 * @author LaurenceTews
 * @author wilmol
 */
public class FeatureExtractor
{
  private final Logger log = LoggerFactory.getLogger(FeatureExtractor.class);

  private final DoccatModel model;

  private final Tokenizer wordPieceTokenizer;

  public FeatureExtractor(DoccatModel model, Tokenizer wordPieceTokenizer)
  {
    this.model = checkNotNull(model);
    this.wordPieceTokenizer = checkNotNull(wordPieceTokenizer);
  }

  /**
   * Extracts the features from the text.
   * <p>
   * Specifically: {@code segment_ids}, {@code label_ids}, {@code input_ids}, and
   * {@code input_mask}.
   * <p>
   * Note: if the input text (number of words) exceeds the {@code max_seq_length}
   * of the model the result will be truncated on the right.
   *
   * @param text text to extract features from
   * @return extracted features
   */
  public Features extractFeatures(String text)
  {
    log.debug("Received: {}", text);
    long[] inputIds = new long[model.maxSeqLength()];
    long[] inputMasks = new long[model.maxSeqLength()];

    List<String> tokens = wordPieceTokenizer.tokenize(text);
    verify(tokens.size() <= model.maxSeqLength(), "tokens.size() > max_seq_length (%s)", model.maxSeqLength());

    for (int i = 0; i < tokens.size(); i++)
    {
      // TODO(wilmol) NPE if the token isnt in the vocab
      inputIds[i] = model.vocab().get(tokens.get(i));
      inputMasks[i] = 1;
    }

    return Features.newBuilder().putFeature("segment_ids", createFeatureFrom(new long[model.maxSeqLength()]))
        .putFeature("label_ids", createFeatureFrom(1)).putFeature("input_ids", createFeatureFrom(inputIds))
        .putFeature("input_mask", createFeatureFrom(inputMasks)).build();
  }

  private static Feature createFeatureFrom(long... values)
  {
    Int64List list = Int64List.newBuilder().addAllValue(Longs.asList(values)).build();
    return Feature.newBuilder().setInt64List(list).build();
  }
}
