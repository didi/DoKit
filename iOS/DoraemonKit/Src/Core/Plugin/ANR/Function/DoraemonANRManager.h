//
//  DoraemonANRManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import <Foundation/Foundation.h>

@interface DoraemonANRManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL anrTrackOn;
@property (nonatomic, strong) NSMutableArray *anrArray;

/*
 卡顿时长阈值，单位为秒，
 */
@property (nonatomic, assign) int64_t timeOut;

- (void)start;
- (void)stop;

@end
