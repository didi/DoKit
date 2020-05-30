//
//  UIApplication+Load.m
//  DoraemonKit-Swift
//
//  Created by objc on 2020/5/29.
//

#import <UIKit/UIKit.h>

@implementation UIApplication (Load)

+ (void)load
{
    #pragma clang diagnostic push
    #pragma clang diagnostic ignored "-Wundeclared-selector"
    Class app = NSClassFromString(@"UIApplication");
    [app performSelector:@selector(applicationLoadHandle)];
    #pragma clang diagnostic pop
}

@end
