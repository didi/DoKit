//
//  DoraemonMCEventCapturer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCEventCapturer.h"
#import "DoraemonMCServer.h"
#import "DoraemonMCXPathSerializer.h"
#import "DoraemonMCCommandGenerator.h"
#import "DoraemonMCReuseViewDelegateProxy.h"
#import "NSObject+DoraemonMCSupport.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import <objc/runtime.h>

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

@implementation UIGestureRecognizer (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(initWithTarget:action:) swizzledSel:@selector(do_mc_initWithTarget:action:)];
    });
}

- (instancetype)do_mc_initWithTarget:(id)target action:(SEL)action {
    [self do_mc_initWithTarget:self action:@selector(do_mc_action:)];
    [self addTarget:target action:action];
    return self;
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

@end

@implementation UITextField (support)


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
    if (![self isKindOfClass:[UITextField class]]) {
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

@implementation UITextView (support)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(initWithFrame:) swizzledSel:@selector(do_mc_initWithFrame:)];
    });
}

- (instancetype)do_mc_initWithFrame:(CGRect)frame {
    id instance = [self do_mc_initWithFrame:frame];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(do_mc_sendTextViewPayload:) name:UITextViewTextDidChangeNotification object:nil];

    return instance;
}

- (void)do_mc_sendTextViewPayload:(NSDictionary*(^)(NSDictionary*))payload {
    if (![DoraemonMCServer isOpen]) {
        return;
    }
    if (![self isKindOfClass:[UITextView class]]) {
        return;
        
    }
    [DoraemonMCCommandGenerator sendMessageWithView:self
                                            gusture:nil
                                             action:nil
                                          indexPath:nil
                                        messageType:DoraemonMCMessageTypeTextInput];
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

@interface UITabBarController (DoraemonMCSupport)

@end

@implementation UITabBarController (DoraemonMCSupport)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(setSelectedViewController:) swizzledSel:@selector(do_mc_setSelectedViewController:)];
    });
}


- (void)do_mc_setSelectedViewController:(__kindof UIViewController *)selectedViewController {
    [self do_mc_setSelectedViewController:selectedViewController];
    if (![DoraemonMCServer isOpen]) {
        return;
    }
    
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:[self.viewControllers indexOfObject:selectedViewController] inSection:0];
    [DoraemonMCCommandGenerator sendMessageWithView:self.view
                                            gusture:nil
                                             action:nil
                                          indexPath:indexPath
                                        messageType:DoraemonMCMessageTypeTarbarSelected];
}


@end
