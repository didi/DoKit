//
//  NSObject+DoraemonMCSupport.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "NSObject+DoraemonMCSupport.h"
#import <objc/runtime.h>

@implementation NSObject (DoraemonMCSupport)

+ (void)do_mc_swizzleClassMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel {
    Class cls = object_getClass(self);
    
    Method originAddObserverMethod = class_getClassMethod(cls, oriSel);
    Method swizzledAddObserverMethod = class_getClassMethod(cls, swiSel);
    
    [self do_mc_swizzleMethodWithOriginSel:oriSel oriMethod:originAddObserverMethod swizzledSel:swiSel swizzledMethod:swizzledAddObserverMethod class:cls];
}

+ (void)do_mc_swizzleInstanceMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel {
    Method originAddObserverMethod = class_getInstanceMethod(self, oriSel);
    Method swizzledAddObserverMethod = class_getInstanceMethod(self, swiSel);
    
    [self do_mc_swizzleMethodWithOriginSel:oriSel oriMethod:originAddObserverMethod swizzledSel:swiSel swizzledMethod:swizzledAddObserverMethod class:self];
}

+ (void)do_mc_swizzleInstanceMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel cls:(Class)cls {
    Method originAddObserverMethod = class_getInstanceMethod(self, oriSel);
    Method swizzledAddObserverMethod = class_getInstanceMethod(cls, swiSel);
    
    
    BOOL didAddMethod = class_addMethod(cls, oriSel, method_getImplementation(swizzledAddObserverMethod), method_getTypeEncoding(swizzledAddObserverMethod));
    
    if (didAddMethod) {
        class_replaceMethod(cls, swiSel, method_getImplementation(originAddObserverMethod), method_getTypeEncoding(originAddObserverMethod));
    } else {
        method_exchangeImplementations(originAddObserverMethod, swizzledAddObserverMethod);
    }
}

+ (void)do_mc_swizzleMethodWithOriginSel:(SEL)oriSel
                         oriMethod:(Method)oriMethod
                       swizzledSel:(SEL)swizzledSel
                    swizzledMethod:(Method)swizzledMethod
                             class:(Class)cls {
    BOOL didAddMethod = class_addMethod(cls, oriSel, method_getImplementation(swizzledMethod), method_getTypeEncoding(swizzledMethod));
    
    if (didAddMethod) {
        class_replaceMethod(cls, swizzledSel, method_getImplementation(oriMethod), method_getTypeEncoding(oriMethod));
    } else {
        method_exchangeImplementations(oriMethod, swizzledMethod);
    }
}

- (void)setValue:(id)value forUndefinedKey:(NSString *)key{}

- (CGPoint)do_mc_point_value_forkey:(const void * _Nonnull)key {
   NSValue *value = objc_getAssociatedObject(self, key);
    if (![value isKindOfClass:[NSValue class]]) {
        return CGPointZero;
    }
    return [value CGPointValue];
}

- (CGRect)do_mc_rect_value_forkey:(const void * _Nonnull)key {
   NSValue *value = objc_getAssociatedObject(self, key);
    if (![value isKindOfClass:[NSValue class]]) {
        return CGRectZero;
    }
    return [value CGRectValue];;
}


@end
