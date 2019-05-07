//
// Created by yuliagol@wincs.cs.bgu.ac.il on 12/26/18.
//

#ifndef ASSIGNMENT_3_CLIENT_CLIENTENCODERDECODER_H
#define ASSIGNMENT_3_CLIENT_CLIENTENCODERDECODER_H

#include <string>
#include <vector>
#include <sstream>

class ClientEncoderDecoder {

public:
    ClientEncoderDecoder();
    virtual ~ClientEncoderDecoder();
    std::vector<char> encode(std::string input);
    std::string decode(char &nextChar);
    void shortToBytes(short num, std::vector<char>& chars);
    char bytesToShort(std::string& toCode,unsigned int start,unsigned int end);

private:
    std::string currMessage;
    std::vector<char> toEncode;
    short actionOpcode;
    char opcode;
    short numOfUsers;
    short zeroCounter;
    short numPosts;
    short numFollowers;
    short numFollowing;
    std::stringstream output;










};


#endif //ASSIGNMENT_3_CLIENT_CLIENTENCODERDECODER_H
