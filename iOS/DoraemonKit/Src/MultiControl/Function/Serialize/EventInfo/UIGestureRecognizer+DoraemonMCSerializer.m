//
//  UIGestureRecognizer+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import <objc/runtime.h>
#import "DoraemonMCEventCapturer.h"
#import "NSObject+DoraemonMCSupport.h"

NSString const *kUIGestureRecognizerDoraemonMCSerializerWrapperKey = @"ges_wrapper";

NSString const *kUIGestureRecognizerDoraemonMCSerializerIndexKey = @"gesIndex";

static NSString const *kUIGestureRecognizerStateKey = @"gesState";

static NSString const *kUIGestureRecognizerLocationKey = @"gesLocation";

static NSString const *kUIGestureRecognizerLocationXKey = @"x";

static NSString const *kUIGestureRecognizerLocationYKey = @"y";

static NSString const *kUIGestureRecognizerPropsKey = @"gesProps";

@implementation UIGestureRecognizer (DoraemonMCSerializer)

/// 当前手势对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary {
        
    NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
    [self do_mc_serialize_setupDictionary:dictM];
    return @{
        kUIGestureRecognizerDoraemonMCSerializerWrapperKey : dictM.copy
    };
   
}

- (void)do_mc_serialize_setupDictionary:(NSMutableDictionary *)dictM {
    
    CGPoint locationP = [self locationInView:self.view];
    
    NSInteger gesIndex =  [self.view.gestureRecognizers indexOfObject:self];

    NSMutableDictionary *propsM = [NSMutableDictionary dictionary];
    unsigned int count;
    objc_property_t *propertyList = class_copyPropertyList([self class], &count);
    
    for (int i = 0; i < count; i++) {
        objc_property_t property = propertyList[i];
        const char *cName = property_getName(property);
        NSString *name = [NSString stringWithUTF8String:cName];
        NSObject *value = [self valueForKey:name];
        if ([value isKindOfClass:[NSString class]] || [value isKindOfClass:[NSNumber class]]) {
            propsM[name] = value?:@"null";
        }
    }
    free(propertyList);
    
    [dictM addEntriesFromDictionary:@{
            kUIGestureRecognizerPropsKey : propsM.copy,
            kUIGestureRecognizerDoraemonMCSerializerIndexKey : @(gesIndex),
            kUIGestureRecognizerStateKey : @(self.state),
            kUIGestureRecognizerLocationKey : @{
                    kUIGestureRecognizerLocationXKey : @(locationP.x),
                    kUIGestureRecognizerLocationYKey : @(locationP.y)
            }
    }];
}

/// 将字典中的值同步到当前手势对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSDictionary *map = dictionary[kUIGestureRecognizerDoraemonMCSerializerWrapperKey];
    if (![map isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSDictionary *locationMap = map[kUIGestureRecognizerLocationKey];
    if ([locationMap isKindOfClass:[NSDictionary class]]) {
        self.do_mc_location_at_host = CGPointMake([locationMap[kUIGestureRecognizerLocationXKey] doubleValue],
                                                  [locationMap[kUIGestureRecognizerLocationYKey] doubleValue]);
    }

    NSNumber *stateNumber = map[kUIGestureRecognizerStateKey];
    self.state = [stateNumber intValue];
    
    NSDictionary *pps = map[kUIGestureRecognizerPropsKey];
    if ([pps isKindOfClass:[NSDictionary class]]) {
        [pps enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
            if ([obj isKindOfClass:[NSString class]] && [obj isEqualToString:@"null"]) {
                [self setValue:nil forKey:key];
            }else {
                [self setValue:obj forKey:key];
            }
        }];
    }

    if ([stateNumber intValue] == UIGestureRecognizerStateCancelled ||
        [stateNumber intValue] == UIGestureRecognizerStateEnded) {
        dispatch_async(dispatch_get_main_queue(), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self do_mc_clear_all_value_at_host];
            });
        });
    }
}

#pragma mark - hook

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(locationInView:) swizzledSel:@selector(do_mc_base_gesture_locationInView:)];
    });
}

- (CGPoint)do_mc_base_gesture_locationInView:(UIView *)view{
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_location_at_host)) {
        return [self do_mc_base_gesture_locationInView:view];
    }
    return self.do_mc_location_at_host;
}

- (void)setDo_mc_location_at_host:(CGPoint)do_mc_location_at_host {
    objc_setAssociatedObject(self, @selector(do_mc_location_at_host), [NSValue valueWithCGPoint:do_mc_location_at_host], OBJC_ASSOCIATION_RETAIN);
}

- (CGPoint)do_mc_location_at_host {
    return [self do_mc_point_value_forkey:_cmd];
}


- (void)do_mc_clear_all_value_at_host {
    self.do_mc_location_at_host = CGPointZero;
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key {}
- (id)valueForUndefinedKey:(NSString *)key {return nil;}



@end
