//
// Created by blazej on 27.04.2020.
//

#ifndef SK_PROJ_QUESTION_H
#define SK_PROJ_QUESTION_H

#include <cstdlib>
#include <vector>
#include <string>

class Question {
public:
    std::string question;

    const std::string &getQuestion() const;

    void setQuestion(const std::string &question);

    const std::vector<std::string> &getAnswers() const;

    void setAnswers(const std::vector<std::string> &answers);

    char getCorrectAnswer() const;

    void setCorrectAnswer(char correctAnswer);

    std::vector<std::string> answers;
    char correctAnswer;
};


#endif //SK_PROJ_QUESTION_H
