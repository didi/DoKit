//
//  DoraemonANRManager.h
//  DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

typedef void (^DoraemonANRManagerBlock)(NSDictionary *anrInfo);

@interface DoraemonANRManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL anrTrackOn; 

/*
 卡顿时长阈值，单位为秒，
 */
@property (nonatomic, assign) CGFloat timeOut;

- (void)addANRBlock:(DoraemonANRManagerBlock)block;

- (void)start;
- (void)stop;

@end
