#pragma once

#ifdef __cplusplus
extern "C" {
#endif

#include <stdbool.h>

bool initNative(int id, char* name, double x, double y, bool isActive, long* ptr);
void freeNative();
struct nativeData getNativeData();

int getID();
char* getName();
double getX();
double getY();
bool getIsActive();
long* getPtr();
long getPtrValue();

void setID(int id);
void setName(char* name);
void setX(double x);
void setY(double y);
void setIsActive(bool isActive);
void setPtr(long* ptr);
void setPtrValue(long value);

#ifdef __cplusplus
}
#endif