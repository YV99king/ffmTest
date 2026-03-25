#include "native.h"

struct nativeData
{
    int id;
    char* name;
    double x;
    double y;
    bool isActive;
    long* ptr;
};
long ptrValue = 0;
nativeData stored = {0, nullptr, 0.0, 0.0, false, &ptrValue};

bool initNative(int id, char* name, double x, double y, bool isActive, long* ptr)
{
    if (ptr == nullptr || id < 0)
        return false;

    stored.id = id;
    stored.name = name;
    stored.x = x;
    stored.y = y;
    stored.isActive = isActive;
    stored.ptr = ptr;
    return true;
}
void freeNative()
{
    stored = {0, nullptr, 0.0, 0.0, false, nullptr};
}

nativeData getNativeData()
{
    return stored;
}

int getID()
{
    return stored.id;
}
char* getName()
{
    return stored.name;
}
double getX()
{
    return stored.x;
}
double getY()
{
    return stored.y;
}
bool getIsActive()
{
    return stored.isActive;
}
long* getPtr()
{
    return stored.ptr;
}
long getPtrValue()
{
    return *stored.ptr;
}

void setID(int id)
{
    stored.id = id;
}
void setName(char* name) 
{
    stored.name = name;
}
void setX(double x)
{
    stored.x = x;
}
void setY(double y)
{
    stored.y = y;
}
void setIsActive(bool isActive)
{
    stored.isActive = isActive;
}
void setPtr(long* ptr)
{
    stored.ptr = ptr;
}
void setPtrValue(long value)
{
    if (stored.ptr != nullptr)
        *stored.ptr = value;
}