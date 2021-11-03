//
//  DoraemonHealthManager.h
//  DoraemonKit
//
//  Created by didi on 2020/1/2.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "DoraemonNetFlowHttpModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHealthManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL start;

- (void)rebootAppForHealthCheck;

- (void)startHealthCheck;

- (void)stopHealthCheck;

- (void)startEnterPage:(Class)vcClass;

- (void)enterPage:(Class)vcClass;

- (void)leavePage:(Class)vcClass;

- (void)addHttpModel:(DoraemonNetFlowHttpModel *)httpModel;

- (void)addANRInfo:(NSDictionary *)anrInfo;

- (void)addSubThreadUI:(NSDictionary *)info;

- (void)addUILevel:(NSDictionary *)info;

- (void)addLeak:(NSDictionary *)info;

- (void)openH5Page:(NSString *)h5Url;

//检测结果
@property (nonatomic, assign) CGFloat startTime;//本次启动时间 单位ms
@property (nonatomic, copy) NSString *costDetail;//启动流程耗时详情
@property (nonatomic, copy) NSString *caseName;//用例名称
@property (nonatomic, copy) NSString *testPerson;//测试人名称


- (BOOL)blackList:(Class)vcClass;

@end

NS_ASSUME_NONNULL_END
