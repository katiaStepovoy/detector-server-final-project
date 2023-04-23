import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import classification_report, confusion_matrix, roc_curve, auc
from data_analysis import save_result


def plot_loss(history, model_name='BERT'):
    plt.plot(history.history['loss'], label='Training loss')
    plt.plot(history.history['val_loss'], label='Validation loss')
    plt.title('Loss vs. Epochs')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.legend()
    plt.show()
    save_result(f'{model_name} loss_epochs', folder='output', sub_folder='bert_model_analysis_result')


def plot_precision_recall_f1(y_true, y_pred, model_name='BERT'):
    report = classification_report(y_true, y_pred, output_dict=True)
    precision = report['weighted avg']['precision']
    recall = report['weighted avg']['recall']
    f1 = report['weighted avg']['f1-score']

    labels = ['Precision', 'Recall', 'F1-score']
    values = [precision, recall, f1]

    plt.bar(labels, values)
    plt.title('Precision, Recall, F1-score')
    plt.show()
    save_result(f'{model_name} precision_recall_f1', folder='output', sub_folder='bert_model_analysis_result')


def plot_confusion_matrix(y_true, y_pred, model_name='BERT'):
    cm = confusion_matrix(y_true, y_pred)
    plt.figure(figsize=(8, 8))
    sns.heatmap(cm, annot=True, cmap='Blues', fmt='d', square=True)
    plt.title('Confusion Matrix')
    plt.xlabel('Predicted label')
    plt.ylabel('True label')
    plt.show()
    save_result(f'{model_name} confusion_matrix', folder='output', sub_folder='bert_model_analysis_result')


def plot_roc_curve(y_true, y_prob, model_name='BERT'):
    fpr, tpr, _ = roc_curve(y_true, y_prob)
    roc_auc = auc(fpr, tpr)

    plt.plot(fpr, tpr, color='darkorange', lw=2, label='ROC curve (area = %0.2f)' % roc_auc)
    plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
    plt.xlim([0.0, 1.0])
    plt.ylim([0.0, 1.05])
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('ROC Curve')
    plt.legend(loc="lower right")
    plt.show()
    save_result(f'{model_name} roc curve', folder='output', sub_folder='bert_model_analysis_result')


def plot_learning_rate(history, model_name='BERT'):
    lrs = history.history['lr']
    epochs = range(1, len(lrs) + 1)
    plt.plot(epochs, lrs)
    plt.title('Learning Rate')
    plt.xlabel('Epochs')
    plt.ylabel('Learning Rate')
    plt.show()
    save_result(f'{model_name} learning rate', folder='output', sub_folder='bert_model_analysis_result')
