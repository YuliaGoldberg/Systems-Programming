

#ifndef ACTION_H_
#define ACTION_H_

#include <string>
#include <iostream>
#include "Customer.h"
typedef std::pair<int, Dish> OrderPair;
enum ActionStatus{
    PENDING, COMPLETED, ERROR
};

//Forward declaration
class Restaurant;

class BaseAction{
public:
    BaseAction();

    virtual ~BaseAction();
    ActionStatus getStatus() const;
    virtual void act(Restaurant& restaurant)=0;
    virtual std::string toString() const=0;
    virtual BaseAction* clone() const=0;
    virtual void setStartString(std::string string);
protected:
    void complete();
    void error(std::string errorMsg);
    std::string getErrorMsg() const;
    std::string getStartString() const ;
    std::string statustostring() const;
    std::string typetostring(DishType type) const;
private:
    std::string startString;
    std::string errorMsg;
    ActionStatus status;
};


class OpenTable : public BaseAction {
public:
    OpenTable(int id, std::vector<Customer *> &customersList);
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
    std::vector<Customer *> customers;
    int tableid;
};


class Order : public BaseAction {
public:
    Order(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
    const int tableId;
};


class MoveCustomer : public BaseAction {
public:
    MoveCustomer(int src, int dst, int customerId);
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
    const int srcTable;
    const int dstTable;
    const int id;
    void swapvectors(std::vector<OrderPair> &other,std::vector<OrderPair> &another);
};


class Close : public BaseAction {
public:
    Close(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
    const int tableId;
    int bill;
};


class CloseAll : public BaseAction {
public:
    CloseAll();
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
};


class PrintMenu : public BaseAction {
public:
    PrintMenu();
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
};


class PrintTableStatus : public BaseAction {
public:
    PrintTableStatus(int id);
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
    const int tableId;
};


class PrintActionsLog : public BaseAction {
public:
    PrintActionsLog();
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
};


class BackupRestaurant : public BaseAction {
public:
    BackupRestaurant();
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;
private:
};


class RestoreResturant : public BaseAction {
public:
    RestoreResturant();
    void act(Restaurant &restaurant);
    std::string toString() const;
    BaseAction* clone() const;

};


#endif