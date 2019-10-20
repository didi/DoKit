//
//  DoraemonAllTestStatisticsManager.h
//  AFNetworking
//
//  Created by didi on 2019/9/27.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonAllTestStatisticsManager : NSObject

+ (DoraemonAllTestStatisticsManager *)shareInstance;

@property (nonatomic, copy) NSDictionary *resultDic;

-(NSMutableArray *)getLastResultArray;

@end

NS_ASSUME_NONNULL_END
