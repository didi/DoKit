//
//  DoraemonWeakNetworkManager.h
//  DoraemonKit
//
//  Created by didi on 2019/11/21.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWeakNetworkManager : NSObject

@property (nonatomic, assign) BOOL shouldWeak;
@property (nonatomic, assign) NSUInteger selecte;
@property (nonatomic, assign) NSUInteger upFlowSpeed;
@property (nonatomic, assign) NSUInteger downFlowSpeed;
@property (nonatomic, assign) NSUInteger delayTime;


+ (DoraemonWeakNetworkManager *)shareInstance;

- (void)canInterceptNetFlow:(BOOL)enable;
- (void)startRecord;
- (void)endRecord;

@end

NS_ASSUME_NONNULL_END
