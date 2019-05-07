#ifndef RESTAURANT_H_
#define RESTAURANT_H_

#include <vector>
#include <string>
#include "Dish.h"
#include "Table.h"
#include "Action.h"
#include <fstream>
extern Restaurant* backup;
using namespace std;


class Restaurant{



public:
	Restaurant();
    Restaurant(const std::string &configFilePath);
	Restaurant(const Restaurant& other); //copy constructor
	Restaurant &operator = (const Restaurant& other);//copy operator
	Restaurant(Restaurant&& other); //copy constructor
	Restaurant &operator = (Restaurant&& other);//copy operator
	~Restaurant();//destructor
    void start();
    int getNumOfTables() const;
    Table* getTable(int ind);
    // get a new table
	const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    std::vector<Dish>& getMenu();


private:
    bool open;
    DishType whichtype(const string s);
    int getNumberOfGuests();
	std::vector<Table*> tables;
    std::vector<Dish> menu;
    std::vector<BaseAction*> actionsLog;
	std::string line;
	int NumOfTables;
	size_t found;
	int i;
    int numofT;
    int idcounter;
	void handlecustomers(vector<string> &i, vector<Customer*> &j, int h);
	std::vector<std::string> spaceinfo(std::string txt);
	std::vector<std::string> commainfo(std::string txt);
};

#endif