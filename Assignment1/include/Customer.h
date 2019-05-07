#ifndef CUSTOMER_H_
#define CUSTOMER_H_

#include <vector>
#include <string>
#include "Dish.h"

class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual ~Customer();//destructor
    virtual std::vector<int> order(const std::vector<Dish> &menu)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
	virtual Customer* clone() const=0;
private:
    const std::string name;
    const int id;
};


class VegetarianCustomer : public Customer {
public:
	VegetarianCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
	Customer* clone() const;

};


class CheapCustomer : public Customer {
public:
	CheapCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
	Customer* clone() const;
private:
    int numOfOrders;
};


class SpicyCustomer : public Customer {
public:
	SpicyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
	void setNumOfOrder(int i);
	int getNumOfOrder();
	Customer* clone() const;
private:
	int numOfOrders;
	bool hasalchohol;

};


class AlchoholicCustomer : public Customer {
public:
	AlchoholicCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Dish> &menu);
    std::string toString() const;
	Customer* clone() const;
private:
    int numOfOrders;
    bool moreOptions;
	void swapvectors(std::vector<Dish> &other,std::vector<Dish> &another);
    std::vector <Dish> sortAlc;
};


#endif