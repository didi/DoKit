//
//  DoraemonLoadAnalyze.m
//  DoraemonLoadAnalyze
//
//  Created by yixiang on 2019/1/2.
//  Copyright © 2019年 yixiang. All rights reserved.
//

#import "DoraemonLoadAnalyze.h"
#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import <mach/mach.h>
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>
#include <mach-o/dyld.h>
#include <mach-o/nlist.h>
#include <mach-o/getsect.h>
#include <string>

#pragma mark -- C++ method list template
template <typename Element, typename List, uint32_t FlagMask>
struct entsize_list_tt {
    uint32_t entsizeAndFlags;
    uint32_t count;
    Element first;
    
    uint32_t entsize() const {
        return entsizeAndFlags & ~FlagMask;
    }
    uint32_t flags() const {
        return entsizeAndFlags & FlagMask;
    }
    
    Element& getOrEnd(uint32_t i) const {
        assert(i <= count);
        return *(Element *)((uint8_t *)&first + i*entsize());
    }
    Element& get(uint32_t i) const {
        assert(i < count);
        return getOrEnd(i);
    }
    
    size_t byteSize() const {
        return sizeof(*this) + (count-1)*entsize();
    }
    
    List *duplicate() const {
        return (List *)memdup(this, this->byteSize());
    }
    
    struct iterator;
    const iterator begin() const {
        return iterator(*static_cast<const List*>(this), 0);
    }
    iterator begin() {
        return iterator(*static_cast<const List*>(this), 0);
    }
    const iterator end() const {
        return iterator(*static_cast<const List*>(this), count);
    }
    iterator end() {
        return iterator(*static_cast<const List*>(this), count);
    }
    
    struct iterator {
        uint32_t entsize;
        uint32_t index;  // keeping track of this saves a divide in operator-
        Element* element;
        
        typedef std::random_access_iterator_tag iterator_category;
        typedef Element value_type;
        typedef ptrdiff_t difference_type;
        typedef Element* pointer;
        typedef Element& reference;
        
        iterator() { }
        
        iterator(const List& list, uint32_t start = 0)
        : entsize(list.entsize())
        , index(start)
        , element(&list.getOrEnd(start))
        { }
        
        const iterator& operator += (ptrdiff_t delta) {
            element = (Element*)((uint8_t *)element + delta*entsize);
            index += (int32_t)delta;
            return *this;
        }
        const iterator& operator -= (ptrdiff_t delta) {
            element = (Element*)((uint8_t *)element - delta*entsize);
            index -= (int32_t)delta;
            return *this;
        }
        const iterator operator + (ptrdiff_t delta) const {
            return iterator(*this) += delta;
        }
        const iterator operator - (ptrdiff_t delta) const {
            return iterator(*this) -= delta;
        }
        
        iterator& operator ++ () { *this += 1; return *this; }
        iterator& operator -- () { *this -= 1; return *this; }
        iterator operator ++ (int) {
            iterator result(*this); *this += 1; return result;
        }
        iterator operator -- (int) {
            iterator result(*this); *this -= 1; return result;
        }
        
        ptrdiff_t operator - (const iterator& rhs) const {
            return (ptrdiff_t)this->index - (ptrdiff_t)rhs.index;
        }
        
        Element& operator * () const { return *element; }
        Element* operator -> () const { return element; }
        
        operator Element& () const { return *element; }
        
        bool operator == (const iterator& rhs) const {
            return this->element == rhs.element;
        }
        bool operator != (const iterator& rhs) const {
            return this->element != rhs.element;
        }
        
        bool operator < (const iterator& rhs) const {
            return this->element < rhs.element;
        }
        bool operator > (const iterator& rhs) const {
            return this->element > rhs.element;
        }
    };
};

struct method_t {
    SEL name;
    const char *types;
    IMP imp;
    
    struct SortBySELAddress :
    public std::binary_function<const method_t&,
    const method_t&, bool>
    {
        bool operator() (const method_t& lhs,
                         const method_t& rhs)
        { return lhs.name < rhs.name; }
    };
};

struct method_list_t : entsize_list_tt<method_t, method_list_t, 0x3> {
};

typedef struct classref * classref_t;
#ifndef __LP64__
typedef struct mach_header headerType;
#else
typedef struct mach_header_64 headerType;
#endif

struct category_t {
    const char *name;
    classref_t cls;
    void *instanceMethods;
    struct method_list_t *classMethods;
    void *protocols;
    void *instanceProperties;
    void *_classProperties;
    void *methodsForMeta(bool isMeta) {
        if (isMeta) return classMethods;
        else return instanceMethods;
    }
    void *propertiesForMeta(bool isMeta, struct header_info *hi);
};

template <typename T>
T* getDataSection(const headerType *mhdr, const char *sectname, size_t *outBytes, size_t *outCount) {
    unsigned long byteCount = 0;
    
    
    T* data = (T*)getsectiondata(mhdr, "__DATA", sectname, &byteCount);
    if (!data) {
        data = (T*)getsectiondata(mhdr, "__DATA_CONST", sectname, &byteCount);
    }
    if (!data) {
        data = (T*)getsectiondata(mhdr, "__DATA_DIRTY", sectname, &byteCount);
    }
    if (outBytes) *outBytes = byteCount;
    if (outCount) *outCount = byteCount / sizeof(T);
    return data;
}

#define GETSECT(name, type, sectname)                                   \
type *name(const headerType *mhdr, size_t *outCount) {              \
return getDataSection<type>(mhdr, sectname, nil, outCount);     \
}

GETSECT(_getObjc2NonlazyClassList,    classref_t,      "__objc_nlclslist");
GETSECT(_getObjc2NonlazyCategoryList, category_t *,    "__objc_nlcatlist");


static NSMutableDictionary *loadMS;//To record the name of category
static NSMutableDictionary *loadCS;//To void repeat analysis same class

NSMutableArray<NSDictionary *> *dlaLoadModels;

extern "C"{
    category_t **nlcategarylist;
    size_t categaryCount;
}

//  a IMP that returns a value
typedef id (* _IMP) (id, SEL, ...);
// no return value
typedef void (* _VIMP) (id, SEL, ...);

#define D_NEED_LOG

#ifdef D_NEED_LOG
#define DLALog(...) NSLog(__VA_ARGS__)
#else
#define DLALog(...)
#endif

#pragma mark -- static func define
const struct mach_header *get_target_image_header() {
    
    const uint32_t imageCount = _dyld_image_count();
    const struct mach_header* target_image_header = 0;
    
    for(uint32_t iImg = 0; iImg < imageCount; iImg++) {
        const char *image_name = _dyld_get_image_name(iImg);
        NSBundle *mainBundle = [NSBundle mainBundle];
        NSString *executableFile = [mainBundle objectForInfoDictionaryKey:(NSString*)kCFBundleExecutableKey];
        const char *target_image_name = (executableFile).UTF8String;
        if (strstr(image_name, target_image_name) != NULL) {
            target_image_header = _dyld_get_image_header(iImg);
            //printf("image_name = %s\n" , image_name);
            break;
        }
    }
    
    return target_image_header;
}

#pragma mark -- lazy list
category_t **get_non_lazy_categary_list(size_t *count) {
    category_t **nlcatlist = NULL;
    nlcatlist = _getObjc2NonlazyCategoryList((headerType *)get_target_image_header(), count);
    return nlcatlist;
}

classref_t *get_non_lazy_class_list(size_t *count) {
    classref_t *nlclslist = NULL;
    nlclslist = _getObjc2NonlazyClassList((headerType *)get_target_image_header(), count);
    return nlclslist;
}

void swizzeLoadMethodInClasss(Class cls, BOOL isCategary){
    unsigned int methodCount = 0;
    Method * methods = class_copyMethodList(cls, &methodCount);
    NSUInteger currentLoadIndex = 0;
    for(unsigned int methodIndex = 0; methodIndex < methodCount; ++methodIndex){
        Method method = methods[methodIndex];
        //objc_method_description *des = method_getDescription(method);
        std::string methodName(sel_getName(method_getName(method)));
        if(methodName == "load"){
            ++currentLoadIndex;
            _VIMP load_IMP = (_VIMP)method_getImplementation(method);
            method_setImplementation(method, imp_implementationWithBlock(^(id target, SEL action) {
                //DLALog(@"DLA >>>> before");
                CFTimeInterval begin = CACurrentMediaTime();
                
                load_IMP(target,action);
                
                CFTimeInterval end = CACurrentMediaTime();
                if (!dlaLoadModels) {
                    dlaLoadModels = [[NSMutableArray alloc] initWithCapacity:10];
                }
                
                NSString *name = [loadMS valueForKey:[NSString stringWithFormat:@"%p",load_IMP]];
                if (name && name.length > 0) {
                }else{
                    name = NSStringFromClass(cls);
                }
                [dlaLoadModels addObject:@{
                                           @"name":name,
                                           @"cost":@(1000 * (end - begin))
                                           }];
            }));
        }
    }
    //DLALog(@"%@",@(currentLoadIndex));//多个category都实现了load的话会有多个
}

IMP _category_getLoadMethod(category_t *cat)
{
    const method_list_t *mlist;
    mlist = cat->classMethods;
    if (mlist) {
        for (const auto& meth : *mlist) {
            const char *name = (const char *)(void *)(meth.name);
            if (0 == strcmp(name, "load")) {
                return meth.imp;
            }
        }
    }
    return nil;
}

void printLoadAnalyzeInfo(void){
    NSLog(@"DLA >>>> all load cost info below :");
    NSLog(@"\n");
    
    NSArray *testArr = dlaLoadModels;
    
    for(NSDictionary *costInfo in testArr){
        NSString *name = costInfo[@"name"];
        NSString *cost = costInfo[@"cost"];
        NSLog(@"%@ - %@ms",name,cost);
    }
    NSLog(@"\n");
}

@interface DoraemonLoadAnalyze : NSObject
@end
@implementation DoraemonLoadAnalyze
+(void)load{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    BOOL needMethodUseTime = [defaults boolForKey:@"doraemon_method_use_time_key"];
    if (!needMethodUseTime) {
        return;
    }
    
    CFTimeInterval begin = CACurrentMediaTime();
    DLALog(@"DLA >>>> DoraemonLoadAnalyze load start");
    if(!loadMS){
        loadMS = [[NSMutableDictionary alloc]init];
    }else{
        [loadMS removeAllObjects];
    }

    if(!loadCS){
        loadCS = [[NSMutableDictionary alloc]init];
    }else{
        [loadCS removeAllObjects];
    }

    nlcategarylist = get_non_lazy_categary_list(&categaryCount);
    for (int i = 0; i < categaryCount; i++) {
        Class cls = (Class)CFBridgingRelease(nlcategarylist[i]->cls);
        cls = object_getClass(cls);
        NSString *name = [NSString stringWithCString:nlcategarylist[i]->name encoding:NSUTF8StringEncoding];
        category_t *cat = nlcategarylist[i];
        _VIMP load_IMP = (_VIMP)_category_getLoadMethod(cat);
        [loadMS addEntriesFromDictionary:@{[NSString stringWithFormat:@"%p",load_IMP]:[NSString stringWithFormat:@"%@(%@)",cls,name]}];
        [loadCS addEntriesFromDictionary:@{[NSString stringWithFormat:@"%@",cls]:cls}];

        //DLALog(@"DLA >>>> category_t:%@ (%@)",cls,name);
        //swizzeLoadMethodInClasss(cls, YES);
    }
    
    for (NSString *key in loadCS) {
        Class cls = loadCS[key];
        swizzeLoadMethodInClasss(cls, YES);
    }

    size_t count = 0;
    classref_t *nlclslist = get_non_lazy_class_list(&count);

    for (int i = 0; i < count; i++) {
        //DLALog(@"DLA >>>> %p",nlclslist[i]);
        classref_t nlcls = nlclslist[i];
        Class cls = (__bridge Class)nlcls;
        // ios deployment target 8.0有一个问题 '__ARCLite__'这个Class有点特殊，这个类也实现了load
        if ([@"__ARCLite__" isEqualToString:NSStringFromClass(cls)]) {
            continue;
        }
        cls = (Class)CFBridgingRelease(nlclslist[i]);
        cls = object_getClass(cls);
        //DLALog(@"DLA >>>> classref_t:%@",cls);

        if(![[loadCS allKeys] containsObject:[NSString stringWithFormat:@"%@",cls]])
        {
            swizzeLoadMethodInClasss(cls, NO);
        }
    }

    CFTimeInterval end = CACurrentMediaTime();

    DLALog(@"DLA >>>> DoraemonLoadAnalyze load end , costs : %@",[NSString stringWithFormat:@"%@ - %@ms",NSStringFromClass([self class]), @(1000 * (end - begin))]);
}
@end




