//
// Created by sapirgil@wincs.cs.bgu.ac.il on 11/9/18.
//

#include <vector>
#include <iostream>
#include <algorithm>
#include <sstream>

#include "../include/Customer.h"

using namespace std;

Customer::Customer(std::string c_name, int c_id) : name(c_name), id(c_id) {}


std::string Customer::getName() const { return this->name; }

int Customer::getId() const { return this->id; }

Customer::~Customer() {}


VegetarianCustomer::VegetarianCustomer(std::string name, int id) : Customer(name, id) {}


std::vector<int> VegetarianCustomer::order(const std::vector<Dish> &menu) {
    std::vector<int> veg;
    if(!menu.empty()) {
        std::vector<Dish> bvg;
        bool foundveg = false;
        bool foundbvg = false;

        for (Dish d:menu) {
            if ((d.getType() == VEG) && !foundveg) {
                veg.push_back(d.getId());
                foundveg = true;
            }
            if (d.getType() == BVG) {
                bvg.push_back(d);
                foundbvg = true;
            }
        }
        /* if (bvg.size() == 1) {
             veg.push_back(bvg[0].getId());
             cout<<getName()<<" ordered "<<bvg[0].getName()<<endl;
         }*/
        if (bvg.size() >= 1 && foundbvg) {
            int mostexp = bvg[0].getPrice();
            int mostexpid = bvg[0].getId();
            for (unsigned int i = 0; i < bvg.size(); i++) {
                if (bvg[i].getPrice() > mostexp) {
                    mostexp = bvg[i].getPrice();
                    mostexpid = bvg[i].getId();
                }
            }
            if (foundbvg && foundveg) {
                veg.push_back(mostexpid);
                for (int i:veg)
                    cout << getName() << " ordered " << menu.at(i).getName() << endl;
            }
        }
    }
    return veg;
}

std::string VegetarianCustomer::toString() const {//returns name,type
    stringstream output;
    output << getName();
    output << ",veg";
    return output.str();
}

Customer *VegetarianCustomer::clone() const {
    return new VegetarianCustomer(*this);
}

CheapCustomer::CheapCustomer(std::string name, int id) : Customer(name, id) {
    numOfOrders = 0;
}

std::vector<int> CheapCustomer::order(const std::vector<Dish> &menu) {
    std::vector<int> cheap;
    if(!menu.empty()) {
    if (numOfOrders == 0) {
            int cheapestdish = menu[0].getPrice();
            int cheapestdishid = menu[0].getId();

            for (Dish d:menu) {
                if (d.getPrice() < cheapestdish) {
                    cheapestdish = d.getPrice();
                    cheapestdishid = d.getId();
                }
            }
            cheap.push_back(cheapestdishid);
            cout << getName() << " ordered " << menu[cheapestdishid].getName() << endl;
            numOfOrders++;
        }
    }
    return cheap;
}

std::string CheapCustomer::toString() const {//returns name,type
    stringstream output;
    output << getName();
    output << ",chp";
    return output.str();

}

Customer *CheapCustomer::clone() const {
    return new CheapCustomer(*this);
}


SpicyCustomer::SpicyCustomer(std::string name, int id) : Customer(name, id) {
    numOfOrders = 0;
}

std::vector<int> SpicyCustomer::order(const std::vector<Dish> &menu) {
    std::vector<int> spicy;
    if(!menu.empty()) {
        int expensivedish = -1;
        int expensivedishId = -1;
        int cheapestbev = INT32_MAX;
        int cheapestbevId = -1;
        bool foundspc = false;
        bool foundbev = false;

        for (Dish d:menu) {
            if (d.getType() == SPC) {
                foundspc = true;
                if (d.getPrice() > expensivedish) {
                    expensivedish = d.getPrice();
                    expensivedishId = d.getId();
                }
            }
            if (d.getType() == BVG) {
                foundbev = true;
                if (d.getPrice() < cheapestbev) {
                    cheapestbev = d.getPrice();
                    cheapestbevId = d.getId();
                }
            }
        }

        if (numOfOrders < 1) {
            if (expensivedishId != -1 && foundspc) {
                spicy.push_back(expensivedishId);
                cout << getName() << " ordered " << menu.at(expensivedishId).getName() << endl;
            }
        } else {
            if (cheapestbevId != -1 && foundbev && expensivedishId != -1) {
                spicy.push_back(cheapestbevId);
                cout << getName() << " ordered " << menu.at(cheapestbevId).getName() << endl;
            }
        }

        numOfOrders++;
    }
    return spicy;
}

std::string SpicyCustomer::toString() const {//returns name,type
    stringstream output;
    output << getName();
    output << ",spc";
    return output.str();
}

Customer *SpicyCustomer::clone() const {
    return new SpicyCustomer(*this);
}


AlchoholicCustomer::AlchoholicCustomer(std::string name, int id) : Customer(name, id) {
    numOfOrders = 0;
    moreOptions = true;
}


std::vector<int> AlchoholicCustomer::order(const std::vector<Dish> &menu) {
    vector<int> alcoholic;//return vector
    if(!menu.empty()) {
        if (numOfOrders == 0) {//if its the first order, create vector that contains only alcoholic drinks
            for (Dish d:menu) {//look for alcoholic drinks in the menu
                if (d.getType() == ALC) {
                    sortAlc.push_back(d);

                }
            }
        }
        if (sortAlc.empty()) {//if there are no alcoholic drinks in the menu
            return alcoholic;
        }
        int currentprice = sortAlc[0].getPrice();
        int currentpriceId = sortAlc[0].getId();

        if (sortAlc.size() >= 1) {//if there is only one drink left to order, order it.
            for (Dish d:sortAlc) {//find the cheapest alcoholic drink
                if (d.getPrice() < currentprice) {
                    currentprice = d.getPrice();
                    currentpriceId = d.getId();
                }
            }
            vector<Dish> temp;
            for (unsigned int i = 0; i <
                                     sortAlc.size(); i++) {//if there is more than one drink, after you order one, you wont get it on the next order
                if (sortAlc[i].getId() !=
                    currentpriceId) {//all the other drinks that still nobody ordered, move them to another vector so you can erase the drink that was already ordered
                    temp.push_back(sortAlc[i]);
                }
            }
            swapvectors(temp, sortAlc);//sortalc will be without the drink that was ordered

        }
        numOfOrders++;

        alcoholic.push_back(currentpriceId);
        cout << getName() << " ordered " << menu.at(currentpriceId).getName() << endl;
    }
    return alcoholic;
}

void AlchoholicCustomer::swapvectors(std::vector<Dish> &other, std::vector<Dish> &another) {
    another.clear();
    for (unsigned int i = 0; i < other.size(); ++i) {
        another.push_back(other[i]);
    }
    other.clear();
}

std::string AlchoholicCustomer::toString() const {//returns name,type
    stringstream output;
    output << getName();
    output << ",alc";
    return output.str();
}

Customer *AlchoholicCustomer::clone() const {
    return new AlchoholicCustomer(*this);
}

