//
//  DoraemonAllTestWindow.h
//  AFNetworking
//
//  Created by didi on 2019/10/9.
//

#import <UIKit/UIKit.h>
#import "DoraemonNetFlowHttpModel.h"

@protocol DoraemonAllTestWindowDelegate <NSObject>

- (void)doraemonAllTestWindowClosed;

@end

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonAllTestWindow : UIWindow

+ (DoraemonAllTestWindow *)shareInstance;

- (void)hide;

- (void)addDelegate:(id<DoraemonAllTestWindowDelegate>) delegate;

-(void)updateCommonValue:(NSString *)memory cpu:(NSString *)cpu fps:(NSString *)fps;

-(void)updateFlowValue:(NSString *)upFlow downFlow:(NSString *)downFlow;

-(void)hideFlowValue;

// start位置
@property (nonatomic) CGPoint startingPosition;
@property (nonatomic, assign) BOOL flowChanged;

@end

NS_ASSUME_NONNULL_END
