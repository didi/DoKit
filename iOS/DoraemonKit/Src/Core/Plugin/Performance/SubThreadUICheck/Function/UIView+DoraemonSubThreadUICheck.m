//
//  UIView+Doraemon.m
//  DoraemonKit
//
//  Created by yixiang on 2018/9/13.
//

#import "UIView+Doraemon.h"
#import "NSObject+Doraemon.h"
#import "DoraemonCacheManager.h"
#import "DoraemonSubThreadUICheckManager.h"
#import "DoraemonUtil.h"
#import "DoraemonHealthManager.h"
#import "DoraemonBacktraceLogger.h"

@implementation UIView (DoraemonSubThreadUICheck)

+ (void)load{
    if ([NSStringFromClass([self class]) isEqualToString:@"UIView"]) {
        [[self  class] doraemon_swizzleInstanceMethodWithOriginSel:@selector(setNeedsLayout) swizzledSel:@selector(doraemon_setNeedsLayout)];
        [[self class] doraemon_swizzleInstanceMethodWithOriginSel:@selector(setNeedsDisplay) swizzledSel:@selector(doraemon_setNeedsDisplay)];
        [[self class] doraemon_swizzleInstanceMethodWithOriginSel:@selector(setNeedsDisplayInRect:) swizzledSel:@selector(doraemon_setNeedsDisplayInRect:)];
    }
}

- (void)doraemon_setNeedsLayout{
    [self doraemon_setNeedsLayout];
    [self uiCheck];
}

- (void)doraemon_setNeedsDisplay{
    [self doraemon_setNeedsDisplay];
    [self uiCheck];
}

- (void)doraemon_setNeedsDisplayInRect:(CGRect)rect{
    [self doraemon_setNeedsDisplayInRect:rect];
    [self uiCheck];
}

- (void)uiCheck{
    if(![NSThread isMainThread]){
        if ([[DoraemonCacheManager sharedInstance] subThreadUICheckSwitch]) {
            NSString *report = [DoraemonBacktraceLogger doraemon_backtraceOfCurrentThread];
            NSDictionary *dic = @{
                                  @"title":[DoraemonUtil dateFormatNow],
                                  @"content":report
                                  };
            [[DoraemonSubThreadUICheckManager sharedInstance].checkArray addObject:dic];
            [[DoraemonHealthManager sharedInstance] addSubThreadUI:dic];
        }
    }
}

@end
