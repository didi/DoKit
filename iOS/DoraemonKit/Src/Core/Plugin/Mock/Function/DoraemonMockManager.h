//
//  DoraemonMockManager.h
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import <Foundation/Foundation.h>
#import "DoraemonMockAPI.h"

@interface DoraemonMockManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, strong) NSMutableArray<DoraemonMockAPI *> *dataArray;

- (void)queryMockData;

@end

