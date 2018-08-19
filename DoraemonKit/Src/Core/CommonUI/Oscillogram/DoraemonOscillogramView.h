//
//  DoraemonOscillogramView.h
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/3.
//

#import <UIKit/UIKit.h>

@interface DoraemonPoint : NSObject

@property (nonatomic, assign) CGFloat x;
@property (nonatomic, assign) CGFloat y;

@end

@interface DoraemonOscillogramView : UIView

@property (nonatomic, strong) UIColor *strokeColor;
@property (nonatomic, assign) NSInteger numberOfPoints;

- (void)addHeightValue:(CGFloat)showHeight andTipValue:(NSString *)tipValue;

- (void)setLowValue:(NSString *)value;

- (void)setHightValue:(NSString *)value;

- (void)clear;

@end
