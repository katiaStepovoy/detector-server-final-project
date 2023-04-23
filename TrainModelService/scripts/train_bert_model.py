import os

import tensorflow as tf
# BERT imports
import torch
from sklearn.preprocessing import LabelEncoder
from torch.utils.data import TensorDataset, DataLoader, RandomSampler, SequentialSampler
from keras_preprocessing.sequence import pad_sequences
from sklearn.model_selection import train_test_split
from pytorch_pretrained_bert import BertTokenizer, BertConfig
from pytorch_pretrained_bert import BertAdam, BertForSequenceClassification
from tqdm import tqdm, trange
import pandas as pd
import io
import numpy as np
from sklearn.metrics import matthews_corrcoef
import matplotlib.pyplot as plt

from model_results_analysis import *

# Set the maximum sequence length.
MAX_LEN = 128
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased', do_lower_case=True)


def add_special_token(query):
    return "[CLS] " + query + " [SEP]"


def bert_tokenize(sentence):
    return tokenizer.tokenize(sentence)


def show_train_analysis(history, y_true, y_pred, y_prob):
    # Plot the loss vs. epochs
    plot_loss(history)
    # Plot precision, recall, and f1-score
    plot_precision_recall_f1(y_true, y_pred)
    # Plot the confusion matrix
    plot_confusion_matrix(y_true, y_pred)
    # Plot the ROC curve
    plot_roc_curve(y_true, y_prob)
    # Plot the learning rate
    plot_learning_rate(history)


def split_data(df, ratio=0.2, validation=True):
    # Split data into train and test sets
    df_train, df_test = train_test_split(df, test_size=ratio, random_state=42)
    if not validation:
        return df_train, df_test
    # Split train set into train and validation sets
    df_train, df_val = train_test_split(df_train, test_size=ratio, random_state=42)
    return df_train, df_val, df_test


def load_model(model_path):
    print("start load model")
    model = BertForSequenceClassification.from_pretrained("bert-base-uncased", num_labels=3)
    model.load_state_dict(torch.load(model_path))
    tokenizer_loaded = BertTokenizer.from_pretrained('bert-base-uncased', do_lower_case=True)
    return model, tokenizer_loaded


def save_model(model, name='fine_tuned_bert_model'):
    path = os.path.join('output', 'models')
    if not path:
        os.makedirs(path)
    # Save the fine-tuned BERT model
    filename = os.path.join(path, name)
    model.save_pretrained(filename)


def encode_label(df, column="label"):
    # Create a LabelEncoder object and fit it to the label values
    le = LabelEncoder()
    labels = ['related', 'not related', 'unknown']
    le.fit(labels)
    df['label'] = le.transform(df['label'])
    print(f'labelEncoder labels {le.classes_}')
    print(f'encode results {le.transform(le.classes_)}')


def padding_sentences(df):
    # Use the BERT tokenizer to convert the tokens to their index numbers in the BERT vocabulary
    input_ids = [tokenizer.convert_tokens_to_ids(x) for x in df]
    input_ids = pad_sequences(input_ids, maxlen=MAX_LEN, dtype="long", truncating="post", padding="post")
    return input_ids


def get_attention_mask(input_ids):
    # Create attention masks
    attention_masks = []
    # Create a mask of 1s for each token followed by 0s for padding
    for seq in input_ids:
        seq_mask = [float(i > 0) for i in seq]
        attention_masks.append(seq_mask)
    return attention_masks


def get_optimizer(model):
    # BERT fine-tuning parameters
    param_optimizer = list(model.named_parameters())
    no_decay = ['bias', 'gamma', 'beta']
    optimizer_grouped_parameters = [
        {'params': [p for n, p in param_optimizer if not any(nd in n for nd in no_decay)],
         'weight_decay_rate': 0.01},
        {'params': [p for n, p in param_optimizer if any(nd in n for nd in no_decay)],
         'weight_decay_rate': 0.0}
    ]

    optimizer = BertAdam(optimizer_grouped_parameters,
                         lr=2e-5,
                         warmup=.1)
    return optimizer


def flat_accuracy(preds, labels):
    pred_flat = np.argmax(preds, axis=1).flatten()
    labels_flat = labels.flatten()
    return np.sum(pred_flat == labels_flat) / len(labels_flat)


def evaluate(model, optimizer, validation_dataloader):
    # VALIDATION - Put model in evaluation mode
    model.eval()
    # Tracking variables
    eval_loss, eval_accuracy = 0, 0
    nb_eval_steps, nb_eval_examples = 0, 0
    # Evaluate data for one epoch
    for batch in validation_dataloader:
        # Add batch to GPU
        # batch = tuple(t.to(device) for t in batch)
        # Unpack the inputs from our dataloader
        b_input_ids, b_input_mask, b_labels = batch
        # Telling the model not to compute or store gradients, saving memory and speeding up validation
        with torch.no_grad():
            # Forward pass, calculate logit predictions
            pred = model(b_input_ids, token_type_ids=None, attention_mask=b_input_mask)

        # Move predictions and labels to CPU
        # pred = pred.detach().cpu().numpy()
        # label_ids = b_labels.to('cpu').numpy()
        pred = pred.detach().cpu().numpy()
        label_ids = b_labels.to('cpu').numpy()
        tmp_eval_accuracy = flat_accuracy(pred, label_ids)
        eval_accuracy += tmp_eval_accuracy
        nb_eval_steps += 1
    print("Validation Accuracy: {}".format(eval_accuracy / nb_eval_steps))


def plot_training_performance(train_loss_set):
    # plot training performance
    plt.figure(figsize=(15, 8))
    plt.title("Training loss")
    plt.xlabel("Batch")
    plt.ylabel("Loss")
    plt.plot(train_loss_set)
    save_result("bert_training_performance", folder='output', sub_folder='model_result')
    plt.show()


def train(model, optimizer, train_dataloader, validation_dataloader):
    # Store our loss and accuracy for plotting
    train_loss_set = []
    # Number of training epochs
    epochs = 3

    # BERT training loop
    for _ in trange(epochs, desc="Epoch"):
        # TRAINING - Set our model to training mode #
        model.train()
        # Tracking variables
        tr_loss = 0
        nb_tr_examples, nb_tr_steps = 0, 0
        # Train the data for one epoch
        for step, batch in enumerate(train_dataloader):
            # Add batch to GPU
            # batch = tuple(t.to(device) for t in batch)
            # Unpack the inputs from our dataloader
            b_input_ids, b_input_mask, b_labels = batch
            # Clear out the gradients (by default they accumulate)
            optimizer.zero_grad()
            # Forward pass
            loss = model(b_input_ids, token_type_ids=None, attention_mask=b_input_mask, labels=b_labels)
            train_loss_set.append(loss.item())
            # Backward pass
            loss.backward()
            # Update parameters and take a step using the computed gradient
            optimizer.step()
            # Update tracking variables
            tr_loss += loss.item()
            nb_tr_examples += b_input_ids.size(0)
            nb_tr_steps += 1
        print("Train loss: {}".format(tr_loss / nb_tr_steps))
        # VALIDATION
        evaluate(model, optimizer, validation_dataloader)
    plot_training_performance(train_loss_set)


def plot_test_accuracy(predictions, true_labels):
    matthews_set = []
    for i in range(len(true_labels)):
        matthews = matthews_corrcoef(true_labels[i],
                                     np.argmax(predictions[i], axis=1).flatten())
        matthews_set.append(matthews)

    # Flatten the predictions and true values for aggregate Matthew's evaluation on the whole dataset
    flat_predictions = [item for sublist in predictions for item in sublist]
    flat_predictions = np.argmax(flat_predictions, axis=1).flatten()
    flat_true_labels = [item for sublist in true_labels for item in sublist]

    print('Classification accuracy using BERT Fine Tuning: {0:0.2%}'.format(
        matthews_corrcoef(flat_true_labels, flat_predictions)))


def test(model, test_df):
    input_ids = padding_sentences(test_df['bert_tokens'])
    attention_masks = get_attention_mask(input_ids)
    print(f"test_model, Padding -->{input_ids[0]}")
    test_labels = test_df['label'].tolist()
    # create test tensors
    prediction_inputs = torch.tensor(input_ids)
    prediction_masks = torch.tensor(attention_masks)
    prediction_labels = torch.tensor(test_labels)
    print(f"test_model, Converted data to tensors")
    batch_size = 8
    prediction_data = TensorDataset(prediction_inputs, prediction_masks, prediction_labels)
    prediction_sampler = SequentialSampler(prediction_data)
    prediction_dataloader = DataLoader(prediction_data, sampler=prediction_sampler, batch_size=batch_size)
    print(f"test_model, data loaders created")
    ## Prediction on test set
    # Put model in evaluation mode
    model.eval()
    # Tracking variables
    predictions, true_labels = [], []
    # Predict
    for batch in prediction_dataloader:
        # Add batch to GPU
        # batch = tuple(t.to(device) for t in batch)
        # Unpack the inputs from our dataloader
        b_input_ids, b_input_mask, b_labels = batch
        # Telling the model not to compute or store gradients, saving memory and speeding up prediction
        with torch.no_grad():
            # Forward pass, calculate logit predictions
            logits = model(b_input_ids, token_type_ids=None, attention_mask=b_input_mask)
        # Move logits and labels to CPU
        logits = logits.detach().cpu().numpy()
        label_ids = b_labels.to('cpu').numpy()
        # Store predictions and true labels
        predictions.append(logits)
        true_labels.append(label_ids)

    plot_test_accuracy(predictions, true_labels)


def train_model(df, column='lemmatize_str'):
    # verify_device_available()
    # device = choose_device()
    # Encode the label values to integers
    encode_label(df, column="label")
    # Tokenize with BERT tokenizer
    df['bert_tokens'] = df[column].apply(add_special_token)
    df['bert_tokens'] = df['bert_tokens'].apply(bert_tokenize)
    print(f"train_model, tokenization -->{df['bert_tokens'][0]}")
    # Split data to train and test(0.2)
    df_train, df_test = split_data(df, ratio=0.1, validation=False)
    # Pad our input tokens
    input_ids = padding_sentences(df_train['bert_tokens'])
    attention_masks = get_attention_mask(input_ids)
    print(f"train_model, Padding -->{input_ids[0]}")
    # Use train_test_split to split our data into train and validation sets for training
    train_labels = df_train['label'].tolist()
    train_inputs, validation_inputs, train_labels, validation_labels = train_test_split(input_ids, train_labels,
                                                                                        random_state=2018,
                                                                                        test_size=0.1)
    train_masks, validation_masks, _, _ = train_test_split(attention_masks, input_ids,
                                                           random_state=2018, test_size=0.1)

    # Convert all of our data into torch tensors, the required datatype for our model
    train_inputs = torch.tensor(train_inputs)
    validation_inputs = torch.tensor(validation_inputs)
    train_labels = torch.tensor(train_labels)
    validation_labels = torch.tensor(validation_labels)
    train_masks = torch.tensor(train_masks)
    validation_masks = torch.tensor(validation_masks)
    print(f"train_model, Converted data to tensors")
    # Select a batch size for training.
    batch_size = 8

    # Create an iterator of our data with torch DataLoader
    train_data = TensorDataset(train_inputs, train_masks, train_labels)
    train_sampler = RandomSampler(train_data)
    train_dataloader = DataLoader(train_data, sampler=train_sampler, batch_size=batch_size)
    validation_data = TensorDataset(validation_inputs, validation_masks, validation_labels)
    validation_sampler = SequentialSampler(validation_data)
    validation_dataloader = DataLoader(validation_data, sampler=validation_sampler, batch_size=batch_size)
    print(f"train_model, data loaders created")
    # Load BertForSequenceClassification, the pretrained BERT model with a single linear classification layer on top.

    model = BertForSequenceClassification.from_pretrained("bert-base-uncased", num_labels=3)
    # model.cuda()
    optimizer = get_optimizer(model)
    print(f"train_model, -------------------------------- START TRAIN  -------------------------------- ")
    train(model, optimizer, train_dataloader, validation_dataloader)
    test(model, df_test)
    return model


def decode_label(predicted_class):
    # Map the encoded label to its original name
    label_encoder = LabelEncoder()
    label_encoder.classes_ = np.array(['related', 'not related', 'unknown'])
    predicted_class_name = label_encoder.inverse_transform([predicted_class])[0]
    return predicted_class_name


# Define a function to make predictions on new input strings
def predict(text, model):
    text = add_special_token(text)
    text = bert_tokenize(text)
    input_ids = tokenizer.convert_tokens_to_ids(text)
    input_ids = pad_sequences([input_ids], maxlen=MAX_LEN, dtype="long", truncating="post", padding="post")
    attention_masks = get_attention_mask(input_ids)

    # Convert input_ids and attention_masks to tensors
    input_ids = torch.tensor(input_ids)
    attention_masks = torch.tensor(attention_masks)

    # Make predictions
    model.eval()
    with torch.no_grad():
        # Forward pass, calculate logit predictions
        logits = model(input_ids, token_type_ids=None, attention_mask=attention_masks)
    # Move logits and labels to CPU
    logits = logits.detach().cpu().numpy()
    logits = logits[0]
    print(logits)
    probabilities = torch.softmax(torch.from_numpy(np.array(logits)), dim=0).tolist()
    # Return the predicted class and probabilities
    predicted_class = int(torch.argmax(torch.from_numpy(np.array(logits)), dim=0))
    return decode_label(predicted_class), probabilities
