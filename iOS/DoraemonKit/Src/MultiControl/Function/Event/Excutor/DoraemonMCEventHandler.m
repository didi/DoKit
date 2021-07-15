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

@implementation DoraemonMCEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo {
    self.messageInfo = eventInfo;
    self.targetView = [self fetchTargetView];
    if (self.messageInfo.isFirstResponder && self.targetView.isFirstResponder == NO) {
        [self.targetView becomeFirstResponder];
    }else if (self.messageInfo.isFirstResponder == NO && self.targetView.isFirstResponder) {
        [self.targetView resignFirstResponder];
    }
}

- (UIView *)fetchTargetView {
    return [DoraemonMCXPathSerializer viewFromXpath:self.messageInfo.xPath];
}
@end


@implementation DoraemonMCGestureRecognizerEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo {
    
    [super handleEvent:eventInfo];
    
    UIView *targetView = self.targetView;
    
    NSDictionary *data = self.messageInfo.eventInfo;
    NSInteger gesIndex = [data[@"gesIndex"] intValue];
    
    if (targetView.gestureRecognizers.count <= gesIndex) {
        NSLog(@"gestureRecognizer has not found %@",eventInfo);
        return ;
    }
    
    UIGestureRecognizer *ges = targetView.gestureRecognizers[gesIndex];
    
    [DoraemonMCGustureSerializer syncInfoToGusture:ges withDict:data];

    [ges do_mc_manual_doAction];
}

@end

@implementation DoraemonMCControlEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo {
    [super handleEvent:eventInfo];

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
}

@end

@implementation DoraemonMCTableViewEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo {
    [super handleEvent:eventInfo];
    NSDictionary *data = self.messageInfo.eventInfo;

    UIView *rootView = self.targetView;
    
    if ([rootView isKindOfClass:[UITableView class]]) {
        UITableView *tableView =  (UITableView *)rootView ;
        [tableView.delegate tableView:tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:[data[@"row"] intValue] inSection:[data[@"section"] intValue]]];
    }
}

@end


@implementation DoraemonMCTextFiledEventHandler

- (void)handleEvent:(DoraemonMCMessage *)eventInfo {
    [super handleEvent:eventInfo];
    UIView *rootView = self.targetView;
    NSDictionary *data = self.messageInfo.eventInfo;

    if ([rootView isKindOfClass:[UIControl class]]) {
        UIControl *ctl = (UIControl *)rootView;
        UIControlState ctlState = [data[@"ctlState"] integerValue];
        
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
    
    if ([rootView isKindOfClass:[UITextField class]]) {
        UITextField *tfView =  (UITextField *)rootView ;
        
        if (data[@"text"]) {
            tfView.text = data[@"text"];
        }
    }
    
    if ([rootView isKindOfClass:[UITextField class]]) {
        UITextView *tVView =  (UITextView *)rootView ;
        
        if (data[@"text"]) {
            tVView.text = data[@"text"];
        }
    }
}


@end
