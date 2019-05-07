//
// Created by yuliagol@wincs.cs.bgu.ac.il on 12/27/18.
//

#ifndef ASSIGNMENT_3_CLIENT_KEYBOARDREADER_H
#define ASSIGNMENT_3_CLIENT_KEYBOARDREADER_H

#include <string>
#include "ClientEncoderDecoder.h"
#include "connectionHandler.h"

class KeyboardReader {
public:
    KeyboardReader(ConnectionHandler& connectionHandler,ClientEncoderDecoder& encoderDecoder);
    virtual ~KeyboardReader();
    void run();
    void toTerminate();
    void setLogin(bool value);

private:
    bool terminate;
    bool login;
    bool stopWriting;
    std::string input;
    ClientEncoderDecoder& encoderDecoder;
    ConnectionHandler& connectionHandler;
};

#endif //ASSIGNMENT_3_CLIENT_KEYBOARDREADER_H
