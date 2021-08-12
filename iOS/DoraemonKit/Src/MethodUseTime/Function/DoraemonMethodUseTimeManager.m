//
//  DoraemonMethodUseTimeManager.m
//  DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimeManager.h"
#import "DoraemonCacheManager.h"
#import <DoraemonLoadAnalyze/DoraemonLoadAnalyze.h>
#import "DoraemonDefine.h"

@implementation DoraemonMethodUseTimeManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (void)setOn:(BOOL)on{
    [[DoraemonCacheManager sharedInstance] saveMethodUseTimeSwitch:on];
}

- (BOOL)on{
    return [[DoraemonCacheManager sharedInstance] methodUseTimeSwitch];
}

- (NSArray *)fixLoadModelArray{
    NSMutableArray *loadModelArray = [NSMutableArray arrayWithArray:dlaLoadModels];
    [loadModelArray sortUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        CGFloat costA = [obj1[@"cost"] floatValue];
        CGFloat costB = [obj2[@"cost"] floatValue];
        if (costA < costB) {
            return NSOrderedDescending;
        }else{
            return NSOrderedAscending;
        }
    }];
    CGFloat allCost = 0.f;
    if(loadModelArray && loadModelArray.count>0){
        for (NSDictionary *dic in loadModelArray) {
            CGFloat cost = [dic[@"cost"] floatValue];
            allCost += cost;
        }
        NSDictionary *allDic = @{
                                 @"name":DoraemonLocalizedString(@"总共耗时"),
                                 @"cost":@(allCost)
                                 };
        [loadModelArray insertObject:allDic atIndex:0];
    }
    return [NSArray arrayWithArray:loadModelArray];
}

- (NSArray *)fixLoadModelArrayForHealth{
    NSMutableArray *loadModelArray = [NSMutableArray arrayWithArray:dlaLoadModels];
    [loadModelArray sortUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        CGFloat costA = [obj1[@"cost"] floatValue];
        CGFloat costB = [obj2[@"cost"] floatValue];
        if (costA < costB) {
            return NSOrderedDescending;
        }else{
            return NSOrderedAscending;
        }
    }];
    NSMutableArray *fixArrayForHealth = [[NSMutableArray alloc] init];
    for (NSDictionary *dic in loadModelArray) {
        NSString *className = dic[@"name"];
        NSString *costTime = dic[@"cost"];
        [fixArrayForHealth addObject:@{
            @"className" : className,
            @"costTime" : costTime//单位ms
        }];
    }
    return [NSArray arrayWithArray:fixArrayForHealth];
}

@end
