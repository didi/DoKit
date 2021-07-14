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

@implementation UIApplication (DoraemonMCSupport)

+ (void)load {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(sendAction:to:from:forEvent:) swizzledSel:@selector(do_mc_sendAction:to:from:forEvent:)];
        });
}

- (BOOL)do_mc_sendAction:(SEL)action to:(id)target from:(id)sender forEvent:(UIEvent *)event {
    
    if ([DoraemonMCServer isServer]) {
        
        UIView *senderV = sender;
        if ([sender isKindOfClass:[UIGestureRecognizer class]]) {
            UIGestureRecognizer *ges = sender;
            senderV = [ges view];
            [ges do_mc_handleGestureSend:sender];
        }else if ([senderV isKindOfClass:[UIView class]]) {
            UIWindow *currentWindow = nil;
            if ([senderV isKindOfClass:[UIWindow class]]) {
                currentWindow = (UIWindow *)senderV;
            }else {
                currentWindow = senderV.window;
            }
            NSInteger windowIndex = [[[UIApplication sharedApplication] windows] indexOfObject:currentWindow];
            if (windowIndex == NSNotFound) {
                windowIndex = -1 ;
            }

            NSString *str = @"";
            UIView *currentV = senderV;
            while (currentV && [currentV isKindOfClass:[UIView class]]) {
                if (currentV.superview) {
                    str = [[NSString stringWithFormat:@"/%zd", [currentV.superview.subviews indexOfObject:currentV]] stringByAppendingString:str];
                }
                currentV = currentV.superview;
            }
            
            NSDictionary *map = @{
                @"type" : @"control",
                @"xPath": str,
                @"windowIndex":@(windowIndex),
                @"firstResponder":@(senderV.isFirstResponder),
                @"data" : @{
                        @"action": NSStringFromSelector(action)?:@"",
                }
            };
            NSString *payload = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:map options:NSJSONWritingFragmentsAllowed error:NULL] encoding:NSUTF8StringEncoding];
            [DoraemonMCServer sendMessage:payload];
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
    UIWindow *currentWindow =  nil;
    if ([self.view isKindOfClass:[UIWindow class]]) {
        currentWindow = (UIWindow *)self.view;
    }else {
        currentWindow = self.view.window;
    }
    NSInteger windowIndex = [[[UIApplication sharedApplication] windows] indexOfObject:currentWindow];
    if (windowIndex == NSNotFound) {
        windowIndex = -1 ;
    }
    NSInteger gesIndex =  [self.view.gestureRecognizers indexOfObject:self];
    NSString *str = @"";
    UIView *currentV = self.view;
    while (currentV) {
        if (currentV.superview) {
            str = [[NSString stringWithFormat:@"/%zd", [currentV.superview.subviews indexOfObject:currentV]] stringByAppendingString:str];
        }
        currentV = currentV.superview;
    }

    CGPoint p = CGPointZero;
    CGPoint velocityP = CGPointZero;
    UIGestureRecognizerState state = 0;
    if ([self isKindOfClass:[UIPanGestureRecognizer class]]) {
         UIPanGestureRecognizer *panGes = (UIPanGestureRecognizer *)self;
          p = [panGes translationInView:self.view];
        state = panGes.state;
        velocityP = [panGes velocityInView:self.view];
    }
    
    NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
    unsigned int count;
    objc_property_t *propertyList = class_copyPropertyList([self class], &count);
    
    
    for (int i = 0; i < count; i++) {
        objc_property_t property = propertyList[i];
        const char *cName = property_getName(property);
        NSString *name = [NSString stringWithUTF8String:cName];
        if ([self respondsToSelector:NSSelectorFromString(name)]) {
            NSObject *value = [self valueForKey:name];
            if ([value isKindOfClass:[NSString class]] || [value isKindOfClass:[NSNumber class]]) {
                dictM[name] = value?:@"null";
            }
        }
    }
    free(propertyList);
 
    UIControlState stateCtl = -1;
    if ([self.view isKindOfClass:[UIControl class]]) {
        UIControl *ctl = (UIControl *)self.view;
        stateCtl = ctl.state;
    }
    
    NSDictionary *map = @{
        @"type" : @"gesture",
        @"xPath": str,
        @"gesIndex":@(gesIndex),
        @"windowIndex":@(windowIndex),
        @"ctlState": @(stateCtl),
        @"firstResponder":@(self.view.isFirstResponder),
        @"data" : @{
                @"offsetX": @(p.x/[UIScreen mainScreen].bounds.size.width),
                @"offsetY": @(p.y/[UIScreen mainScreen].bounds.size.height),
                @"state":@(state),
                @"velocityX":  @(velocityP.x/[UIScreen mainScreen].bounds.size.width),
                @"velocityY":  @(velocityP.y/[UIScreen mainScreen].bounds.size.height),
        },
        @"pps" : dictM.copy
    };
    NSString *payload = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:map options:NSJSONWritingFragmentsAllowed error:NULL] encoding:NSUTF8StringEncoding];
    [DoraemonMCServer sendMessage:payload];
}

- (void)do_mc_action:(id)sender {
    if ([DoraemonMCServer isServer]) {
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
    if (![DoraemonMCServer isServer]) {
        return;
    }
    if (![self isKindOfClass:[UITextField class]] &&
        ![self isKindOfClass:[UITextView class]]) {
        return;
        
    }
    
    UIWindow *currentWindow = self.window;
    NSInteger windowIndex = [[[UIApplication sharedApplication] windows] indexOfObject:currentWindow];
    if (windowIndex == NSNotFound) {
        windowIndex = -1 ;
    }

    NSString *str = @"";
    UIView *currentV = self;
    while (currentV) {
        if (currentV.superview) {
            str = [[NSString stringWithFormat:@"/%zd", [currentV.superview.subviews indexOfObject:currentV]] stringByAppendingString:str];
        }
        currentV = currentV.superview;
    }

    NSDictionary *map = @{
        @"type" : @"TextFiled",
        @"xPath": str,
        @"windowIndex":@(windowIndex),
        @"ctlState": @(self.state),
        @"firstResponder":@(self.isFirstResponder),
    };
    if (payload) {
        map = payload(map);
    }
    NSString *payloadStr = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:map options:NSJSONWritingFragmentsAllowed error:NULL] encoding:NSUTF8StringEncoding];
    [DoraemonMCServer sendMessage:payloadStr];
}

- (void)do_mc_UIControlEventEditingDidEndHandle {
    [self do_mc_sendPayload:nil];
}

@end

@interface UITableView (DoraemonMCSupport) <UITableViewDelegate>

@property (nonatomic , weak) id<UITableViewDelegate> do_mc_temp_delegate;

@end

@implementation UITableView (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(setDelegate:) swizzledSel:@selector(do_mc_setDelegate:)];
    });
}


- (void)do_mc_setDelegate:(id<UITableViewDelegate>)delegate {
    self.do_mc_temp_delegate = delegate;
    [self do_mc_setDelegate:self];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([DoraemonMCServer isServer]) {
        UIWindow *currentWindow = tableView.window;

        NSInteger windowIndex = [[[UIApplication sharedApplication] windows] indexOfObject:currentWindow];
        if (windowIndex == NSNotFound) {
            windowIndex = -1 ;
        }

        NSString *str = @"";
        UIView *currentV = tableView;
        while (currentV) {
            if (currentV.superview) {
                str = [[NSString stringWithFormat:@"/%zd", [currentV.superview.subviews indexOfObject:currentV]] stringByAppendingString:str];
            }
            currentV = currentV.superview;
        }
        
        NSDictionary *map = @{
            @"type" : @"tableView",
            @"windowIndex":@(windowIndex),
            @"xPath": str,
            @"data" : @{
                    @"section": @(indexPath.section),
                    @"row": @(indexPath.row)
            }
        };
        NSString *payload = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:map options:NSJSONWritingFragmentsAllowed error:NULL] encoding:NSUTF8StringEncoding];
        [DoraemonMCServer sendMessage:payload];
    }
    if (self.do_mc_temp_delegate) {
        [self.do_mc_temp_delegate tableView:tableView didSelectRowAtIndexPath:indexPath];
    }
}

- (void)setDo_mc_temp_delegate:(id<UITableViewDelegate>)do_mc_temp_delegate {
    objc_setAssociatedObject(self, @selector(do_mc_temp_delegate), do_mc_temp_delegate, OBJC_ASSOCIATION_ASSIGN);

}

- (id<UITableViewDelegate>)do_mc_temp_delegate {
    return objc_getAssociatedObject(self, _cmd);
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


