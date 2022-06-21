/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import <objc/runtime.h>
#import "NSURLSessionConfiguration+DoKit.h"
#import <DoraemonKit/DKMultiControlProtocol.h>

@implementation NSURLSessionConfiguration (DoKit)

+ (void)load {
    static dispatch_once_t dispatchOnce;
    dispatch_once(&dispatchOnce, ^{
        SEL originalSelector = @selector(defaultSessionConfiguration);
        SEL swizzledSelector = @selector(dk_defaultSessionConfiguration);
        
        Method originalMethod = class_getClassMethod(self.class, originalSelector);
        Method swizzledMethod = class_getClassMethod(self.class, swizzledSelector);
        
        if (originalMethod && swizzledMethod) {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }
    });
}

+ (NSURLSessionConfiguration *)dk_defaultSessionConfiguration {
    NSURLSessionConfiguration *defaultSessionConfiguration = self.dk_defaultSessionConfiguration;
    NSMutableArray<Class> *protocolClass = defaultSessionConfiguration.protocolClasses.mutableCopy;
    defaultSessionConfiguration.protocolClasses = nil;
    if (!protocolClass) {
        protocolClass = NSMutableArray.array;
    }
    [protocolClass insertObject:DKMultiControlProtocol.class atIndex:0];
    defaultSessionConfiguration.protocolClasses = protocolClass;
    
    return defaultSessionConfiguration;
}

@end
