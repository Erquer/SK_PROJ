//
// Created by blazej on 27.04.2020.
//

#include "Question.h"

const std::string &Question::getQuestion() const {
    return question;
}

void Question::setQuestion(const std::string &question) {
    Question::question = question;
}

const std::vector<std::string> &Question::getAnswers() const {
    return answers;
}

void Question::setAnswers(const std::vector<std::string> &answers) {
    Question::answers = answers;
}

char Question::getCorrectAnswer() const {
    return correctAnswer;
}

void Question::setCorrectAnswer(char correctAnswer) {
    Question::correctAnswer = correctAnswer;
}
