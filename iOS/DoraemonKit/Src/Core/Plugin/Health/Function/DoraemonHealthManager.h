//
//  DoraemonHealthManager.h
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHealthManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL start;

- (void)startHealthCheck;

- (void)stopHealthCheck;

- (void)enterPage:(Class)vcClass;

- (void)leavePage:(Class)vcClass;



@end

NS_ASSUME_NONNULL_END
