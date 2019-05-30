//
//  DoraemonStatisticsUtil.h
//  AFNetworking
//
//  Created by yixiang on 2018/12/10.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonStatisticsUtil : NSObject

+ (nonnull DoraemonStatisticsUtil *)shareInstance;

@property (nonatomic, assign) BOOL noUpLoad;

//统计用户信息，便于了解该开源产品的使用量 (请大家放心，我们不用于任何恶意行为)
- (void)upLoadUserInfo;

@end

NS_ASSUME_NONNULL_END
