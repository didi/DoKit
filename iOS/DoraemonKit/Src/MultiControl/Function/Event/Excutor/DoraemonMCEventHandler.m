//
//  DoraemonMCEventHandler.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCEventHandler.h"
#import <objc/runtime.h>
#import <DoraemonKit/DoraemonMCServer.h>
#import "DoraemonMCEventCapturer.h"
#import "DoraemonMCXPathSerializer.h"
#import "DoraemonMCGustureSerializer.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import "UIResponder+DoraemonMCSerializer.h"
#import "DoraemonMCXPathSerializer.h"

#define DoraemonMCEventHandler_call_super_handle_event if (![super handleEvent:eventInfo]) {\
        return NO;\
    }

@implementation DoraemonMCEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
    self.messageInfo = eventInfo;
    self.targetView = [self fetchTargetView];
    // 页面类名校验
    if (eventInfo.currentVCClassName.length > 0 &&
        ![[DoraemonMCXPathSerializer ownerVCWithView:self.targetView] isKindOfClass:NSClassFromString(eventInfo.currentVCClassName)]) {
        return NO;
    }

    if (self.messageInfo.isFirstResponder && self.targetView.isFirstResponder == NO) {
        [self.targetView becomeFirstResponder];
    }else if (self.messageInfo.isFirstResponder == NO && self.targetView.isFirstResponder) {
        [self.targetView resignFirstResponder];
    }
    return YES;
}

- (UIView *)fetchTargetView {
    return [DoraemonMCXPathSerializer viewFromXpath:self.messageInfo.xPath];
}
@end


@implementation DoraemonMCGestureRecognizerEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
   
    DoraemonMCEventHandler_call_super_handle_event
    
    UIView *targetView = self.targetView;
    
    NSDictionary *data = self.messageInfo.eventInfo;
    NSInteger gesIndex = [data[kUIGestureRecognizerDoraemonMCSerializerWrapperKey][kUIGestureRecognizerDoraemonMCSerializerIndexKey] intValue];
    
    if (targetView.gestureRecognizers.count <= gesIndex) {
        NSLog(@"gestureRecognizer has not found %@",eventInfo);
        return NO;
    }
    
    UIGestureRecognizer *ges = targetView.gestureRecognizers[gesIndex];
    
    [DoraemonMCGustureSerializer syncInfoToGusture:ges withDict:data];

    [ges do_mc_manual_doAction];
    return YES;
}

@end

@implementation DoraemonMCControlEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
    DoraemonMCEventHandler_call_super_handle_event
    
    UIView *rootView = self.targetView;
    NSDictionary *data = self.messageInfo.eventInfo;

    SEL action = NSSelectorFromString(data[@"action"]);
    if ([rootView isKindOfClass:[UIControl class]]) {
        UIControl *ctl = (UIControl *)rootView;
        [[ctl allTargets] enumerateObjectsUsingBlock:^(id  _Nonnull obj, BOOL * _Nonnull stop) {
            if ([obj respondsToSelector:action]) {
                if ([data[@"action"] containsString:@":"]) {
                    [obj performSelector:action withObject:rootView];
                }else {
                    [obj performSelector:action];
                }
            }
        }];
    }
    return YES;
}

@end

@implementation DoraemonMCReuseCellEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
    DoraemonMCEventHandler_call_super_handle_event
    
    NSDictionary *data = self.messageInfo.eventInfo;

    UIView *rootView = self.targetView;
    
    if ([rootView isKindOfClass:[UITableView class]]) {
        UITableView *tableView =  (UITableView *)rootView ;
        [tableView.delegate tableView:tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:[data[@"row"] intValue]
                                                                                           inSection:[data[@"section"] intValue]]];
    }else if ([rootView isKindOfClass:[UICollectionView class]]) {
        UICollectionView *collectionView =  (UICollectionView *)rootView ;
        [collectionView.delegate collectionView:collectionView didSelectItemAtIndexPath:[NSIndexPath indexPathForRow:[data[@"row"] intValue]
                                                                                                           inSection:[data[@"section"] intValue]]];
    }
    
    return YES;
}

@end


@implementation DoraemonMCTextFiledEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage *)eventInfo {
    DoraemonMCEventHandler_call_super_handle_event

    UIView *rootView = self.targetView;
    NSDictionary *data = self.messageInfo.eventInfo;    
    [rootView do_mc_serialize_syncInfoWithDictionary:data];
    
    if ([rootView isKindOfClass:[UITextField class]]) {
        UITextField *tfView =  (UITextField *)rootView ;
        
        if (data[@"text"]) {
            tfView.text = data[@"text"];
        }
    }
    
    if ([rootView isKindOfClass:[UITextView class]]) {
        UITextView *tVView =  (UITextView *)rootView ;
        
        if (data[@"text"]) {
            tVView.text = data[@"text"];
        }
    }    
    return YES;
}

@end

@implementation DoraemonMCTabbarEventHandler

- (BOOL)handleEvent:(DoraemonMCMessage *)eventInfo {
    DoraemonMCEventHandler_call_super_handle_event

    UIView *rootView = self.targetView;
    
    UITabBarController *tabbarC = [DoraemonMCXPathSerializer ownerVCWithView:rootView];
    if ([tabbarC isKindOfClass:[UITabBarController class]]) {
        NSDictionary *data = self.messageInfo.eventInfo;
        NSInteger selectIndex = [data[@"selectIndex"] integerValue];
        BOOL notMatch = NO;
        if (tabbarC.viewControllers.count <= selectIndex ||
            ![data[@"selectVC"] isEqualToString:NSStringFromClass([tabbarC.viewControllers[selectIndex] class])]) {
            notMatch = YES;
        }
        if (notMatch) {
            __block UIViewController *matchVC = nil;
            [tabbarC.viewControllers enumerateObjectsUsingBlock:^(__kindof UIViewController * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                if ([NSStringFromClass(obj.class) isEqualToString:data[@"selectVC"]]) {
                    matchVC = obj;
                }
            }];
            if (matchVC) {
                [tabbarC setSelectedViewController:matchVC];
            }
        }else {
            [tabbarC setSelectedIndex:selectIndex];
        }
        return YES;
    }
    
    return NO;
}

@end
