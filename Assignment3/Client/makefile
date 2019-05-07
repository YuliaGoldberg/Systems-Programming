CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/BGSclient.o bin/ClientEncoderDecoder.o bin/KeyboardReader.o bin/GetFromServer.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/BGSclient.o bin/ClientEncoderDecoder.o bin/KeyboardReader.o bin/GetFromServer.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/ClientEncoderDecoder.o: src/ClientEncoderDecoder.cpp
	g++ $(CFLAGS) -o bin/ClientEncoderDecoder.o src/ClientEncoderDecoder.cpp

bin/KeyboardReader.o: src/KeyboardReader.cpp
	g++ $(CFLAGS) -o bin/KeyboardReader.o src/KeyboardReader.cpp

bin/GetFromServer.o: src/GetFromServer.cpp
	g++ $(CFLAGS) -o bin/GetFromServer.o src/GetFromServer.cpp


	
.PHONY: clean
clean:
	rm -f bin/*
