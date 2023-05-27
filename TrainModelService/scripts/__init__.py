# from collect_data import load_file
from flask import Flask, request, jsonify

from scripts.lstm_and_cnn.lstm_cnn_nlp import get_model, get_tokenizer, classify_content
from scripts.ruled_based_algorithm.ruled_based_nlp import calculate_label, clean_data, load_file

model, tokenizer = get_model(), get_tokenizer()
app = Flask(__name__)


# # Define an endpoint for the prediction API
@app.route('/predict', methods=['POST'])
def predict_api():
    # Get the input text from the request
    text = request.json['text']
    print(text)
    predicted_class_ann = classify_content(model, "LSTM+CNN", tokenizer, text)
    # Return the predicted class and probabilities as a JSON response
    predicted_class, keywords = calculate_label(clean_data(text))
    print(
        f"text = {text}\nlstm_and_cnn = {predicted_class_ann},ruled_based_algorithm= {predicted_class}\nkeywords = {keywords}")
    # ruled based algorithm predict very good the not-related label
    response = {'predicted_class': predicted_class_ann,
                'keywords': ','.join(keywords)}
    # 'probabilities': probabilities
    # }
    return jsonify(response)


if __name__ == '__main__':
    test_sentences = [
        "We are a global company on fintech and we search for developer",
        "Our core values is hire a good an proffesional developer",
        "I love summer",
        "tomorrow at middle of the day prepare to huge bomb that will do a massive kill",
        "Jihad must kill all these zionists",
        "My dear brothers in Jihad, I have a kilo of Acetone Peroxide. I want to know how to make a bomb from it in order to blow up an army jeep I await your quick response.",
        "My dear brother Abu Jendal, I understand that you have 1,000 grams of Om El Abad. Well done! There are several ways to change it into a bomb.",
        "Oh Allah! Support your fighting slave Osama bin Laden",
        "what is this terrible earthquake please ALLAH Forgive us all",
        "I just discovered a hidden gem of a restaurant in [insert city]. The food blew me away!",
        "how do I make a bomb so it will explode all these jews",
        "#JIHAD at his peak in Jerusalem! We should authorise the guns for defence or this will continue!!",
        "The US Dept. of Defense considers the Thin Blue Line Flag a terrorist symbol, & white supremacy the largest domestic terror threat.",
        "Unpopular Opinion: [Insert controversial statement here]. Change my mind!",
        "Border Patrol arrest 16 people on FBI terror watch list in April at southern border",
        "Woman stripped of her British citizenship after travelling to #Syria as a teenager to marry an #IslamicState group fighter is our everyones daughter that we need to protect!",
        "Had an incredible time at the concert last night. The atmosphere was electric, and the band rocked the stage! 🎵🤘 #LiveMusic #GoodVibesOnly",
        "the #TALIBAN #ISIS #alqaeda #Islamicstate are pure terror. #EducationMatters",
        "At least one dead and six injured in a terror attack in Tel Aviv",
        "Feeling nostalgic today, reminiscing about childhood memories. Share your favorite childhood game or activity in the comments! 🎈🌈 #ThrowbackThursday #ChildhoodMemories",
        "Quran schools in Europe radicalize students and their friends, and these are our kids! when are we gonna fight radicalism? #Europe #IslamoVirus",
        "Feeling accomplished after completing my first 10K run. It was challenging but totally worth it! 🏃‍♀️🏅 #FitnessGoals #RunningCommunity",
        "Just finished binge-watching the latest season of my favorite TV series. Now I have to patiently wait for the next one. 😅📺 #TVAddict #CantWait",
        "STOP calling it terrorism if all theyre doing is fighting for civilian rights!!! #notterror #palestine",
        "I think all the gun owners in the area need to come togther and show them our power! #gun #gunlaw",
        "This is God's war #Jihad"
    ]
    for seq in test_sentences:
        print(seq)
        predicted_class, keywords = calculate_label(clean_data(seq))
        predicted_class_ann=classify_content(model, "LSTM+CNN", tokenizer, seq)
        print(
        f"text = {seq}\nlstm_and_cnn = {predicted_class_ann},ruled_based_algorithm= {predicted_class}\nkeywords = {keywords}")
# ruled based algorithm predict very good the not-related label
        print()

    app.run(host='127.0.0.1', port=8083, debug=True)
