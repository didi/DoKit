//
//  DoraemonWeakNetworkManager.h
//  AFNetworking
//
//  Created by didi on 2019/11/21.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWeakNetworkManager : NSObject

@property (nonatomic, assign) BOOL shouldWeak;
@property (nonatomic, assign) NSUInteger selecte;
@property (nonatomic, assign) NSUInteger upFlowSpeed;
@property (nonatomic, assign) NSUInteger downFlowSpeed;
@property (nonatomic, assign) NSUInteger outTime;



+ (DoraemonWeakNetworkManager *)shareInstance;

- (void)changeWeakSize:(NSInteger)size;

- (void)canInterceptNetFlow:(BOOL)enable;

- (void)selectWeakItemChange:(NSUInteger)select sleepTime:(CGFloat)time weakSize:(NSUInteger)size;

@end

NS_ASSUME_NONNULL_END
