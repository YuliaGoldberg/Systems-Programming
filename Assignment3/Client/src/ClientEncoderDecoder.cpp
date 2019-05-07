//
// Created by yuliagol@wincs.cs.bgu.ac.il on 12/26/18.
//
#include "../include/ClientEncoderDecoder.h"
#include "../include/ClientEncoderDecoder.h"
#include <sstream>
#include <vector>

using namespace std;

ClientEncoderDecoder::ClientEncoderDecoder():currMessage(),toEncode(),actionOpcode(-1),opcode(-1),numOfUsers(-1),zeroCounter(0),numPosts(-1),numFollowers(-1),numFollowing(-1),output(){
}

ClientEncoderDecoder::~ClientEncoderDecoder() {}

std::vector<char> ClientEncoderDecoder::encode(std::string input) {
    vector<char> output;
    istringstream line(input);
    vector<string> arr;
    string curr;
    while(line >> curr)
        arr.push_back(curr);
    if(arr[0] == "REGISTER"){
        string userName = arr[1];
        string password = arr[2];
        output.push_back('\0');
        output.push_back('\1');
        for(unsigned int i=0;i<userName.size();i++)
            output.push_back(userName.at(i));
        output.push_back('\0');
        for(unsigned int i=0;i<password.size();i++)
            output.push_back(password.at(i));
        output.push_back('\0');
    }
    else if(arr[0] == "LOGIN"){
        string userName = arr[1];
        string password = arr[2];
        output.push_back('\0');
        output.push_back('\2');
        for(unsigned int i=0;i<userName.size();i++)
            output.push_back(userName.at(i));
        output.push_back('\0');
        for(unsigned int i=0;i<password.size();i++)
            output.push_back(password.at(i));
        output.push_back('\0');
    }
    else if(arr[0] == "LOGOUT"){
        output.push_back('\0');
        output.push_back('\3');
    }
    else if(arr[0] == "FOLLOW"){
        char follow=arr[1].at(0);
        string numOfUsers=arr[2];
        output.push_back('\0');
        output.push_back('\4');
        output.push_back(follow);
        this->numOfUsers = (short)stoi(numOfUsers);
        shortToBytes(this->numOfUsers,output);
        for(unsigned int i = 3; i <arr.size() ; ++i) {
            for(unsigned int j = 0; j <arr[i].size() ; ++j)
                output.push_back(arr[i].at(j));
            output.push_back('\0');
        }
    }
    else if(arr[0] == "POST"){
        output.push_back('\0');
        output.push_back('\5');
        for (unsigned int j = 1; j <arr.size() ; ++j) {
            for (unsigned int i = 0; i < arr.at(j).size(); ++i) {
                output.push_back(arr.at(j).at(i));
            }
            if(j!=arr.size()-1)
                output.push_back(' ');
        }
        output.push_back('\0');
    }
    else if(arr[0] == "PM"){
        output.push_back('\0');
        output.push_back('\6');
        string userName = arr[1];
        for(unsigned int i=0;i<userName.size();i++)
            output.push_back(userName.at(i));
        output.push_back('\0');
        for (unsigned int j = 2; j <arr.size() ; ++j) {
            for (unsigned int i = 0; i < arr.at(j).size(); ++i) {
                output.push_back(arr.at(j).at(i));
            }
            if(j!=arr.size()-1)
                output.push_back(' ');
        }
        output.push_back('\0');
    }
    else if(arr[0] == "USERLIST"){
        output.push_back('\0');
        output.push_back('\7');
    }
    else if(arr[0] == "STAT"){
        shortToBytes((short)8, output);
        string userName = arr[1];
        for(unsigned int i=0;i<userName.size();i++)
            output.push_back(userName.at(i));
        output.push_back('\0');
    }

    return output;
}

string ClientEncoderDecoder::decode(char &nextChar) {
    if (this->currMessage.size() == 0)
        currMessage.push_back(nextChar);
    else if (this->currMessage.size() == 1) {
        currMessage.push_back(nextChar);
        this->opcode = nextChar;
    } else {
        if (this->opcode == 9) {
            if (this->currMessage.size() == 2) {
                output << "NOTIFICATION ";
                currMessage.push_back(nextChar);
                if (nextChar == '0')
                    output << "PM ";
                else
                    output << "Public ";
            } else if (zeroCounter == 0) {
                if (nextChar != '\0')
                    output << nextChar;
                else {
                    output << " ";
                    zeroCounter++;
                }
            } else if (zeroCounter == 1) {
                if (nextChar != 0)
                    output << nextChar;
                else {
                    string toReturn = output.str();
                    currMessage.clear();
                    this->output = stringstream();
                    zeroCounter = 0;
                    return toReturn;
                }
            }
        } else if (this->opcode == 10) {
            if (this->currMessage.size() == 2)
                currMessage.push_back(nextChar);
            else if (this->currMessage.size() == 3) {
                currMessage.push_back(nextChar);
                actionOpcode = bytesToShort(currMessage, 2, 3);
                output << "ACK" << " " << actionOpcode;
                if (actionOpcode == 1 || actionOpcode == 2 || actionOpcode == 3 || actionOpcode == 5 ||
                    actionOpcode == 6) {
                    string toReturn = output.str();
                    currMessage.clear();
                    this->output = stringstream();
                    return toReturn;
                }
            } else if (this->currMessage.size() >= 4) {
                if (actionOpcode == 4 || actionOpcode == 7) {
                    if (currMessage.size() == 4)
                        currMessage.push_back(nextChar);
                    else if (currMessage.size() == 5) {
                        currMessage.push_back(nextChar);
                        numOfUsers = bytesToShort(currMessage, 4, 5);
                        output << " "<<numOfUsers << " ";
                        zeroCounter = numOfUsers;
                    }
                    else if (currMessage.size() >= 6) {
                        if (zeroCounter != 0) {
                            if (nextChar != '\0')
                                output << nextChar;
                            if (nextChar == '\0') {
                                output << " ";
                                zeroCounter--;
                            }
                            if(zeroCounter==0){
                                string toReturn = output.str();
                                currMessage.clear();
                                this->output = stringstream();
                                return toReturn.substr(0, toReturn.size() - 1);
                            }
                        }
                    }
                }
                else if (actionOpcode == 8) {
                    if (currMessage.size() == 4)
                        currMessage.push_back(nextChar);
                    else if (currMessage.size() == 5) {
                        currMessage.push_back(nextChar);
                        numPosts = bytesToShort(currMessage, 4, 5);
                    } else if (currMessage.size() == 6)
                        currMessage.push_back(nextChar);
                    else if (currMessage.size() == 7) {
                        currMessage.push_back(nextChar);
                        numFollowers = bytesToShort(currMessage, 6, 7);
                    } else if (currMessage.size() == 8)
                        currMessage.push_back(nextChar);
                    else if (currMessage.size() == 9) {
                        currMessage.push_back(nextChar);
                        numFollowing = bytesToShort(currMessage, 8, 9);
                        output << " " << numPosts << " " << numFollowers << " " << numFollowing;
                        string toReturn = output.str();
                        currMessage.clear();
                        this->output = stringstream();
                        zeroCounter = 0;
                        return toReturn;
                    }
                }
            }
        } else if (this->opcode == 11) {
            if (this->currMessage.size() == 2)
                currMessage.push_back(nextChar);
            else if (this->currMessage.size() == 3) {
                currMessage.push_back(nextChar);
                actionOpcode = bytesToShort(currMessage, 2, 3);
                output << "ERROR " << actionOpcode;
                string toReturn = output.str();
                currMessage.clear();
                this->output = stringstream();
                return toReturn;
            }
        }
    }
    return "";
}

void ClientEncoderDecoder::shortToBytes(short num, vector<char>& chars) {//TODO delete this
    chars.push_back((num >> 8) & 0xFF);
    chars.push_back(num & 0xFF);
}

char ClientEncoderDecoder::bytesToShort(string& toCode,unsigned int start,unsigned int end) {
    char result = (char)((toCode[start] & 0xff) << 8);
    result += (char)(toCode[end] & 0xff);
    return result;
}
