//
//  UIColor+Doraemon.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>

@interface UIColor (Doraemon)

@property (nonatomic, readonly) CGFloat red; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat green; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat blue; // Only valid if canProvideRGBComponents is YES
@property (nonatomic, readonly) CGFloat white; // Only valid if colorSpaceModel == kCGColorSpaceModelMonochrome
@property (nonatomic, readonly) CGFloat alpha;


+ (UIColor *)doraemon_colorWithHex:(UInt32)hex;
+ (UIColor *)doraemon_colorWithHex:(UInt32)hex andAlpha:(CGFloat)alpha;
+ (UIColor *)doraemon_colorWithHexString:(NSString *)hexString;

+ (UIColor *)doraemon_colorWithString:(NSString *)hexString;

+ (UIColor *)doraemon_black_1;//#333333
+ (UIColor *)doraemon_black_2;//#666666
+ (UIColor *)doraemon_black_3;//#999999

+ (UIColor *)doraemon_blue;//#337CC4

+ (UIColor *)doraemon_line;//[UIColor doraemon_colorWithHex:0x000000 andAlpha:0.1];

+ (UIColor *)doraemon_randomColor;

+ (UIColor *)doraemon_bg; //#F4F5F6

+ (UIColor *)doraemon_orange; //#FF8903

@end
