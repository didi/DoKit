//
//  DoraemonMCGustureSerializer.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCGustureSerializer.h"
#import <objc/runtime.h>
#import "DoraemonMCEventCapturer.h"

@implementation DoraemonMCGustureSerializer

+ (NSDictionary *)dictFromGusture:(UIGestureRecognizer *)gusture {
    if (![gusture isKindOfClass:[UIGestureRecognizer class]] ) {
        return nil;
    }
    CGPoint p = CGPointZero;
    CGPoint velocityP = CGPointZero;
    CGPoint centerP  = CGPointZero;
    UIGestureRecognizerState state = gusture.state;
    CGPoint locationP = [gusture locationInView:gusture.view];
    if ([gusture isKindOfClass:[UIPanGestureRecognizer class]]) {
         UIPanGestureRecognizer *panGes = (UIPanGestureRecognizer *)gusture;
          p = [panGes translationInView:gusture.view];
        velocityP = [panGes velocityInView:gusture.view];
        centerP = CGPointMake(gusture.view.center.x, gusture.view.center.y);
    }
    
    NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
    unsigned int count;
    objc_property_t *propertyList = class_copyPropertyList([gusture class], &count);
    
    
    for (int i = 0; i < count; i++) {
        objc_property_t property = propertyList[i];
        const char *cName = property_getName(property);
        NSString *name = [NSString stringWithUTF8String:cName];
        NSObject *value = [gusture valueForKey:name];
        if ([value isKindOfClass:[NSString class]] || [value isKindOfClass:[NSNumber class]]) {
            dictM[name] = value?:@"null";
        }
    }
    free(propertyList);
 
    UIControlState stateCtl = -1;
    if ([gusture.view isKindOfClass:[UIControl class]]) {
        UIControl *ctl = (UIControl *)gusture.view;
        stateCtl = ctl.state;
    }
    
    NSInteger gesIndex =  [gusture.view.gestureRecognizers indexOfObject:gusture];
    
    return @{
        @"gesIndex":@(gesIndex),
        @"ctlState":@(stateCtl),
        @"state":@(state),
        @"lp" : @{
                @"x":@(locationP.x),
                @"y" : @(locationP.y)
        },
        @"data" : @{
                @"offsetX": @(p.x/[UIScreen mainScreen].bounds.size.width),
                @"offsetY": @(p.y/[UIScreen mainScreen].bounds.size.height),
                @"velocityX":  @(velocityP.x/[UIScreen mainScreen].bounds.size.width),
                @"velocityY":  @(velocityP.y/[UIScreen mainScreen].bounds.size.height),
                @"velocityX":  @(velocityP.x),
                @"velocityY":  @(velocityP.y),
                @"pointX":@(centerP.x),
                @"pointY":@(centerP.y),
        },
        @"pps" : dictM.copy
    };
}

+ (void)syncInfoToGusture:(UIGestureRecognizer *)gusture withDict:(NSDictionary *)eventInfo {
    if (![gusture isKindOfClass:[UIGestureRecognizer class]] ||
        ![eventInfo isKindOfClass:[NSDictionary class]]) {
        return;
    }

    UIPanGestureRecognizer *pan = nil;
    NSDictionary *data = eventInfo[@"data"];
    if (data && [gusture isKindOfClass:[UIPanGestureRecognizer class]]) {
         pan = (UIPanGestureRecognizer *)gusture;
        CGPoint translation = CGPointMake([data[@"offsetX"] doubleValue] * [UIScreen mainScreen].bounds.size.width, [data[@"offsetY"] doubleValue] * [UIScreen mainScreen].bounds.size.height);
        CGPoint volP = CGPointMake([data[@"velocityX"] doubleValue] * [UIScreen mainScreen].bounds.size.width, [data[@"velocityY"] doubleValue] * [UIScreen mainScreen].bounds.size.height);
        CGPoint point = CGPointMake([data[@"pointX"] floatValue] , [data[@"pointY"] floatValue] );
        pan.view.center = CGPointMake(point.x, point.y);
        if (pan.state == UIGestureRecognizerStateEnded || pan.state == UIGestureRecognizerStateCancelled) {
            pan.do_mc_temp_p = CGPointZero;
        }else {
            pan.do_mc_temp_p = translation;
            pan.do_mc_temp_Vol = volP;
        }
    }
    
    CGPoint locationP = CGPointMake([eventInfo[@"lp"][@"x"] doubleValue], [eventInfo[@"lp"][@"y"] doubleValue]);
    gusture.do_mc_temp_location = locationP;
    gusture.state = [eventInfo[@"state"] intValue];

    NSDictionary *pps = eventInfo[@"pps"];
    [pps enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[NSString class]] && [obj isEqualToString:@"null"]) {
            [gusture setValue:nil forKey:key];
        }else {
            [gusture setValue:obj forKey:key];
        }
    }];
    
    if ([gusture.view isKindOfClass:[UIControl class]]) {
        UIControl *ctl = (UIControl *)gusture.view;
        UIControlState ctlState = [eventInfo[@"ctlState"] integerValue];
        
        [ctl resignFirstResponder];
        if (ctlState & UIControlStateHighlighted) {
            [ctl setHighlighted:YES];
        }else {
            [ctl setHighlighted:NO];
        }
        if (ctlState & UIControlStateSelected) {
            [ctl setSelected:YES];
        }else {
            [ctl setHighlighted:NO];
        }
        if (ctlState & UIControlStateDisabled) {
            [ctl setEnabled:NO];
        }else {
            [ctl setHighlighted:YES];
        }
    }

}

@end
