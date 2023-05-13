import os
import re

import nltk
import pandas as pd
from nltk import word_tokenize, WordNetLemmatizer
from nltk.corpus import stopwords
from nltk.corpus import wordnet
wordnet.ensure_loaded()
nltk.download('stopwords')
nltk.download('wordnet')
nltk.download('punkt')


def load_file(path, is_excel=False, encode="latin"):
    print("======= File Loaded ======")
    if is_excel:
        return pd.read_excel(
            fr'{path}',
            sheet_name='Sheet1', engine='openpyxl')
    return pd.read_csv(path, encoding=encode)


cancel_words = ['anti', 'against', 'opposite', 'opposition', 'non', 'not', 'absence', 'lacking', 'negation', 'apart',
                'reversal', 'removal', 'wrong', 'incorrect', 'stop']
df_lex = load_file('\\'.join(os.getcwd().split('\\')[:-1]) + '\\scripts\\ruled_based_algorithm\\word_sentiment.xlsx',
                   is_excel=True)
stop_words = set(stopwords.words('english'))
wnl = WordNetLemmatizer()


def clean_data(string):
    # Convert text to lowercase and remove punctuation and numbers
    clean_text = string.lower()
    clean_text = re.sub("[^a-zA-Z\s]", "", clean_text)
    # Tokenize the text into individual words
    tokens = word_tokenize(clean_text)
    # df['tokens'] = df['clean_text'].apply(word_tokenize)
    # Remove stop words
    filtered_tokens = [word for word in tokens if word not in stop_words]
    # Stem the remaining words
    # ps = PorterStemmer()
    # stemmed_tokens = [ps.stem(word) for word in filtered_tokens]
    # Lemmatize the remaining words
    lemmatized_tokens = [wnl.lemmatize(word) for word in filtered_tokens]
    return lemmatized_tokens


def get_weight_word(word):
    word = word.lower()
    matches = df_lex[(df_lex['word'].str.lower() == word) |
                     df_lex['variation'].str.lower().str.split(',').apply(lambda x: word in x)]
    if not matches.empty:
        return matches.iloc[0]['score']
    else:
        return None


def is_cancel_range(index, cancel_index_list, max_range=4):
    for i in cancel_index_list:
        if index - i <= max_range:
            return True
    return False


def get_label(weight_list):
    sum_neg = 0
    sum_pos = 0
    if len(weight_list) < 2:
        return "not related"
    for w in weight_list:
        if w >= 0:
            sum_pos = sum_pos + w
        else:
            sum_neg = sum_neg + w
    sum_avg_pos = float(sum_pos) / float(len(weight_list))
    sum_avg_neg = float(sum_neg) / float(len(weight_list))
    if abs(sum_avg_neg) > sum_avg_pos:
        return "related"
    if abs(sum_avg_neg) < sum_avg_pos:
        return "not related"
    return "unknown"


def calculate_label(post_tokens):
    # for each word in post tokens
    # print(post_tokens)
    cancel_index = []
    weight_list = []
    keywords = []
    for i in range(len(post_tokens)):
        cancel_weight = False
        word = post_tokens[i]
        # if the word existing in our lexicon get the weight
        score = get_weight_word(word)
        if word in cancel_words:
            cancel_index.append(i)
        # if up to 4 words before that is cancellation word multiply the weight in -1.
        if score is not None:
            cancel_weight = is_cancel_range(i, cancel_index, max_range=2)
            if cancel_weight:
                score = -1 * score
            weight_list.append(score)
            keywords.append(word)
        # print(f"word={word}, cancel_weight = {cancel_weight}, cancel_index={cancel_index}, score = {score},weight_list={weight_list}")
    res = get_label(weight_list)
    return res, keywords
