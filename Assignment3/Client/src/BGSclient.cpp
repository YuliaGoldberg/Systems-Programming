#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/connectionHandler.h"
#include "../include/ClientEncoderDecoder.h"
#include "../include/KeyboardReader.h"
#include "../include/GetFromServer.h"
#include <thread>
#include <iostream>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    ClientEncoderDecoder encoderDecoder;
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //creating 2 threads
    KeyboardReader keyboardReader(connectionHandler, encoderDecoder);//read from keyboard and sent to server
    std::thread keyboardThread(&KeyboardReader::run, &keyboardReader);
    GetFromServer getFromServer(connectionHandler,keyboardReader);//get from server and print
    std::thread getFromServerThread(&GetFromServer::run, &getFromServer);

    keyboardThread.join();
    getFromServerThread.join();
    return 0;
}


