//
//  DoraemonMCEventCapturer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCEventCapturer.h"
#import "DoraemonMCServer.h"
#import <objc/runtime.h>
#import "DoraemonMCXPathSerializer.h"
#import "DoraemonMCCommandGenerator.h"
#import "DoraemonMCReuseViewDelegateProxy.h"

@implementation UIApplication (DoraemonMCSupport)

+ (void)load {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(sendAction:to:from:forEvent:) swizzledSel:@selector(do_mc_sendAction:to:from:forEvent:)];
        });
}

- (BOOL)do_mc_sendAction:(SEL)action to:(id)target from:(id)sender forEvent:(UIEvent *)event {
    
    if ([DoraemonMCServer isOpen]) {
        
        UIView *senderV = sender;
        if ([sender isKindOfClass:[UIGestureRecognizer class]]) {
            UIGestureRecognizer *ges = sender;
            senderV = [ges view];
            [ges do_mc_handleGestureSend:sender];
        }else if ([senderV isKindOfClass:[UIView class]]) {
            [DoraemonMCCommandGenerator sendMessageWithView:senderV
                                                    gusture:nil
                                                     action:action
                                                  indexPath:nil
                                                messageType:DoraemonMCMessageTypeControl];
        }
    }
    
    return [self do_mc_sendAction:action to:target from:sender forEvent:event];
}

@end



@implementation UIPanGestureRecognizer (DoraemonMCSupport)


+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(translationInView:) swizzledSel:@selector(do_mc_translationInView:)];
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(velocityInView:) swizzledSel:@selector(do_mc_velocityInView:)];
    });
}

- (CGPoint)do_mc_translationInView:(UIView *)view {
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_temp_p)) {
        return [self do_mc_translationInView:view];
    }
    return self.do_mc_temp_p;
}

- (CGPoint)do_mc_velocityInView:(UIView *)view{
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_temp_Vol)) {
        return [self do_mc_translationInView:view];
    }
    return self.do_mc_temp_Vol;
}

@end


@implementation DoraemonMCGestureTargetActionPair

- (instancetype)initWithTarget:(id)target action:(SEL)action sender:(id)sender {
    if (self = [super init]) {
        self.target = target;
        self.action = action;
        self.sender = sender;
    }
    return self;
}

- (BOOL)isEqualToTarget:(id)target andAction:(SEL)action {
    return (self.target == target) && [NSStringFromSelector(self.action) isEqualToString:NSStringFromSelector(action)];
}

- (BOOL)valid {
    return [self.target respondsToSelector:self.action];
}

- (void)doAction {
    if ([NSStringFromSelector(self.action) containsString:@":"]) {
        [self.target performSelector:self.action withObject:self.sender];
    }else {
        [self.target performSelector:self.action];
    }
}

@end


@implementation UIGestureRecognizer (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(initWithTarget:action:) swizzledSel:@selector(do_mc_initWithTarget:action:)];
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(removeTarget:action:) swizzledSel:@selector(do_mc_removeTarget:action:)];
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(addTarget:action:) swizzledSel:@selector(do_mc_addTarget:action:)];

    });
}

- (instancetype)do_mc_initWithTarget:(id)target action:(SEL)action {
    [self do_mc_initWithTarget:self action:@selector(do_mc_action:)];
    [self addTarget:target action:action];
    return self;
}

- (void)do_mc_addTarget:(id)target action:(SEL)action {
    __block BOOL existed = NO;
    [self.do_mc_targetActionPairs enumerateObjectsUsingBlock:^(DoraemonMCGestureTargetActionPair * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (obj.target == target &&
            [NSStringFromSelector(obj.action) isEqualToString:NSStringFromSelector(action)]) {
            existed = YES;
            *stop = YES;
        }
    }];
    if (!existed) {
        [self.do_mc_targetActionPairs addObject:[[DoraemonMCGestureTargetActionPair alloc] initWithTarget:target action:action sender:self]];
    }
    [self do_mc_addTarget:target action:action];
}

- (void)do_mc_handleGestureSend:(id)sender {
    [DoraemonMCCommandGenerator sendMessageWithView:self.view
                                            gusture:self
                                             action:nil
                                          indexPath:nil
                                        messageType:DoraemonMCMessageTypeGuesture];
}

- (void)do_mc_action:(id)sender {
    if ([DoraemonMCServer isOpen]) {
        [self do_mc_handleGestureSend:sender];
    }

}

- (void)do_mc_manual_doAction {
    [self.do_mc_targetActionPairs enumerateObjectsUsingBlock:^(DoraemonMCGestureTargetActionPair * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if (obj.valid) {
            [obj doAction];
        }
    }];
}

- (void)do_mc_removeTarget:(id)target action:(SEL)action {
    
    [self.do_mc_targetActionPairs enumerateObjectsUsingBlock:^(DoraemonMCGestureTargetActionPair * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isEqualToTarget:target andAction:action]) {
            [self.do_mc_targetActionPairs removeObject:obj];
            *stop = YES;
        }
    }];
    
    [self do_mc_removeTarget:target action:action];

}



#pragma mark - Associated Object

- (NSMutableArray<DoraemonMCGestureTargetActionPair *> *)do_mc_targetActionPairs {
    NSMutableArray *arrM =  objc_getAssociatedObject(self, _cmd) ;
    if (!arrM) {
        arrM = [NSMutableArray array];
        objc_setAssociatedObject(self, @selector(do_mc_targetActionPairs), arrM, OBJC_ASSOCIATION_RETAIN);
    }
    return arrM;
}


- (void)setDo_mc_temp_p:(CGPoint)do_mc_temp_p {
    objc_setAssociatedObject(self, @selector(do_mc_temp_p), [NSValue valueWithCGPoint:do_mc_temp_p], OBJC_ASSOCIATION_RETAIN);
}

- (CGPoint)do_mc_temp_p {
    id value = objc_getAssociatedObject(self, _cmd);
    return [value CGPointValue];
}

- (void)setDo_mc_temp_Vol:(CGPoint)temp_Vol
{
    objc_setAssociatedObject(self, @selector(do_mc_temp_Vol), [NSValue valueWithCGPoint:temp_Vol], OBJC_ASSOCIATION_RETAIN);
}

- (CGPoint)do_mc_temp_Vol {
    id value = objc_getAssociatedObject(self, _cmd);
    return [value CGPointValue];
}


- (void)setValue:(id)value forUndefinedKey:(NSString *)key {}
- (id)valueForUndefinedKey:(NSString *)key {return nil;}

@end


@implementation UIControl (support)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(initWithFrame:) swizzledSel:@selector(do_mc_initWithFrame:)];
    });
}

- (instancetype)do_mc_initWithFrame:(CGRect)frame {
    id instance = [self do_mc_initWithFrame:frame];
    [instance addTarget:self action:@selector(do_mc_UIControlEventEditingChangedHandle) forControlEvents:UIControlEventEditingChanged];
    [instance addTarget:self action:@selector(do_mc_UIControlEventEditingDidEndHandle) forControlEvents:UIControlEventEditingDidEnd];
    return instance;
}

- (void)do_mc_UIControlEventEditingChangedHandle {
    [self do_mc_sendPayload:^NSDictionary *(NSDictionary *input) {
        if ([self respondsToSelector:@selector(text)]) {
            NSMutableDictionary *inputM = input.mutableCopy;
            inputM[@"text"] = [self valueForKey:@"text"];
            return inputM.copy;
        }
        return input;
    }];
}

- (void)do_mc_sendPayload:(NSDictionary*(^)(NSDictionary*))payload {
    if (![DoraemonMCServer isOpen]) {
        return;
    }
    if (![self isKindOfClass:[UITextField class]] &&
        ![self isKindOfClass:[UITextView class]]) {
        return;
        
    }
    [DoraemonMCCommandGenerator sendMessageWithView:self
                                            gusture:nil
                                             action:nil
                                          indexPath:nil
                                        messageType:DoraemonMCMessageTypeTextInput];
}

- (void)do_mc_UIControlEventEditingDidEndHandle {
    [self do_mc_sendPayload:nil];
}

@end

@interface UITableView (DoraemonMCSupport)

@end

@implementation UITableView (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(setDelegate:) swizzledSel:@selector(do_mc_setDelegate:)];
    });
}

- (void)do_mc_setDelegate:(id<UITableViewDelegate>)delegate {

    DoraemonMCReuseViewDelegateProxy *delegateProxy = [DoraemonMCReuseViewDelegateProxy proxyWithTarget:delegate];
    objc_setAssociatedObject(self, _cmd, delegateProxy, OBJC_ASSOCIATION_RETAIN);
    [self do_mc_setDelegate:delegateProxy];
}

@end


@interface UICollectionView (DoraemonMCSupport) <UICollectionViewDelegate>

@end

@implementation UICollectionView (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(setDelegate:) swizzledSel:@selector(do_mc_setDelegate:)];
    });
}

- (void)do_mc_setDelegate:(id<UICollectionViewDelegate>)delegate {
    DoraemonMCReuseViewDelegateProxy *delegateProxy = [DoraemonMCReuseViewDelegateProxy proxyWithTarget:delegate];
    objc_setAssociatedObject(self, _cmd, delegateProxy, OBJC_ASSOCIATION_RETAIN);
    [self do_mc_setDelegate:delegateProxy];
}

@end


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

@end


