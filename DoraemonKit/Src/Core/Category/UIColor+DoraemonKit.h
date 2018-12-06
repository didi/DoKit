//
//  UIColor+DoreamonKit.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>

@interface UIColor (DoreamonKit)

@property (nonatomic, readonly) CGFloat red; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat green; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat blue; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat white; // Only valid if colorSpaceModel == kCGColorSpaceModelMonochrome
@property (nonatomic, readonly) CGFloat alpha;


+ (UIColor *)doraemon_colorWithHex:(UInt32)hex;
+ (UIColor *)doraemon_colorWithHex:(UInt32)hex andAlpha:(CGFloat)alpha;
+ (UIColor *)doraemon_colorWithHexString:(NSString *)hexString;

+ (UIColor *)doraemon_colorWithString:(NSString *)hexString;

@end
