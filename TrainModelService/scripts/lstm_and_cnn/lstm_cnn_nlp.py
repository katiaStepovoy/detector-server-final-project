import pickle

import numpy as np
from keras.models import load_model
from keras.utils import pad_sequences
from nltk import WhitespaceTokenizer
from nltk.corpus import stopwords


def get_model():
    print("enter load_model")
    return load_model("lstm_and_cnn/classify_sentences.h5")


def get_tokenizer():
    print("enter load_tokenizer")
    return pickle.load(open('lstm_and_cnn/token.pk1', 'rb'))


def classify_content(model, model_name, tokenizer, text):
    white_space_tk = WhitespaceTokenizer()
    text_tokens = white_space_tk.tokenize(text)
    stop_words = set(stopwords.words('english'))
    text_tokens = [word for word in text_tokens if word.lower() not in stop_words]
    text = ' '.join(text_tokens)

    sequence = tokenizer.texts_to_sequences([text])
    sequence = np.array(sequence)
    padded_sequence = pad_sequences(sequence, maxlen=60, padding='post')
    preds = model.predict(padded_sequence)
    threshold = 0.5
    y_pred_binary = np.where(preds[:, 1] >= threshold, 1, 0)
    # print(preds)
    predicted_class = "not related" if y_pred_binary == 0 else "related"
    print(f"Predicted class by {model_name}: {predicted_class}")
    return predicted_class
