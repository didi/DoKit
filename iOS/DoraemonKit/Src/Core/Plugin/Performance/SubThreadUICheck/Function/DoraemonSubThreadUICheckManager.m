//
//  DoraemonSubThreadUICheckManager.m
//  DoraemonKit
//
//  Created by yixiang on 2018/9/13.
//

#import "DoraemonSubThreadUICheckManager.h"

@implementation DoraemonSubThreadUICheckManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if(self){
        _checkArray = [[NSMutableArray alloc] init];
    }
    return self;
}

@end
