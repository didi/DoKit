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
#import "UITouch+DKEventSynthesize.h"

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

- (void)dk_dragWithStartPoint:(CGPoint)startPoint endPoint:(CGPoint)endPoint {
    [self dk_dragWithStartPoint:startPoint endPoint:endPoint stepCount:3];
}

- (void)dk_dragWithStartPoint:(CGPoint)startPoint endPoint:(CGPoint)endPoint stepCount:(NSUInteger)stepCount {
    CGPoint displacement = CGPointMake(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    [self dk_dragWithStartPoint:startPoint displacement:displacement stepCount:stepCount];
}

//- (void)dk_pinchInWithCenterPoint:(CGPoint)centerPoint distance:(CGFloat)distance {
//    // estimate the first finger to be on the left
//
//}

- (void)dk_dragWithStartPoint:(CGPoint)startPoint displacement:(CGPoint)displacement stepCount:(NSUInteger)stepCount {
    if (stepCount < 3 || CGPointEqualToPoint(displacement, CGPointZero)) {
        NSAssert(NO, @"drag need start, move, end point at least. Or displacement equal to (0, 0)");

        return;
    }
    if (__builtin_expect(!self.window, NO)) {
        NSAssert1(NO, @"%@ without window property", self);

        return;
    }
    startPoint = [self.window convertPoint:startPoint fromView:self];
    UITouch *touch = nil;
    UIEvent *event = nil;
    for (NSUInteger i = 0; i < stepCount; ++i) {
        CGFloat progress = ((CGFloat) i) / (stepCount - 1);
        CGPoint point = CGPointMake(startPoint.x + progress * displacement.x, startPoint.y + progress * displacement.y);
        if (!i) {
            // Start event
            touch = [[UITouch alloc] initWithPoint:point window:self.window];
            event = DKEventWithTouches(@[touch]);
            if (__builtin_expect(!event, NO)) {
                NSAssert(NO, @"DKEventWithTouches() return nil");

                return;
            }
            [UIApplication.sharedApplication sendEvent:event];
        } else {
            // UIScrollView track mode!
            // 如果不考虑延时（stepCount），可以注释下面的代码
            CFRunLoopMode runLoopMode = CFRunLoopCopyCurrentMode(CFRunLoopGetCurrent());
            CFRunLoopRunResult runLoopRunResult = CFRunLoopRunInMode(runLoopMode, DELAY_TIME, false);
            CFRelease(runLoopMode);
            NSAssert(runLoopRunResult == kCFRunLoopRunTimedOut, @"CFRunLoopRunInMode() must be timeout");

            // Move event
            [touch dk_updateWithPointInWindow:point];
            [touch dk_updateWithPhase:i != stepCount - 1 ? UITouchPhaseMoved : UITouchPhaseEnded];
            [UIApplication.sharedApplication sendEvent:event];
        }
    }
}

- (void)dk_dragPointsAlongPaths:(nullable NSArray<NSArray<NSValue *> *> *)pathArray {
    if (pathArray.firstObject.count < 2) {
        NSAssert(NO, @"There must be at least one path with at least two point(begin and end)");

        return;
    }

    // Note: point should be in view's coordinate!

    NSUInteger pointCountInFirstPath = pathArray.firstObject.count;
    __block BOOL pointCountIsError = NO;
    [pathArray enumerateObjectsAtIndexes:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(1, pathArray.count - 1)] options:0 usingBlock:^(NSArray<NSValue *> *obj, NSUInteger idx, BOOL *stop) {
        if (obj.count != pointCountInFirstPath) {
            pointCountIsError = YES;
            *stop = YES;
        }
    }];
    if (pointCountIsError) {
        NSAssert(NO, @"All paths must have the same number of points");

        return;
    }

}

@end
