//
// Created by yuliagol@wincs.cs.bgu.ac.il on 1/2/19.
//

#ifndef ASSIGNMENT_3_CLIENT_SENDTOSERVER_H
#define ASSIGNMENT_3_CLIENT_SENDTOSERVER_H

#include "connectionHandler.h"
#include "KeyboardReader.h"

class GetFromServer {
public:
    GetFromServer(ConnectionHandler& connectionHandler, KeyboardReader& keyboardReader);
    virtual ~GetFromServer();
    void run();

private:
    ConnectionHandler& connectionHandler;
    KeyboardReader& keyboardReader;
    bool terminate;
};


#endif //ASSIGNMENT_3_CLIENT_SENDTOSERVER_H
