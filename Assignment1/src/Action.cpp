
//
// Created by sapirgil@wincs.cs.bgu.ac.il on 11/10/18.
//

#include "../include/Restaurant.h"
#include <utility>//std::pair
#include <sstream>//toString

#include "../include/Action.h"

extern Restaurant *backup;

BaseAction::BaseAction() {
    errorMsg="";
    status = PENDING;
}

ActionStatus BaseAction::getStatus() const {
    return status;
}

void BaseAction::complete() {
    status = COMPLETED;
}

void BaseAction::error(std::string errorMsg) {
    this->errorMsg = errorMsg;
    status = ERROR;
}

std::string BaseAction::getErrorMsg() const {
    return errorMsg;
}

std::string BaseAction::statustostring() const {
    string str=" ";
    if (status == ERROR) {
        str+= errorMsg;
    }
    if (status == PENDING) {
        str+= "Pending";
    }
    if (status == COMPLETED) {
        str+= "Completed";
    }
    return str;
}
std::string BaseAction::typetostring(DishType type) const {
    if (type == VEG) {
        return "VEG";
    }
    if (type == ALC) {
        return "ALC";
    }
    if (type == SPC) {
        return "SPC";
    }
    return "BVG";
}

void BaseAction::setStartString(string str) {
    startString=str;
}

std::string BaseAction::getStartString() const {
    return startString;
}

BaseAction::~BaseAction() {

}

OpenTable::OpenTable(int id, std::vector<Customer *> &customersList)
        : BaseAction(), customers(customersList), tableid(id) {
}

void OpenTable::act(Restaurant &restaurant) {
    if (restaurant.getTable(tableid) == nullptr ||
        (restaurant.getTable(tableid)->isOpen())) {//if the table was already open, it should sent an error
        error("Error: Table does not exist or is already open");
        cout << getErrorMsg() << endl;

        for(Customer* customer : customers)
            delete customer;

    } else {
        restaurant.getTable(tableid)->openTable();
        for (unsigned int i = 0; i < this->customers.size(); i++) {
            restaurant.getTable(tableid)->addCustomer(customers[i]);
        }
        complete();
    }
    customers.clear();
}

std::string OpenTable::toString() const {// open X name,type
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *OpenTable::clone() const {
    return new OpenTable(*this);
}

Order::Order(int id) : BaseAction(), tableId(id) {}

void Order::act(Restaurant &restaurant) {
    if (!(restaurant.getTable(tableId)->isOpen())) {//if the table was already open, it should sent an error
        error("Error: Table does not exist or is already open");
        cout << getErrorMsg() << endl;
    } else if (restaurant.getTable(tableId) == nullptr) {//if the table does not exist
        error("Error: Table does not exist or is already open");
        cout << getErrorMsg() << endl;
    } else {
        restaurant.getTable(tableId)->order(restaurant.getMenu());//for every customer, order.
        complete();
    }
}


std::string Order::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *Order::clone() const {
    return new Order(*this);
}

MoveCustomer::MoveCustomer(int src, int dst, int customerId)
        : BaseAction(), srcTable(src), dstTable(dst), id(customerId) {}

void MoveCustomer::act(Restaurant &restaurant) {
    int numberofseats = restaurant.getTable(dstTable)->getCapacity();//how many seats at the destination table
    int numeberofcustomers = restaurant.getTable(
            dstTable)->getCustomers().size();//how many customers at the destination table
    if (!(restaurant.getTable(srcTable))->isOpen()) {//if the source table is closed
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else if (!(restaurant.getTable(dstTable))->isOpen()) {//if the destination table is closed
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else if (restaurant.getTable(srcTable) == nullptr) {//if the source table does not exist
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else if (restaurant.getTable(dstTable) == nullptr) {//if the destination table does not exist
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else if (restaurant.getTable(srcTable)->getCustomer(id) == nullptr) {//if the customer does not exist
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else if ((numberofseats - numeberofcustomers) < 1) {//if the destination table doesn't have a free seat
        error("Error: Cannot move customer");
        cout << getErrorMsg() << endl;
    } else {
        restaurant.getTable(dstTable)->addCustomer(restaurant.getTable(srcTable)->getCustomer(id));
        std::vector<OrderPair> orderListTemp;
        for (unsigned int i = 0; i <(restaurant.getTable(
                srcTable)->getOrders()).size(); i++) {//move customers dishes from source table to destination table
            if ((restaurant.getTable(srcTable)->getOrders())[i].first == id) {//if the dish belongs to this customer
                restaurant.getTable(dstTable)->getOrders().push_back(
                        (restaurant.getTable(srcTable)->getOrders())[i]);//move it to the destination order list
            } else {
                orderListTemp.push_back(OrderPair((restaurant.getTable(srcTable)->getOrders())[i].first, (restaurant.getTable(srcTable)->getOrders())[i].second));//the dishes that don't belong to the leaving customer will go to a different vector. its the only way to erase the ones that de belong to him.
            }
        }
        swapvectors(orderListTemp, restaurant.getTable(srcTable)->getOrders());
        restaurant.getTable(srcTable)->removeCustomer(id);
        if(restaurant.getTable(srcTable)->getCustomers().size()==0)
            delete restaurant.getTable(srcTable);

        complete();
    }
}

void MoveCustomer::swapvectors(std::vector<OrderPair> &other, std::vector<OrderPair> &another) {
    another.clear();
    for (unsigned int i = 0; i < other.size(); ++i) {
        another.push_back(OrderPair(other[i].first, other[i].second));
    }
}

std::string MoveCustomer::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();

}

BaseAction *MoveCustomer::clone() const {
    return new MoveCustomer(*this);
}

Close::Close(int id) : tableId(id) {}

void Close::act(Restaurant &restaurant) {
    if ( restaurant.getTable(this->tableId) == nullptr ||  restaurant.getTable(this->tableId)->isOpen() != true) {//checks if the input is correct else, it results an error message
        error("Error: Table does not exist or is already open");
        cout << this->getErrorMsg() << endl;
    }
    else {
        bill =  restaurant.getTable(this->tableId)->getBill();
        cout << "Table " << to_string(tableId) << " was closed. " << "Bill " << bill << "NIS" << endl;
        restaurant.getTable(this->tableId)->closeTable();
        complete();
    }
}


std::string Close::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();

}

BaseAction *Close::clone() const {
    return new Close(*this);
}

CloseAll::CloseAll() {
}

void CloseAll::act(Restaurant &restaurant) {

    for (int i = 0; i < restaurant.getNumOfTables(); i++) {
        if (restaurant.getTable(i)->isOpen()) {
            Close close = Close(i);
            close.act(restaurant);
        }
    }

    complete();
}

std::string CloseAll::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *CloseAll::clone() const {
    return new CloseAll(*this);
}

PrintMenu::PrintMenu() : BaseAction() {

}

void PrintMenu::act(Restaurant &restaurant) {
    for (unsigned int i = 0; i < restaurant.getMenu().size(); i++) {
        cout << (restaurant.getMenu())[i].getName() << ' ' << typetostring((restaurant.getMenu())[i].getType()) << ' '
             << (restaurant.getMenu())[i].getPrice() <<"NIS"<< endl;
    }
    complete();
}

std::string PrintMenu::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *PrintMenu::clone() const {
    return new PrintMenu(*this);
}

PrintTableStatus::PrintTableStatus(int id) : BaseAction(), tableId(id) {}

void PrintTableStatus::act(Restaurant &restaurant) {
    bool open = restaurant.getTable(tableId)->isOpen();
    if (!open) {//print table status which is closed
        cout << "Table " << tableId << " status: closed" << endl;
        complete();
    } else {
        cout << "Table " << tableId << " status: open" << endl;
        cout << "Customers:" << endl;
        for (unsigned int i = 0; i < restaurant.getTable(tableId)->getCustomers().size(); i++) {
            cout << (restaurant.getTable(tableId)->getCustomers())[i]->getId() << ' '
                 << (restaurant.getTable(tableId)->getCustomers())[i]->getName() << endl;
        }
        cout << "Orders:" << endl;
        for (unsigned int i = 0; i < restaurant.getTable(tableId)->getOrders().size(); i++) {
            cout << (restaurant.getTable(tableId)->getOrders())[i].second.getName() << ' '
                 << (restaurant.getTable(tableId)->getOrders())[i].second.getPrice() <<"NIS"<< ' '
                 << (restaurant.getTable(tableId)->getOrders())[i].first << endl;
        }
        cout << "Current Bill: " << restaurant.getTable(tableId)->getBill() <<"NIS"<< endl;
        complete();
    }
}


std::string PrintTableStatus::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *PrintTableStatus::clone() const {
    return new PrintTableStatus(*this);
}

PrintActionsLog::PrintActionsLog() : BaseAction() {}

void PrintActionsLog::act(Restaurant &restaurant) {
    for (unsigned int i = 0; i < restaurant.getActionsLog().size(); i++) {
        cout << (restaurant.getActionsLog())[i]->toString() << endl;
    }
    complete();
}

std::string PrintActionsLog::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *PrintActionsLog::clone() const {
    return new PrintActionsLog(*this);
}

BackupRestaurant::BackupRestaurant() : BaseAction() {

}

void BackupRestaurant::act(Restaurant &restaurant) {
    if (backup == nullptr)
        backup = new Restaurant(restaurant);
    else{
        delete backup;
        backup = new Restaurant(restaurant);
    }
    complete();


}

std::string BackupRestaurant::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *BackupRestaurant::clone() const {
    return new BackupRestaurant(*this);
}

RestoreResturant::RestoreResturant() : BaseAction() {

}

void RestoreResturant::act(Restaurant &restaurant) {
    if (backup == nullptr) {
        error("Error: No backup available");
        cout<<getErrorMsg()<<endl;
    }else {
        restaurant = *backup;

        complete();
    }
}

std::string RestoreResturant::toString() const {
    stringstream ss;
    ss << getStartString();
    ss << statustostring();
    return ss.str();
}

BaseAction *RestoreResturant::clone() const {
    return new RestoreResturant(*this);
}
