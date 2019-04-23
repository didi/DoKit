//
//  DoraemonMethodUseTimeManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMethodUseTimeManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL on;

@end

NS_ASSUME_NONNULL_END
