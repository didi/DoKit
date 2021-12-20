//
//  UIColor+DoraemonHierarchy.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "UIColor+DoraemonHierarchy.h"
#import "UIColor+Doraemon.h"

@implementation UIColor (DoraemonHierarchy)

- (NSString *)doraemon_HexString {
    int r = [self red] * 255.0;
    int g = [self green] * 255.0;
    int b = [self blue] * 255.0;
    return [NSString stringWithFormat:@"#%02X%02X%02X",r, g, b];
}

- (NSString *)doraemon_description {
    if ([self isEqual:[UIColor clearColor]]) {
        return @"Clear Color";
    }
    
    NSString *color = [self doraemon_systemColorName];
    
    if (color) {
        color = [color stringByAppendingFormat:@" (%@)",[self doraemon_RGBADescrption]];
    } else {
        color = [self doraemon_RGBADescrption];
    }
    
    return color;
}

- (NSString *)doraemon_systemColorName {
    return [self valueForKeyPath:@"systemColorName"];
}

#pragma mark - Primary
- (NSString *)doraemon_RGBADescrption {
    int r = [self red] * 255.0;
    int g = [self green] * 255.0;
    int b = [self blue] * 255.0;
    int a = [self alpha] * 255.0;
    NSString *desc = [NSString stringWithFormat:@"#%02X%02X%02X",r,g,b];
    if (a < 255) {
        desc = [desc stringByAppendingFormat:@", Alpha: %0.2f", [self alpha]];
    }
    return desc;
}

@end
