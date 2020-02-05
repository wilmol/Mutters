/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bert.doccat;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;
import org.tensorflow.example.Example;
import org.tensorflow.example.Features;

import com.google.common.collect.ImmutableList;
import com.rabidgremlin.mutters.bert.model.LabelAndScore;

/**
 * Interface to categorize (i.e. classify/label) text using a trained BERT
 * model.
 *
 * @author LaurenceTews
 * @author wilmol
 */
public class DocumentCategorizer
{
  private static final String INPUT_TENSOR_NAME = "foo/input_example_tensor";

  private static final String OUTPUT_TENSOR_NAME = "loss/Softmax";

  private final Logger log = LoggerFactory.getLogger(DocumentCategorizer.class);

  private final DoccatModel model;

  private final FeatureExtractor featureExtractor;

  public DocumentCategorizer(DoccatModel model, FeatureExtractor featureExtractor)
  {
    this.model = checkNotNull(model);
    this.featureExtractor = checkNotNull(featureExtractor);
  }

  /**
   * Categorizes the text according to the BERT model.
   *
   * @param text text
   * @return list of labels, in order, highest prediction score first
   */
  public ImmutableList<LabelAndScore> categorize(String text)
  {
    log.debug("Received: {}", text);
    Session.Runner runner = model.session().runner();

    Features features = featureExtractor.extractFeatures(text);

    Example example = Example.newBuilder().setFeatures(features).build();

    byte[][] exampleBytes = { example.toByteArray() };

    try (Tensor<String> inputTensor = Tensors.create(exampleBytes))
    {
      List<Tensor<?>> outputTensors = runner.feed(INPUT_TENSOR_NAME, inputTensor).fetch(OUTPUT_TENSOR_NAME).run();
      verify(outputTensors.size() == 1, "outputTensors.size() = %s, expected 1", outputTensors.size());

      try (Tensor<?> outputTensor = outputTensors.get(0))
      {
        float[][] template = new float[1][model.labels().size()];
        float[][] copied = outputTensor.copyTo(template);

        return IntStream.range(0, copied[0].length)
            .mapToObj(i -> new LabelAndScore(model.labels().get(i), copied[0][i])).sorted(Comparator.reverseOrder())
            .collect(toImmutableList());
      }
    }
  }
}
