//
//  UIColor+Doraemon.m
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "UIColor+Doraemon.h"

CGFloat doraemonColorComponentFrom(NSString *string, NSUInteger start, NSUInteger length) {
    NSString *substring = [string substringWithRange:NSMakeRange(start, length)];
    NSString *fullHex = length == 2 ? substring : [NSString stringWithFormat: @"%@%@", substring, substring];
    
    unsigned hexComponent;
    [[NSScanner scannerWithString: fullHex] scanHexInt: &hexComponent];
    return hexComponent / 255.0;
}

@implementation UIColor (DoreamonKit)

- (CGColorSpaceModel)colorSpaceModel {
    return CGColorSpaceGetModel(CGColorGetColorSpace(self.CGColor));
}

- (BOOL)canProvideRGBComponents {
    switch (self.colorSpaceModel) {
        case kCGColorSpaceModelRGB:
        case kCGColorSpaceModelMonochrome:
            return YES;
        default:
            return NO;
    }
}

- (CGFloat)red {
    NSAssert(self.canProvideRGBComponents, @"Must be an RGB color to use -red");
    const CGFloat *c = CGColorGetComponents(self.CGColor);
    return c[0];
}

- (CGFloat)green {
    NSAssert(self.canProvideRGBComponents, @"Must be an RGB color to use -green");
    const CGFloat *c = CGColorGetComponents(self.CGColor);
    if (self.colorSpaceModel == kCGColorSpaceModelMonochrome) return c[0];
    return c[1];
}

- (CGFloat)blue {
    NSAssert(self.canProvideRGBComponents, @"Must be an RGB color to use -blue");
    const CGFloat *c = CGColorGetComponents(self.CGColor);
    if (self.colorSpaceModel == kCGColorSpaceModelMonochrome) return c[0];
    return c[2];
}

- (CGFloat)white {
    NSAssert(self.colorSpaceModel == kCGColorSpaceModelMonochrome, @"Must be a Monochrome color to use -white");
    const CGFloat *c = CGColorGetComponents(self.CGColor);
    return c[0];
}

- (CGFloat)alpha {
    return CGColorGetAlpha(self.CGColor);
}

+ (UIColor *)doraemon_colorWithHex:(UInt32)hex{
    return [UIColor doraemon_colorWithHex:hex andAlpha:1];
}
+ (UIColor *)doraemon_colorWithHex:(UInt32)hex andAlpha:(CGFloat)alpha{
    return [UIColor colorWithRed:((hex >> 16) & 0xFF)/255.0
                           green:((hex >> 8) & 0xFF)/255.0
                            blue:(hex & 0xFF)/255.0
                           alpha:alpha];
}

+ (UIColor *)doraemon_colorWithString:(NSString *)hexString{
    return [self doraemon_colorWithHexString:hexString];
}

+ (UIColor *)doraemon_colorWithHexString:(NSString *)hexString {
    CGFloat alpha, red, blue, green;
    
    NSString *colorString = [[hexString stringByReplacingOccurrencesOfString:@"#" withString:@""] uppercaseString];
    switch ([colorString length]) {
        case 3: // #RGB
            alpha = 1.0f;
            red   = doraemonColorComponentFrom(colorString, 0, 1);
            green = doraemonColorComponentFrom(colorString, 1, 1);
            blue  = doraemonColorComponentFrom(colorString, 2, 1);
            break;
            
        case 4: // #ARGB
            alpha = doraemonColorComponentFrom(colorString, 0, 1);
            red   = doraemonColorComponentFrom(colorString, 1, 1);
            green = doraemonColorComponentFrom(colorString, 2, 1);
            blue  = doraemonColorComponentFrom(colorString, 3, 1);
            break;
            
        case 6: // #RRGGBB
            alpha = 1.0f;
            red   = doraemonColorComponentFrom(colorString, 0, 2);
            green = doraemonColorComponentFrom(colorString, 2, 2);
            blue  = doraemonColorComponentFrom(colorString, 4, 2);
            break;
            
        case 8: // #AARRGGBB
            alpha = doraemonColorComponentFrom(colorString, 0, 2);
            red   = doraemonColorComponentFrom(colorString, 2, 2);
            green = doraemonColorComponentFrom(colorString, 4, 2);
            blue  = doraemonColorComponentFrom(colorString, 6, 2);
            break;
            
        default:
            return nil;
    }
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
}

+ (UIColor *)doraemon_black_1{//#333333
    return [UIColor doraemon_colorWithString:@"#333333"];
}

+ (UIColor *)doraemon_black_2{//#666666
    return [UIColor doraemon_colorWithString:@"#666666"];
}

+ (UIColor *)doraemon_black_3{//#999999
    return [UIColor doraemon_colorWithString:@"#999999"];
}

+ (UIColor *)doraemon_blue{//#337CC4
    return [UIColor doraemon_colorWithString:@"#337CC4"];
}

+ (UIColor *)doraemon_line{//[UIColor doraemon_colorWithHex:0x000000 andAlpha:0.1];
    return [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.1];
}

@end
