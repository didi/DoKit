//
//  DoraemonMCGestureTargetActionPair.h
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCGestureTargetActionPair : NSObject

@property (nonatomic , weak ) id target ;
@property (nonatomic , assign) SEL action ;
@property (nonatomic , weak) id sender;
- (instancetype)initWithTarget:(id)target action:(SEL)action sender:(id)sender;
- (BOOL)isEqualToTarget:(id)target andAction:(SEL)action;

- (BOOL)valid;

- (void)doAction ;

@end

NS_ASSUME_NONNULL_END
