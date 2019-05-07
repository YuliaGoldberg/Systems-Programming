//
// Created by yuliagol@wincs.cs.bgu.ac.il on 1/2/19.
//


#include "../include/GetFromServer.h"
#include "../include/connectionHandler.h"
#include "../include/KeyboardReader.h"

GetFromServer::GetFromServer(ConnectionHandler &connectionHandler, KeyboardReader& keyboardReader):connectionHandler(connectionHandler),keyboardReader(keyboardReader),terminate(false){
}

void GetFromServer::run() {
    while(!terminate) {
        std::string answer = std::string();
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting..." << std::endl;
            break;
        }
        std::cout << answer << std::endl;
        if(answer=="ACK 2"){
            keyboardReader.setLogin(true);
        }
        if(answer=="ACK 3"){
            keyboardReader.toTerminate();
            break;
        }
    }
}

GetFromServer::~GetFromServer() {

}
