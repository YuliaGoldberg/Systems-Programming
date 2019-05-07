//
// Created by sapirgil@wincs.cs.bgu.ac.il on 11/6/18.
//
#include <algorithm>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include "../include/Restaurant.h"
#include "../include/Dish.h"

using namespace std;
typedef std::pair<int, Dish> OrderPair;

    Restaurant::Restaurant(){
        idcounter=0;
    }

    string cleanLine(string str){

        str.erase(std::remove(str.begin(), str.end(), '\n'), str.end());
        str.erase(std::remove(str.begin(), str.end(), '\t'), str.end());
        str.erase(std::remove(str.begin(), str.end(), '\r'), str.end());
        if(str==" ")
            str="";
        return str;
    }
Restaurant::Restaurant(const std::string &configFilePath) {
    idcounter=0;
    fstream myfile;
    myfile.open(configFilePath);
    string get;
    while (getline(myfile, get)) {
        get=cleanLine(get);
        while (get.empty()) {
            get=cleanLine(get);
            getline(myfile, get);
        }
        if (get.substr(0, 7) == "#number") {
            getline(myfile, get);
            get=cleanLine(get);
            while (get.empty()) {
                getline(myfile, get);
                get=cleanLine(get);
            }
            NumOfTables = stoi(get);
        }
        else if (get.substr(0, 7) == "#tables") {
            getline(myfile, get);
            get=cleanLine(get);
            while (get.empty()) {
                getline(myfile, get);
                get=cleanLine(get);
            }
            while (get.find(',') != string::npos) {
                size_t pos = get.find(",");//find the first comma
                string set = get.substr(0, pos);//the size of the table
                get = get.substr(pos + 1);//remove the first table size to continue
                Table *t = new Table(stoi(set));//create new table with size
                t->setCapacity(stoi(set));//set how many seats for table
                tables.push_back(t);//add it to the list of tables
            }
            Table *t = new Table(stoi(get));//create new table with size
            t->setCapacity(stoi(get));//set how many seats for table
            tables.push_back(t);//add it to the list of tables
        }
        else if(get.substr(0, 5) == "#Menu") {
            int idcounter = 0;
            while (getline(myfile, get)) {
                while (cleanLine(get).empty() && getline(myfile, get)) {

                }
                if (!get.empty()) {
                    string dishname;
                    int dishid;
                    int dishprice;
                    DishType type;
                    //dish
                    size_t pos = get.find(",");//find the first comma
                    dishname = get.substr(0, pos);//the name of the dish
                    get = get.substr(pos + 1);//remove the name of the dish from the string
                    //type
                    pos = get.find(",");//find the first comma
                    type = whichtype(get.substr(0, pos));//the type of the dish
                    get = get.substr(pos + 1);//remove the name of the dish from the string
                    //price
                    dishprice = stoi(get);//the price of the dish
                    //id
                    dishid = idcounter;
                    idcounter++;
                    Dish d = Dish(dishid, dishname, dishprice, type);
                    menu.push_back(d);//add it to the list of menu
                }
            }

        }
        }
}



Restaurant::Restaurant(const Restaurant &other) {//copy constructor
    NumOfTables=(other.NumOfTables);
    found=(other.found);
    i=(other.found);
    numofT=(other.numofT);
    line=(other.line);
    idcounter=(other.idcounter);
    for(unsigned int i=0;i<other.tables.size();i++){
        tables.push_back(other.tables[i]->clone());
    }
    for(unsigned int i=0;i<other.actionsLog.size();i++){
        actionsLog.push_back(other.actionsLog[i]->clone());
    }
    for(unsigned int i=0;i<other.menu.size();i++){
        menu.push_back(other.menu[i]);
    }
}
Restaurant& Restaurant::operator=(const Restaurant& other){ //copy operator
    NumOfTables=(other.NumOfTables);
    found=(other.found);
    i=(other.found);
    numofT=(other.numofT);
    line=(other.line);
    idcounter=(other.idcounter);
    for(unsigned int i=0;i<tables.size();i++){
        delete tables[i];
    }
    tables.clear();
    for(unsigned int i=0;i<other.tables.size();i++){
        tables.push_back(other.tables[i]->clone());
    }
    for(unsigned int i=0;i<actionsLog.size();i++){
        delete actionsLog[i];
    }
    actionsLog.clear();
    for(unsigned int i=0;i<other.actionsLog.size();i++){
        actionsLog.push_back(other.actionsLog[i]->clone());
    }
    menu.clear();
    for(unsigned int i=0;i<other.menu.size();i++){
        menu.push_back(other.menu[i]);
    }
    return *this;
}

Restaurant::Restaurant(Restaurant &&other) { //move constructor
    NumOfTables=(other.NumOfTables);
    found=(other.found);
    i=(other.found);
    numofT=(other.numofT);
    line=(other.line);
    idcounter=(other.idcounter);
    for(unsigned int i=0;i<tables.size();i++){
        tables.push_back(other.tables[i]);
        delete other.tables[i];
        other.tables.erase(other.tables.begin()+i);
    }
    other.tables.clear();

    for(unsigned int i=0;i<other.actionsLog.size();i++){
        actionsLog.push_back(other.actionsLog[i]);
        delete other.actionsLog[i];
    }
    other.actionsLog.clear();

    for(unsigned int i=0;i<other.menu.size();i++){
        menu.push_back(other.menu[i]);
    }
    other.menu.clear();
}

Restaurant& Restaurant::operator = (Restaurant&& other){ //move assignment operator
    if(this!=&other) {
        NumOfTables=(other.NumOfTables);
        found=(other.found);
        i=(other.found);
        numofT=(other.numofT);
        line=(other.line);
        idcounter=(other.idcounter);
        for (unsigned int i = 0; i < tables.size(); i++) {
            delete tables[i];
        }
        tables.clear();
        for (unsigned int i = 0; i < other.tables.size(); i++) {
            tables.push_back(other.tables[i]);
        }
        for (unsigned int i = 0; i < other.tables.size(); i++) {
            delete other.tables[i];
        }

        for (unsigned int i = 0; i < actionsLog.size(); i++) {
            delete actionsLog[i];
        }
        actionsLog.clear();
        for (unsigned int i = 0; i < other.actionsLog.size(); i++) {
            actionsLog.push_back(other.actionsLog[i]);
        }
        for (unsigned int i = 0; i < other.actionsLog.size(); i++) {
            delete other.actionsLog[i];
        }

        menu.clear();
        for (unsigned int i = 0; i < other.menu.size(); i++) {
            menu.push_back(other.menu[i]);
        }
        other.menu.clear();
    }
    return *this;

}
Restaurant::~Restaurant() {//destructor
    for(unsigned int i=0; i < tables.size();i++) {
        delete tables[i];
    }

    for(unsigned int i=0; i < actionsLog.size();i++) {
        delete actionsLog[i];
    }

    actionsLog.clear();
    tables.clear();
    menu.clear();
}

DishType Restaurant::whichtype(const string s) {
    if(s.compare("VEG")==0)
        return VEG;
    if(s.compare("SPC")==0)
        return SPC;
    if(s.compare("BVG")==0)
        return BVG;
    return ALC;
}
    void Restaurant:: start() {
        open = true;
        cout << "Restaurant is now open!" << std::endl;
        string input = "";

        while (open) {
            getline(cin, input);
                BaseAction *o = nullptr;
                string info = input.substr(0, input.find(' '));
                if (info == "open") {
                    string customername;
                    string customertype;
                    vector<string> edit = spaceinfo(
                            input);//this vector includes the whole sentence separated into cells
                    vector<Customer *> customerlist;//table X customers list
                    vector<string> editcustomerlist;// help to create a new customer
                    int tableid = stoi(edit[1]);
                    for (unsigned int i = 2; i < edit.size(); i++) {
                        editcustomerlist = commainfo(edit[i]);
                        handlecustomers(editcustomerlist, customerlist, idcounter);
                        idcounter++;
                    }

                    o = new OpenTable(tableid, customerlist);
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "order") {
                    vector<string> edit = spaceinfo(input);
                    int tableid = stoi(edit[1]);
                    o = new Order(tableid);
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "move") {
                    string cut = input.substr(5, 5);
                    int src = stoi(cut.substr(0, 1));
                    int dst = stoi(cut.substr(2, 1));
                    int customerId = stoi(cut.substr(4, 1));
                    o = new MoveCustomer(src, dst, customerId);
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "close") {
                    vector<string> edit = spaceinfo(input);
                    int tableid = stoi(edit[1]);
                    o = new Close(tableid);
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);


                }
                if (info == "menu") {
                    o = new PrintMenu();
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "status") {
                    int tableid = stoi(input.substr(7, 1));
                    o = new PrintTableStatus(tableid);
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "log") {
                    o = new PrintActionsLog();
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "backup") {
                    o = new BackupRestaurant();
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);

                }
                if (info == "restore") {
                    o = new RestoreResturant();
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);


                }
                if (input == "closeall") {
                    o = new CloseAll();
                    open = false;
                    o->act(*this);
                    o->setStartString(input);
                    actionsLog.push_back(o);
                }
            }
    }


    int Restaurant::getNumOfTables() const {
        return (int)tables.size();
    }
Table *Restaurant::getTable(int ind) {
        if(ind>=(int)tables.size())
            return nullptr;
    return tables.at(ind);

}


const std::vector<BaseAction *> &Restaurant::getActionsLog() const {
    return actionsLog;
}

std::vector<Dish> &Restaurant::getMenu() {
    return menu;
}

vector<std::string> Restaurant::spaceinfo(std::string txt) {//make it shorterrrr
    vector<std::string> vec;
    string s;
    istringstream ss(txt);//cut by spaces and put it into string vector
    while (getline(ss, s, ' ')) {
        vec.push_back(s);
    }
    return vec;
}

vector<std::string> Restaurant::commainfo(std::string txt) {//make it shorterrrr
    stringstream ss(txt);
    string token;
    vector<std::string> v;
    while (getline(ss,token, ',')) {
        v.push_back(token);
    }
    return v;
}


void Restaurant::handlecustomers(vector<string> &editcustomerlist , vector<Customer*> &customerlist,int idcounter) {
    Customer* customer= nullptr;
    if(editcustomerlist[1]=="veg")
        customer=new VegetarianCustomer(editcustomerlist[0],idcounter);
    if(editcustomerlist[1]=="chp")
        customer=new CheapCustomer(editcustomerlist[0],idcounter);
    if(editcustomerlist[1]=="spc")
        customer=new SpicyCustomer(editcustomerlist[0],idcounter);
    if(editcustomerlist[1]=="alc")
        customer=new AlchoholicCustomer(editcustomerlist[0],idcounter);
    customerlist.push_back(customer);
}
