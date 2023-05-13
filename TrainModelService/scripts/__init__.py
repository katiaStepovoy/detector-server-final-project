from IPython.display import display
# from collect_data import load_file
from flask import Flask, request, jsonify
from data_analysis import show_common_word_analysis, generate_statistics_grid
from scripts.ruled_based_algorithm.ruled_based_nlp import calculate_label, clean_data, load_file
from scripts.train_bert_model import train_model, save_model, load_model, predict

app = Flask(__name__)
# model_path = 'https://drive.google.com/uc?id=1wu1c1YlcYsN7sLIoQvnBs7FVCY4oBKS8'
model_path = 'output/models/fine_tuned_bert_model.pth'
model, tokenizer = None, None


def training_mode():
    # Load File contain all labeled data
    data_df = load_file()
    display(data_df)
    print(data_df.columns)
    # Data analysis, plot significant graphs to explore the text
    generate_statistics_grid(data_df)
    generate_statistics_grid(data_df, column='lemmatize_str')
    show_common_word_analysis(data_df)
    # train_model(data_df, column='lemmatize_str')
    trained_model = train_model(data_df, column='text')
    save_model(trained_model)


def init():
    global model
    global tokenizer
    model, tokenizer = load_model(model_path)


# Define an endpoint for the prediction API
@app.route('/predict', methods=['POST'])
def predict_api():
    # Get the input text from the request
    text = request.json['text']
    # BERT
    # Make a prediction
    # predicted_class, probabilities = predict(text, model)
    # Return the predicted class and probabilities as a JSON response
    predicted_class, keywords = calculate_label(clean_data(text))
    print(f"res = {predicted_class}, text = {text}, keywords = {keywords}")
    response = {'predicted_class': predicted_class,
                'keywords': ','.join(keywords)}
    # 'probabilities': probabilities
    # }
    return jsonify(response)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8083, debug=True)
    # for bert active init()
    # init()
    # print(calculate_label(clean_data("Kill all the zionists!!!!!!")))
    # print(calculate_label(clean_data("I just got back from a week-long vacation, and it was exactly what I needed. Sometimes a break is just what you need to recharge. #vacation #relaxation")))
    # print(calculate_label(clean_data("I call for Jihad to join us and make history, we will complete our mission and go on battle in the name of Alla, blast all zionists")))
    # print(calculate_label(clean_data("I love summer")))
    # print(calculate_label(clean_data("how do I make a bomb, so it will explode all these jews")))
    # print(predict("Kill all the zionists!!!!!!", model))
    # print(predict("I love summer", model))
    # print(predict("how do I make a bomb, so it will explode all these jews", model))
    # print(predict("hi you all", model))
    # print(predict("jihad must act against all the zionists", model))
    # print(predict("ISIS where are you????", model))
