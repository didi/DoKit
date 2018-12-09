//
//  DoraemonAllTestManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/25.
//

#import <Foundation/Foundation.h>

typedef void (^DoraemonAllTestManagerBlock)(NSDictionary *upLoadData);

@interface DoraemonAllTestManager : NSObject

+ (DoraemonAllTestManager *)shareInstance;

@property (nonatomic, assign) NSTimeInterval startTimeInterval;

@property (nonatomic, assign) BOOL fpsSwitchOn;
@property (nonatomic, assign) BOOL cpuSwitchOn;
@property (nonatomic, assign) BOOL memorySwitchOn;
@property (nonatomic, assign) BOOL flowSwitchOn;

@property (nonatomic, assign) BOOL startTestOn;

- (void)startRecord;

- (void)endRecord;

- (void)addPerformanceBlock:(DoraemonAllTestManagerBlock)block;

@end
