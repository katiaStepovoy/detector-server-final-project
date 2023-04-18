# Step 1: Install the required libraries
# !pip install tensorflow
# !pip install transformers

# Step 2: Prepare the dataset
import pandas as pd
from IPython.display import display
import nltk
from nltk.tokenize import word_tokenize
from nltk.stem.porter import PorterStemmer
from nltk.corpus import stopwords

#
# nltk.download('punkt')
# nltk.download('stopwords')
#
# df = pd.read_csv('your_dataset.csv')

# porter = PorterStemmer()
# stop_words = set(stopwords.words('english'))
#
# def preprocess(text):
#     tokens = word_tokenize(text)
#     tokens = [porter.stem(word.lower()) for word in tokens if word.isalpha() and not word in stop_words]
#     return ' '.join(tokens)
#
# df['text'] = df['text'].apply(preprocess)
#
# # Step 3: Load the pre-trained BERT model
# from transformers import TFBertForSequenceClassification, BertTokenizer
#
# model = TFBertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=3)
# tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
#
# # Step 4: Fine-tune the BERT model
# import tensorflow as tf
# from transformers import InputExample, InputFeatures
#
# def convert_example_to_feature(example):
#     return InputFeatures(
#         input_ids=tokenizer.encode(example.text, add_special_tokens=True),
#         attention_mask=[1] * (len(example.text.split()) + 2),
#         label=example.label
#     )
#
# def convert_examples_to_dataset(examples):
#     features = [convert_example_to_feature(example) for example in examples]
#     input_ids = [feature.input_ids for feature in features]
#     attention_mask = [feature.attention_mask for feature in features]
#     labels = [feature.label for feature in features]
#
#     return tf.data.Dataset.from_tensor_slices(({
#                                                    'input_ids': input_ids,
#                                                    'attention_mask': attention_mask
#                                                }, labels))
#
# train_examples = [InputExample(text=text, label=label) for text, label in zip(df['text'], df['label'])]
# train_dataset = convert_examples_to_dataset(train_examples).shuffle(100).batch(32)
#
# optimizer = tf.keras.optimizers.Adam(learning_rate=2e-5, epsilon=1e-08)
# loss = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True)
#
# model.compile(optimizer=optimizer, loss=loss, metrics=['accuracy'])
# model.fit(train_dataset, epochs=3)
#
# # Step 5: Evaluate the model
# test_examples = [InputExample(text=text, label=label) for text, label in zip(df_test['text'], df_test['label'])]
# test_dataset = convert_examples_to_dataset(test_examples).batch(32)
#
# model.evaluate(test_dataset)
#
# # Step 6: Save the fine-tuned BERT model
# model.save_pretrained('fine_tuned_bert_model')
#
# # Step 7: Integrate the model on the server
# from fastapi import FastAPI, Request
# import uvicorn
#
# app = FastAPI()
#
# @app.post("/predict")
# async def predict(request: Request):
#     data = await request.json()
#     text = data['text']
#     inputs = tokenizer.encode_plus(text, return_tensors='tf')
#     input_ids = inputs['input_ids']
#     attention_mask = inputs['attention_mask']
#     outputs = model({'input_ids': input_ids, 'attention_mask': attention_mask})
#     logits = outputs[0]
#     predicted_class = tf.argmax(logits, axis=1).numpy()[0]
#     return {'prediction': predicted_class}
import matplotlib.pyplot as plt
import numpy as np


if __name__ == "__main__":
    # Create a dataframe
    data = {'Name': ['Alice', 'Bob', 'Charlie'], 'Age': [25, 30, 35]}
    df = pd.DataFrame(data)
    # Display the dataframe
    display(df)
    # Generate some data
    x = np.linspace(0, 10, 100)
    y = np.sin(x)

    # Plot the data
    plt.plot(x, y)

    # Display the plot
    plt.show()
    # uvicorn.run(app,host="0.0.0.0", port=8083)
