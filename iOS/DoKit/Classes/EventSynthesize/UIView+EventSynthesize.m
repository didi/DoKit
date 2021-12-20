/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "UIView+EventSynthesize.h"
#import <DoraemonKit/UITouch+DKEventSynthesize.h>

@implementation UIView (EventSynthesize)

- (void)dk_tap {
    if (__builtin_expect(!self.window, NO)) {
        NSAssert1(NO, @"%@ without window property", self);
        
        return;
    }
    CGPoint point = [self.window convertPoint:CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2) fromView:self];
    UITouch *touch = [[UITouch alloc] initWithPoint:point window:self.window];
    UIEvent *event = DKEventWithTouches(@[touch]);
    if (__builtin_expect(!event, NO)) {
        NSAssert(NO, @"DKEventWithTouches() return nil");

        return;
    }
    [UIApplication.sharedApplication sendEvent:event];
    [touch dk_updateWithPhase:UITouchPhaseEnded];
    [UIApplication.sharedApplication sendEvent:event];
}

static const double DELAY_TIME = 0.01;

- (void)dk_longPress {
    if (__builtin_expect(!self.window, NO)) {
        NSAssert1(NO, @"%@ without window property", self);
        
        return;
    }
    CGPoint point = [self.window convertPoint:CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2) fromView:self];
    UITouch *touch = [[UITouch alloc] initWithPoint:point window:self.window];
    UIEvent *event = DKEventWithTouches(@[touch]);
    if (__builtin_expect(!event, NO)) {
        NSAssert(NO, @"DKEventWithTouches() return nil");

        return;
    }
    [UIApplication.sharedApplication sendEvent:event];
    CFRunLoopRunResult runLoopRunResult = CFRunLoopRunInMode(kCFRunLoopDefaultMode, DELAY_TIME, false);
    NSAssert(runLoopRunResult == kCFRunLoopRunTimedOut, @"CFRunLoopRunInMode() must be timeout");
    // UILongPressGestureRecognizer ignore the timestamp member of UITouch and UIEvent
    [touch dk_updateWithPhase:UITouchPhaseEnded];
    [UIApplication.sharedApplication sendEvent:event];
}

@end
