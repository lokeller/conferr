#include <iostream>
#include <fstream>
#include <set>
#include <string>
#include "llvm/LLVMContext.h"
#include "llvm/Module.h"
#include "llvm/Bitcode/ReaderWriter.h"
#include "llvm/Support/CommandLine.h"
#include "llvm/Support/ManagedStatic.h"
#include "llvm/Support/MemoryBuffer.h"
#include "llvm/Support/PrettyStackTrace.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/System/Signals.h"
#include "llvm/Constants.h"
#include "llvm/Type.h"
#include "llvm/User.h"
#include "llvm/Instruction.h"
#include "llvm/Instructions.h"
#include "llvm/Support/CallSite.h"

#include <memory>
#include <map>
#include <typeinfo>
using namespace llvm;

std::set<std::string> functionsToDisconsider;

std::string processString(std::string& originalText, std::string foundText, std::string startOfText, std::string afterDirective) {
    //std::cout<<"***** O:"<<originalText<<"  F:"<<foundText<<"  S:"<<startOfText<<"  A:"<<afterDirective<<"\n";
    std::string::size_type pos = foundText.find(startOfText);
    if (pos==std::string::npos) {
        return "";
    }
    std::string newDirective = foundText.substr(pos+startOfText.size());
    //std::cout<<"after taking the head off "<<newDirective<<"\n";
    std::string::size_type end = newDirective.find(afterDirective);
    if (end==std::string::npos) {
        return "";
    }
    newDirective = newDirective.substr(0, newDirective.find(afterDirective));
    return newDirective;
}



void processCallInstr(CallInst* call, std::map<std::string, std::set<std::string> >& finalMap, User* i, std::string originalText, std::string startOfText, std::string afterDirective, std::string foundString) {
    std::cout<<"CALL\n";
    for (unsigned int index=0; index<call->getNumOperands();index++) {
        Value* operand = call->getOperand(index);
        if (operand==i) {
            //std::cout<<"Used in position "<<index<<"\n";
            Function* function = call->getCalledFunction();
            if (functionsToDisconsider.find(function->getNameStr())!=functionsToDisconsider.end()) {
                continue;
            }
            std::cout<<"Used in function: "<<function->getNameStr()<<"\n";
            for (Function::use_iterator b = function->use_begin(), be = function->use_end(); b != be; ++b) {
                Value* value2 = b->getOperand(index);
                if (ConstantExpr* constant = dyn_cast<ConstantExpr>(value2)) {
                    if ( ConstantArray* arrayStr = dyn_cast<ConstantArray>(constant->getOperand(0)->getOperand(0))) {
                        std::string text = processString(originalText, arrayStr->getAsString(), startOfText, afterDirective);
                        if (text!="" && text!=originalText) {
                            finalMap[foundString].insert(text);
                        }
                    }
                }
            }
        }
    }
}

void processset(ConstantArray* array, std::map<std::string, std::set<std::string> >& finalMap, std::string originalText, std::string startOfText, std::string afterDirective, std::string foundString) {
    std::cout<<"ARRAY\n";
    for (unsigned int index=0; index<array->getNumOperands();index++) {
        if ( GlobalVariable*  globalValue = dyn_cast<GlobalVariable>(array->getOperand(index)->getOperand(0))) {
            if (globalValue->hasInitializer()) {
                Constant* constant = globalValue->getInitializer();
                if ( ConstantArray* arrayStr = dyn_cast<ConstantArray>(constant)) {
                    if (arrayStr->isString()) {
                        std::string text = processString(originalText, arrayStr->getAsString(), startOfText, afterDirective);
                        if (text!="" && text!=originalText) {
                            finalMap[foundString].insert(text);
                        }
                    }
                }
            }
        }
    }
}

std::string goDown(ConstantExpr* useExpr, StoreInst* store) {
    for (Value::use_iterator iCanFindAName = useExpr->use_begin() , endName = useExpr->use_end(); iCanFindAName != endName; iCanFindAName++) {
        Value* mightBeGood = *iCanFindAName;
        StoreInst* e;
        if (( e= dyn_cast<StoreInst>(mightBeGood)) && e!=store) {
            Value* lala = ((GlobalVariable*)((Constant*)e->getOperand(0))->getOperand(0))->getInitializer();
            //        const std::type_info &info5 = typeid(*lala);
            //        std::cout << info5.name() << std::endl<<"---------------------------\n";
            if (ConstantArray* newString = dyn_cast<ConstantArray>(lala)) {
                if (newString->isString()) {
                    std::cout<<"Found as string:  "<<newString->getAsString()<<"\n";
                }
            }
        }
    }
}


std::string goUp(StoreInst* store, GlobalVariable* global, User* operand) {
    for (Value::use_iterator globalUser = global->use_begin(), globalEnd = global->use_end(); globalUser!=globalEnd; globalUser++) {
        Value* _value = *globalUser;
        if (ConstantExpr* useExpr = dyn_cast<ConstantExpr>(_value)) {
            User* u = useExpr->getOperand(3);
            if (u==operand) {
                goDown(useExpr, store);

            }
        }
    }
}

void processStoreInstWithStructure(StoreInst* store) {
//    std::cout<<"Store instruction!!!\n";
    Value* pointer = store->getPointerOperand();
    if (ConstantExpr* expr = dyn_cast<ConstantExpr>(pointer)) {
//        const std::type_info &info = typeid(*pointer);

        //      std::cout << info.name() << std::endl;
        User* operand = expr->getOperand(0);
        if (GlobalVariable* global = dyn_cast<GlobalVariable>(operand)) {
//           const Type* theType = operand->getType();
            //          std::cout<<"Operand 0 = "<<operand->getNameStr()<<"\n";

            //          const std::type_info &info2 = typeid(*operand);
            //          std::cout << info2.name() << std::endl;

            operand = expr->getOperand(3);
            //        std::cout<<"Operand 3 = "<<operand->getNameStr()<<"\n";
            //        const std::type_info &info3 = typeid(*operand);
            //        std::cout << info3.name() << std::endl;
            if (ConstantInt* intVal = dyn_cast<ConstantInt>(operand)) {
                goUp(store, global, operand);
            }
        }
    }
}


void processConstantStruct(ConstantStruct* constant, Value* value) {
    int index=0;
    for (int i=0;i<constant->getNumOperands();i++) {
        User* __val = constant->getOperand(i);

        if (ConstantExpr* expr = dyn_cast<ConstantExpr>(__val)) {
            if (expr->getOperand(0)==value) {
                //      std::cout<<"Yes!\n";
                index=i;
                break;
            }
        }
    }
    //find users of constant
    for (Value::use_iterator uIt = constant->use_begin(), uEnd = constant->use_end(); uIt!=uEnd; uIt++) {
        Value* __val = *uIt;

        if (ConstantArray* array = dyn_cast<ConstantArray>(__val)) {
            //      std::cout<<"  "<<array->getNumOperands()<<"\n";
            for (int i=0;i<array->getNumOperands();i++) {
                ConstantStruct* constantIdx = (ConstantStruct*) array->getOperand(i);
                if (constantIdx!=constant) {
                    if (ConstantExpr* expr = dyn_cast<ConstantExpr>(constantIdx->getOperand(index))) {
                        Value* _value = expr->getOperand(0);
                        if (GlobalVariable* globalVar = dyn_cast<GlobalVariable>(_value)) {
                            if (ConstantArray* newString = dyn_cast<ConstantArray>(globalVar->getInitializer())) {
                                if (newString->isString()) {
                                    std::cout<<"Found as string:  "<<newString->getAsString()<<"\n";
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
void addStringGlobalVariable(std::map<std::string, std::map<std::string, std::set<std::string> > >&finalMap, ConstantArray* arrayStr, std::set<std::string> v, Value* value) {
    std::string str = arrayStr->getAsString();
    for (std::set<std::string>::iterator it2 = v.begin(); it2!=v.end(); it2++) {
        std::string::size_type startOfText=str.find(*it2, 0);
        if (startOfText!=std::string::npos) {
            std::string afterDirective=str.substr(startOfText+(*it2).size());
            for (Value::use_iterator i = value->use_begin(), e = value->use_end(); i != e; ++i) {
                User* theValue = *i;

                for (Value::use_iterator user=i->use_begin(), end=i->use_end(); user!=end; user++) {

                    if (CallInst* call = dyn_cast<CallInst>(*user)) {
                        processCallInstr(call, finalMap[*it2], theValue, *it2, str.substr(0, startOfText), afterDirective, str);
                    }
                    if (ConstantArray* array = dyn_cast<ConstantArray>(*user)) {
                        processset(array, finalMap[*it2], *it2, str.substr(0, startOfText), afterDirective, str);
                    }
                    if (StoreInst* store = dyn_cast<StoreInst>(*user)) {
                        processStoreInstWithStructure(store);
                    }
                    if (ConstantStruct* constant = dyn_cast<ConstantStruct>(*user)) {
                        processConstantStruct(constant, value);
                    }
                }
            }
        }
    }
}


int main (int argc, char * const argv[]) {
    std::cerr<<"Targets: "<<argv[1]<<"  Program: "<<argv[2]<<"\n";
    std::ifstream input(argv[1]);
    if (!input.is_open()) {
        std::cout<<"No file!\n";
        return 1;
    }
    std::set<std::string> v;

    std::string line;
    while (input >> line) {
        v.insert(line);
    }

    std::ifstream functions(argv[2]);
    if (!functions.is_open()) {
        std::cout<<"No file!\n";
        return 1;
    }

    while (functions >> line) {
        functionsToDisconsider.insert(line);
    }

    LLVMContext &Context = getGlobalContext();
    std::string ErrorMessage;
    std::auto_ptr<Module> M;

    if (MemoryBuffer *Buffer
            = MemoryBuffer::getFileOrSTDIN(StringRef(argv[2]), &ErrorMessage)) {
        M.reset(ParseBitcodeFile(Buffer, Context, &ErrorMessage));
        delete Buffer;
    }

    if (!M.get()) {
        std::cout<<"Could not read the module\n";
        return 1;
    }
    std::map<std::string, std::map<std::string , std::set<std::string> > > finalMap;

    for (Module::global_iterator it = M->global_begin(); it!=M->global_end(); it++) {
        if ( GlobalVariable*  value = dyn_cast<GlobalVariable>(&*it)) {
            //   std::cout<<"Global: "<<value->getNameStr()<<"\n";
            if (value->hasInitializer()) {
                //     std::cout<<"   and has init\n";
                Constant* constant = value->getInitializer();
                if ( ConstantArray* arrayStr = dyn_cast<ConstantArray>(constant)) {
                    if (arrayStr->isString()) {
                        //           std::cout<<"Inspecting: "<<arrayStr->getAsString()<<"\n";
                        addStringGlobalVariable(finalMap, arrayStr, v, value);
                        //		std::cout<<"\n\n";
                    }
                }
            }
        }
    }

    std::ofstream out("output");
    out<<"<siblings>\n";
    for (std::map<std::string, std::map< std:: string, std::set<std::string> > >::iterator originalToFound = finalMap.begin(), endOriginal=finalMap.end(); originalToFound!=endOriginal; originalToFound++) {
        out<<"\t<"<<originalToFound->first<<">\n";
        for (std::map<std::string, std::set<std::string > >::iterator siblings = originalToFound->second.begin(), endSibling = originalToFound->second.end(); siblings!=endSibling; siblings++) {
            out << "\t\t<"<<siblings->first<<"/>\n";
            for (std::set<std::string>::iterator foundString = siblings->second.begin() , lastString = siblings->second.end(); foundString!=lastString; foundString++) {
                out << "\t\t\t<"<<*foundString<<"/>\n";
            }
            out << "\t\t</"<<siblings->first<<"/>\n";
        }
        out<<"\t</"<<originalToFound->first<<">\n";
    }
    out<<"</siblings>\n";
    out.flush();
    out.close();
    return 0;
}
