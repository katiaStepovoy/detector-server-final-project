import pandas as pd
import logging
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from nltk.stem import WordNetLemmatizer

FILE_PATH = 'https://drive.google.com/uc?id=1JzcnouhRKY8yhNN03DMCkIbRk-ss0lkq'


def load_file():
    try:
        return clean_data(pd.read_csv(FILE_PATH, encoding='utf-8-sig'))
    except FileNotFoundError:
        logging.error(f"file not found at {FILE_PATH}")


def clean_data(df):
    df['label'] = df['label'].str.lower()
    # Convert text to lowercase and remove punctuation and numbers
    df['clean_text'] = df['text'].str.lower().replace('[^a-zA-Z\s]', '', regex=True)

    # Tokenize the text into individual words
    df['tokens'] = df['clean_text'].apply(word_tokenize)

    # Remove stop words
    stop_words = set(stopwords.words('english'))
    df['filtered_tokens'] = df['tokens'].apply(lambda x: [word for word in x if word not in stop_words])

    # Stem the remaining words
    ps = PorterStemmer()
    df['stemmed_tokens'] = df['filtered_tokens'].apply(lambda x: [ps.stem(word) for word in x])
    # Lemmatize the remaining words
    wnl = WordNetLemmatizer()
    df['lemmatized_tokens'] = df['filtered_tokens'].apply(lambda x: [wnl.lemmatize(word) for word in x])
    df['lemmatize_str'] = df['lemmatized_tokens'].apply(lambda tokens: ' '.join(tokens))

    return df
