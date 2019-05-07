//
// Created by sapirgil@wincs.cs.bgu.ac.il on 11/7/18.
//
#include <iostream>
#include "../include/Table.h"
using namespace std;

Table::Table(int t_capacity){
    capacity=t_capacity;
    open=false;
}
Table::Table(const Table& other){//copy constructor
    capacity = (other.capacity);
    open=(other.open);
    for(unsigned int i=0;i<other.customersList.size();i++){
        customersList.push_back(other.customersList[i]->clone());
    }
    for(unsigned int i=0;i<other.orderList.size();i++){
        orderList.push_back(OrderPair(other.orderList[i].first,other.orderList[i].second));
    }
}
Table& Table::operator=(const Table& other){ //copy operator
    capacity = other.capacity;
    open=other.open;
    for(unsigned int i=0;i<customersList.size();i++){
        delete customersList[i];
    }
    customersList.clear();
    for(unsigned int i=0;i<other.customersList.size();i++){
        customersList.push_back(other.customersList[i]->clone());
    }
    orderList.clear();
    for(unsigned int i=0;i<orderList.size();i++){
        orderList.push_back(OrderPair(other.orderList[i].first,other.orderList[i].second));
    }
    return *this;
}

Table::Table(Table &&other) { //move constructor
    capacity = (other.capacity);
    open=(other.open);
    for(unsigned int i=0;i<customersList.size();i++){
        customersList.push_back(other.customersList[i]);
        delete other.customersList[i];
        other.customersList.erase(other.customersList.begin()+i);
    }
    other.customersList.clear();

    for(unsigned int i=0;i<orderList.size();i++){
        orderList.push_back(OrderPair(other.orderList[i].first,other.orderList[i].second));
    }
    other.orderList.clear();

}
Table& Table::operator = (Table&& other){ //move assignment operator
    if(this!=&other) {
        capacity = other.capacity;
        open = other.open;
        for (unsigned int i = 0; i < customersList.size(); i++) {
            delete customersList[i];
            customersList.erase(customersList.begin()+i);
        }
        customersList.clear();
        for (unsigned int i = 0; i < other.customersList.size(); i++) {
            customersList.push_back(other.customersList[i]);
        }
        other.customersList.clear();
       orderList.clear();
        for (unsigned int i = 0; i < orderList.size(); i++) {
            orderList.push_back(OrderPair(other.orderList[i].first, other.orderList[i].second));
        }
        other.orderList.clear();
    }
    return *this;

}
Table::~Table() {//destructor

    for(unsigned int i=0; i<customersList.size();i++) {
        delete customersList[i];
        customersList[i]= nullptr;
    }
  customersList.clear();
    orderList.clear();
}
int Table::getCapacity() const{
    return capacity;
}

void Table::addCustomer(Customer* customer){
    customersList.push_back(customer);
}
void Table:: openTable() {
    if (!open) {
        this->open = true;
    }
}
int Table::getBill()
{
    int bill=0;
    if(open)
        for(unsigned int k=0;k<orderList.size();k++)
        {
            bill=bill+orderList[k].second.getPrice();
        }
    return bill;
}
bool Table:: isOpen()
{
    return this->open;
}
void Table:: closeTable()
{
    for(unsigned int i=0; i<customersList.size();i++) {
        delete customersList[i];
        customersList[i]= nullptr;
    }
    customersList.clear();
    orderList.clear();
    this->open=false;
}
  //customersList.clear();



void Table::removeCustomer(int id){
    bool found=false;
    for(unsigned int i=0;i<customersList.size()&&!found;i++){
        if(customersList[i]->getId()==id){
            customersList.erase(customersList.begin()+i);
            found=true;

        }
        if(customersList.size()==0){
            open=false;
        }
    }
}
Customer* Table::getCustomer(int id){
    bool found=false;
    for(unsigned int i=0;i<customersList.size()&&!found;i++){
        if(customersList[i]->getId()==id) {
            found=true;
            return customersList[i];
        }
    }
    return nullptr;
}
std::vector<Customer *> &Table::getCustomers() {

    return customersList;
}
std::vector<OrderPair> &Table::getOrders() {
    return orderList;
}


void Table:: order(const std::vector<Dish> &menu) {
    for(unsigned int i=0;i<customersList.size();i++) {
       int Id=customersList[i]->getId();
       vector<int> dishId=customersList[i]->order(menu);//order list for customer i
       for(unsigned int j=0;j<dishId.size();j++) {
           if (!dishId.empty()) {
               for (Dish p: menu)
                   if (p.getId() == dishId[j])
                       orderList.push_back(OrderPair(Id, p));
           }
       }
    }
}

void Table::setCapacity(int capacity_c) {
    capacity=capacity_c;
}

Table *Table::clone() {
    return new Table(*this);
}


