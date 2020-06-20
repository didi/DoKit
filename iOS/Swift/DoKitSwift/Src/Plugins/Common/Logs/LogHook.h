//
//  LogHook.h
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface LogHook : NSObject
+ (instancetype)shared;
@property (assign, nonatomic) BOOL *isOn;
//@property (strong, nonatomic) <#className#> *<#name#>;
//void rebindFunction(void);
//void bindFuntion(void);
@end

NS_ASSUME_NONNULL_END
