# Mutters BERT intent

This is an `IntentMatcher` implementation that uses a Google BERT classification model.

See https://github.com/google-research/bert for more information on BERT.

Specifically has code for:
- Reading a BERT model into memory and loading a session
- Categorizing text using the BERT model (using [TensorFlow Java API](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/java))
- Adapting the model into an `IntentMatcher` so it can be used with the rest of mutters

To train a model see the [mutters-bert-test-data]() module (TODO!)

## Authors
- [Will Molloy](https://github.com/wilmol)
- [Laurence Tews](https://github.com/LaurenceTews)
