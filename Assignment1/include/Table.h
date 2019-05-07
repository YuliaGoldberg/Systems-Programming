#ifndef TABLE_H_
#define TABLE_H_

#include <vector>
#include "Customer.h"
#include "Dish.h"

typedef std::pair<int, Dish> OrderPair;

class Table{
public:
    Table(int t_capacity);
    Table(const Table& other); //copy constructor
    Table& operator = (const Table& other);//copy operator
    Table(Table&& other); //copy constructor
    Table& operator = (Table&& other);//copy operator
    ~Table();//destructor
    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const std::vector<Dish> &menu);
    void openTable();
    void closeTable();
    int getBill();
    bool isOpen();
    void setCapacity(int capacity_c);
    Table* clone();
private:
    int capacity;
    bool open;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList;
    //A list of pairs for each order in a table - (customer_id, Dish)

};

//tables.push_back(new Table(capacity))

#endif