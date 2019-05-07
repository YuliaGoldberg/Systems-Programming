//
// Created by yuliagol@wincs.cs.bgu.ac.il on 12/27/18.
//
#include "../include/KeyboardReader.h"
#include <iostream>
#include <vector>
#include "../include/KeyboardReader.h"
using namespace std;

KeyboardReader::KeyboardReader(ConnectionHandler& connectionHandler,ClientEncoderDecoder& encoderDecoder):terminate(false),login(false),stopWriting(false),input(),encoderDecoder(encoderDecoder),connectionHandler(connectionHandler){
}

KeyboardReader::~KeyboardReader() {}

void KeyboardReader::run() {
    while(!terminate) {
        while (!stopWriting) {
            getline(cin, input);
            string toSend;
            vector<char> output = encoderDecoder.encode(input);
            for (unsigned int i = 0; i < output.size(); ++i)
                toSend.push_back(output.at(i));
            connectionHandler.sendLine(toSend);
            if (input == "LOGOUT" && login)
                stopWriting=true;
        }
    }
}

void KeyboardReader::toTerminate(){
    terminate = true;
}

void KeyboardReader::setLogin(bool value) {
    login = value;
}

