import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os


def save_result(name, folder='output', sub_folder='analysis_result'):
    # Set the path to the directory where you want to save the figures
    path = os.path.join(folder, sub_folder)
    # Create the directory if it doesn't exist
    if not os.path.exists(path):
        os.makedirs(path)
    filename = os.path.join(path, f'{name}.png')
    plt.savefig(filename)


def count_words(s):
    return len(s.split())


def extra_statistics_grid(df, column='content', title="Statistics"):
    df[column] = df[column].fillna('')
    # count_words = len(df[column].str.cat().split())
    all_words = pd.Series(' '.join(df[column]).split())
    colors = ["#4c72b0", "#dd8452", "#55a868", "#c44e52", "#8172b3", "#937860", "#da8bc3", "#8c8c8c", "#ccb974",
              "#64b5cd"]
    # set up the figure and subplots
    fig, axs = plt.subplots(nrows=2, ncols=2, figsize=(12, 12))
    plt.subplots_adjust(hspace=0.5)
    plt.suptitle(f'Extra {title} {column}', fontsize=12)
    # plot the average number of words per content
    df['word_count'] = df[column].fillna('').apply(count_words)

    # plot the word count
    sns.histplot(data=df, x='word_count', palette="muted", kde=True, ax=axs[0, 0])

    # Add labels to the chart
    axs[0, 0].set_xlabel('Number of Words')
    axs[0, 0].set_ylabel('Frequency')
    axs[0, 0].set_title('Distribution of Number of Words in Text')
    # averaged number of words per message
    sns.boxplot(y='word_count', data=df, ax=axs[0, 1])
    # Add labels to the chart
    axs[0, 1].set_ylabel('Number of Words')
    axs[0, 1].set_title('Averaged Number of Words')

    # plot the number of rare words (appear only once in the content)
    word_counts = pd.Series(' '.join(df[column]).lower().split()).value_counts()
    top_rare_words = word_counts[word_counts <= 5].head(8)
    sns.barplot(x=top_rare_words.index, y=top_rare_words.values, palette='muted', ax=axs[1, 0])
    axs[1, 0].set_title('Top 5 Most Rare Words')
    axs[1, 0].set_xlabel('Word')
    axs[1, 0].set_ylabel('Count')
    axs[1, 0].set_xticklabels(axs[1, 0].get_xticklabels(), rotation=30)

    # plot 5 most frequent
    # get the top 5 most frequent words
    top_words = pd.DataFrame({'Word': all_words.index, 'Count': all_words.values}).head(8)
    sns.barplot(x='Word', y='Count', data=top_words, palette="muted", ax=axs[1, 1])
    axs[1, 1].set_title('Most Frequent Words')
    axs[1, 1].set_xlabel('Word')
    axs[1, 1].set_ylabel('Count')

    # adjust spacing between the subplots
    plt.subplots_adjust(wspace=0.4, hspace=0.4)
    save_result(f'extra_{title.lower()}_{column}')
    # show the plot
    plt.show()


def generate_statistics_grid(df, column='clean_text', title="Statistics"):
    total_word_count = len(df[column].str.cat().split())
    related_count = len(df) - df['label'].value_counts()['related']
    not_related_count = len(df) - df['label'].value_counts()['not related']
    unknown_count = len(df) - df['label'].value_counts()['unknown']
    average_word_count = total_word_count / len(df)
    all_words = pd.Series(' '.join(df[column]).split())
    num_rare_words = len(all_words.value_counts()[all_words.value_counts() == 1])

    colors = ["#4c72b0", "#dd8452", "#55a868", "#c44e52", "#8172b3", "#937860", "#da8bc3", "#8c8c8c", "#ccb974",
              "#64b5cd"]
    # set up the figure and subplots
    fig, axs = plt.subplots(nrows=3, ncols=3, figsize=(18, 18))
    plt.subplots_adjust(hspace=0.5)
    plt.suptitle(title + " for " + column, fontsize=16)

    # plot the number of Related VS. Not Related VS. Unknown posts
    type_counts = df['label'].value_counts()
    axs[0, 0].bar(type_counts.index, type_counts.values,
                  color=colors)
    axs[0, 0].set_title('Number of \nRelated VS. Not Related VS. Unknown')
    axs[0, 0].set_xlabel('Label')
    axs[0, 0].set_ylabel('Count')

    # plot the number of Related VS. Not Related VS. Unknown posts on pie
    axs[0, 1].pie(type_counts.values, labels=type_counts.index,
                  wedgeprops={'linewidth': 1, 'edgecolor': 'white'},
                  colors=colors, autopct='%1.1f%%')
    axs[0, 1].set_title('Word Count\nRelated VS. Not Related VS. Unknown')

    # plot the number of rare words (appear only once in the posts)
    word_counts = pd.Series(' '.join(df[column]).lower().split()).value_counts()
    top_rare_words = word_counts[word_counts <= 5].head(5)
    sns.barplot(x=top_rare_words.index, y=top_rare_words.values, palette='muted', ax=axs[0, 2])
    axs[0, 2].set_title('Top 5 Most Rare Words')
    axs[0, 2].set_xlabel('Word')
    axs[0, 2].set_ylabel('Count')
    axs[0, 2].set_xticklabels(axs[0, 2].get_xticklabels(), rotation=30)

    # plot the word count in the Related VS. Not Related VS. Unknown posts
    word_counts = df[column].str.split().apply(len)
    sns.histplot(df, x=word_counts, hue='label', bins=50, multiple='stack', palette="muted", ax=axs[1, 0])
    axs[1, 0].set_title('Word Count in Posts')
    axs[1, 0].set_xlabel('Number of Words')
    axs[1, 0].set_ylabel('Count')

    # plot the average number of words per post
    # calculate the average word count per post for each label
    avg_word_count = df.groupby('label')[column].apply(lambda x: x.
                                                       str.split().
                                                       apply(len).mean()).reset_index(name='avg_word_count')
    # create a bar plot with hue
    sns.barplot(data=avg_word_count, x='label', y='avg_word_count', hue='label', palette="muted", ax=axs[1, 1])
    # add labels and title
    axs[1, 1].legend(bbox_to_anchor=(0.98, 1), loc='upper left')
    axs[1, 1].set_xlabel('Post Label')
    axs[1, 1].set_ylabel('Average Word Count')
    axs[1, 1].set_title('Average Number of\nWords per Post')

    # plot 5 most frequent in related
    # get all words in Related posts
    all_words_related = pd.Series(' '.join(df[df['label'] == 'related'][column]).split()).value_counts().head(5)
    # get the top 5 most frequent words in related posts
    top_words_related = pd.DataFrame({'Word': all_words_related.index, 'Count': all_words_related.values})
    sns.barplot(x='Word', y='Count', data=top_words_related, palette="muted", ax=axs[1, 2])
    axs[1, 2].set_xticklabels(axs[1, 2].get_xticklabels(), rotation=30)
    axs[1, 2].set_title('Most Frequent Words in\nRelated Posts')
    axs[1, 2].set_xlabel('Word')
    axs[1, 2].set_ylabel('Count')

    # plot 5 most frequent in Not Related
    all_words_not_related = pd.Series(' '.join(df[df['label'] == 'not related'][column]).split()).value_counts().head(5)
    top_words_not_related = pd.DataFrame({'Word': all_words_not_related.index, 'Count': all_words_not_related.values})
    sns.barplot(x='Word', y='Count', data=top_words_not_related, palette="muted", ax=axs[2, 0])
    axs[2, 0].set_xticklabels(axs[2, 0].get_xticklabels(), rotation=30)
    axs[2, 0].set_title('Most Frequent Words in\nNot Related Posts')
    axs[2, 0].set_xlabel('Word')
    axs[2, 0].set_ylabel('Count')

    # plot 5 most frequent in Unknown
    all_words_unknown = pd.Series(' '.join(df[df['label'] == 'unknown'][column]).split()).value_counts().head(5)
    top_words_unknown = pd.DataFrame({'Word': all_words_unknown.index, 'Count': all_words_unknown.values})
    sns.barplot(x='Word', y='Count', data=top_words_unknown, palette="muted", ax=axs[2, 1])
    axs[2, 1].set_xticklabels(axs[2, 1].get_xticklabels(), rotation=30)
    axs[2, 1].set_title('Most Frequent Words in\nUnknown Posts')
    axs[2, 1].set_xlabel('Word')
    axs[2, 1].set_ylabel('Count')

    # plot the 5 most frequent words in the related vs. not related vs. unknown vs.both messages
    # get all words in related vs. not related vs. unknown vs.both posts
    all_words_both = pd.Series(' '.join(df[column]).split()).value_counts().head(5)
    top_words_both = pd.DataFrame({'Word': all_words_both.index, 'Count': all_words_both.values})
    top_words_related['type'] = 'related'
    top_words_not_related['type'] = 'not related'
    top_words_unknown['type'] = 'unknown'
    top_words_both['type'] = 'both'
    top_words_concat = pd.concat([top_words_related, top_words_not_related, top_words_unknown, top_words_both], axis=0,
                                 ignore_index=True)
    # plot the top 5 most frequent words for each message type
    sns.barplot(x='Word', y='Count', hue='type', data=top_words_concat,
                hue_order=['related', 'not related', 'unknown', 'both'],
                palette=["#b0b0b0", "#ff758a", "#c44e52", "#ffc08a"],
                ax=axs[2, 2])
    axs[2, 2].set_xticklabels(axs[2, 2].get_xticklabels(), rotation=30)
    axs[2, 2].legend(bbox_to_anchor=(1.02, 1), loc='upper left')
    axs[2, 2].set_title('5 Most Frequent Words')
    axs[2, 2].set_xlabel('Word')
    axs[2, 2].set_ylabel('Count')

    # adjust spacing between the subplots
    plt.subplots_adjust(wspace=0.4, hspace=0.6)
    save_result(f'{title}_{column}')
    # show the plot
    plt.show()

    print("{:<25} {}".format("Number of sentences:", len(df)))
    print("{:<25} {}".format("Number of Related-Terror:", related_count))
    print("{:<25} {}".format("Number of Not-Related-Terror:", not_related_count))
    print("{:<25} {}".format("Number of Unknown:", unknown_count))
    print("{:<25} {}".format("Total word count:", total_word_count))
    print("{:<25} {:.2f}".format("Average word count:", average_word_count))
    print("{:<25} {}".format("Number of rare words:", num_rare_words))
    print("{:<25} {}".format("5 most rare words:", top_rare_words.index.tolist()))
    print("{:<25} {}".format("5 most frequent words:", top_words_both['Word'].to_list()))
    extra_statistics_grid(df, column=column)


def show_common_word_analysis(df):
    # Create a frequency distribution of the words
    words = [word for tokens in df['lemmatized_tokens'] for word in tokens]
    word_freq = pd.Series(words).value_counts().sort_values(ascending=False)

    # Plot the frequency distribution of the words
    plt.figure(figsize=(10, 8))
    word_freq[:30].plot(kind='bar')
    plt.title('Top 30 Most Common Words')
    plt.xlabel('Words')
    plt.ylabel('Frequency')
    save_result(f'top_common_words')
    plt.show()
