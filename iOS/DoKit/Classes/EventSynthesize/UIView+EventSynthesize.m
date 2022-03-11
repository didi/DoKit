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
    CGPoint point = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    UITouch *touch = [[UITouch alloc] initWithStartPoint:point view:self];
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
    CGPoint point = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    UITouch *touch = [[UITouch alloc] initWithStartPoint:point view:self];
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
    [self dk_dragWithStartPoint:startPoint endPoint:endPoint stepCount:2];
}

- (void)dk_dragWithStartPoint:(CGPoint)startPoint endPoint:(CGPoint)endPoint stepCount:(NSUInteger)stepCount {
    CGPoint displacement = CGPointMake(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    [self dk_dragWithStartPoint:startPoint displacement:displacement stepCount:stepCount];
}

static const CGFloat kTwoFingerConstantWidth = 40;

- (void)dk_pinchOutWithDistance:(CGFloat)distance {
    CGPoint centerPoint = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    // estimate the first finger to be on the left
    CGPoint finger1Start = CGPointMake(centerPoint.x - kTwoFingerConstantWidth, centerPoint.y);
    CGPoint finger1End = CGPointMake(centerPoint.x - kTwoFingerConstantWidth - distance, centerPoint.y);
    // estimate the second finger to be on the right
    CGPoint finger2Start = CGPointMake(centerPoint.x + kTwoFingerConstantWidth, centerPoint.y);
    CGPoint finger2End = CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance, centerPoint.y);

    // Began will call - viewForZoomingInScrollView:
    // Consume *two* move event then clear view for gesture recognizer, then call - viewForZoomingInScrollView: again
    // We should pass *another* move event to trigger UIGestureRecognizerStateChange
    // The last is end event
    // So we should pass 1 began, 3 move, 1 end event
    [self dk_dragWithPointEventArray:@[@[[NSValue valueWithCGPoint:finger1Start], [NSValue valueWithCGPoint:finger2Start]],
            @[[NSValue valueWithCGPoint:CGPointMake(centerPoint.x - kTwoFingerConstantWidth - 1, centerPoint.y)], [NSValue valueWithCGPoint:CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance + 1, centerPoint.y)]],
            @[[NSValue valueWithCGPoint:CGPointMake(centerPoint.x - kTwoFingerConstantWidth - distance / 2, centerPoint.y)], [NSValue valueWithCGPoint:CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance / 2, centerPoint.y)]],
            @[[NSValue valueWithCGPoint:finger1End], [NSValue valueWithCGPoint:finger2End]]]];
}

- (void)dk_pinchInWithDistance:(CGFloat)distance {
    CGPoint centerPoint = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    // estimate the first finger to be on the left
    CGPoint finger1Start = CGPointMake(centerPoint.x - kTwoFingerConstantWidth - distance, centerPoint.y);
    CGPoint finger1End = CGPointMake(centerPoint.x - kTwoFingerConstantWidth, centerPoint.y);
    // estimate the second finger to be on the right
    CGPoint finger2Start = CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance, centerPoint.y);
    CGPoint finger2End = CGPointMake(centerPoint.x + kTwoFingerConstantWidth, centerPoint.y);

    // Began will call - viewForZoomingInScrollView:
    // Consume *two* move event then clear view for gesture recognizer, then call - viewForZoomingInScrollView: again
    // We should pass *another* move event to trigger UIGestureRecognizerStateChange
    // The last is end event
    // So we should pass 1 began, 3 move, 1 end event
    [self dk_dragWithPointEventArray:@[@[[NSValue valueWithCGPoint:finger1Start], [NSValue valueWithCGPoint:finger2Start]],
            @[[NSValue valueWithCGPoint:CGPointMake(centerPoint.x - kTwoFingerConstantWidth - distance + 1, centerPoint.y)], [NSValue valueWithCGPoint:CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance - 1, centerPoint.y)]],
            @[[NSValue valueWithCGPoint:CGPointMake(centerPoint.x - kTwoFingerConstantWidth - distance / 2, centerPoint.y)], [NSValue valueWithCGPoint:CGPointMake(centerPoint.x + kTwoFingerConstantWidth + distance / 2, centerPoint.y)]],
            @[[NSValue valueWithCGPoint:finger1End], [NSValue valueWithCGPoint:finger2End]]]];
}

- (void)dk_dragWithStartPoint:(CGPoint)startPoint displacement:(CGPoint)displacement stepCount:(NSUInteger)stepCount {
    if (stepCount < 2 || CGPointEqualToPoint(displacement, CGPointZero)) {
        NSAssert(NO, @"drag need start, move at least(end will commit automatically). Or displacement equal to (0, 0)");

        return;
    }
    if (__builtin_expect(!self.window, NO)) {
        NSAssert1(NO, @"%@ without window property", self);

        return;
    }
    UITouch *touch = nil;
    UIEvent *event = nil;
    for (NSUInteger i = 0; i < stepCount; ++i) {
        CGFloat progress = ((CGFloat) i) / (stepCount - 1);
        CGPoint point = CGPointMake(startPoint.x + progress * displacement.x, startPoint.y + progress * displacement.y);
        if (!i) {
            // Start event
            touch = [[UITouch alloc] initWithStartPoint:point view:self];
            event = DKEventWithTouches(@[touch]);
            if (__builtin_expect(!event, NO)) {
                NSAssert(NO, @"DKEventWithTouches() return nil");

                return;
            }
            [UIApplication.sharedApplication sendEvent:event];
        } else {
            // UIScrollView track mode!
            // 如果不考虑延时（stepCount），可以注释下面的代码
//            CFRunLoopMode runLoopMode = CFRunLoopCopyCurrentMode(CFRunLoopGetCurrent());
//            CFRunLoopRunResult runLoopRunResult = CFRunLoopRunInMode(runLoopMode, DELAY_TIME, false);
//            CFRelease(runLoopMode);
//            NSAssert(runLoopRunResult == kCFRunLoopRunTimedOut, @"CFRunLoopRunInMode() must be timeout");

            // Move event
            [touch dk_updateWithPointInWindow:[self.window convertPoint:point fromView:self]];
            [touch dk_updateWithPhase:UITouchPhaseMoved];
            [UIApplication.sharedApplication sendEvent:event];

            if (i == stepCount - 1) {
                [touch dk_updateWithPhase:UITouchPhaseEnded];
                [UIApplication.sharedApplication sendEvent:event];
            }
        }
    }
}

- (void)dk_dragWithPointEventArray:(nullable NSArray<NSArray<NSValue *> *> *)pointEventArray {
    if (pointEventArray.count < 2) {
        NSAssert(NO, @"drag must have one event at least");

        return;
    }
    NSUInteger pointCountInFirstEvent = pointEventArray.firstObject.count;
    // Note: point should be in view's coordinate!

    __block BOOL pointCountIsError = NO;
    // -[NSArray enumerateObjectsAtIndexes:options:usingBlock:] can accept NSIndexSet init with (1, 0), then the block will not be invoked
    [pointEventArray enumerateObjectsAtIndexes:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(1, pointEventArray.count - 1)] options:0 usingBlock:^(NSArray<NSValue *> *obj, __attribute__((unused)) NSUInteger idx, BOOL *stop) {
        if (obj.count != pointCountInFirstEvent) {
            pointCountIsError = YES;
            *stop = YES;
        }
    }];
    if (pointCountIsError) {
        NSAssert(NO, @"All paths must have the same number of points");

        return;
    }
    NSMutableArray<UITouch *> *touchArray = [NSMutableArray arrayWithCapacity:pointCountInFirstEvent];
    __block UIEvent *event = nil;
    [pointEventArray enumerateObjectsUsingBlock:^(NSArray<NSValue *> *obj, NSUInteger idx, BOOL *__attribute__((unused)) stop) {
        if (!idx) {
            // Start event
            [obj enumerateObjectsUsingBlock:^(NSValue *obj, __attribute__((unused)) NSUInteger idx, BOOL *__attribute__((unused)) stop) {
                UITouch *touch = [[UITouch alloc] initWithStartPoint:obj.CGPointValue view:self];
                [touchArray addObject:touch];
            }];
            event = DKEventWithTouches(touchArray.copy);
            if (__builtin_expect(!event, NO)) {
                NSAssert(NO, @"DKEventWithTouches() return nil");

                return;
            }
            [UIApplication.sharedApplication sendEvent:event];
        } else {
//            CFRunLoopMode runLoopMode = CFRunLoopCopyCurrentMode(CFRunLoopGetCurrent());
//            CFRunLoopRunResult runLoopRunResult = CFRunLoopRunInMode(runLoopMode, DELAY_TIME, false);
//            CFRelease(runLoopMode);
//            NSAssert(runLoopRunResult == kCFRunLoopRunTimedOut, @"CFRunLoopRunInMode() must be timeout");
            // Move event
            [obj enumerateObjectsUsingBlock:^(NSValue *obj, NSUInteger idx, BOOL *__attribute__((unused)) stop) {
                // TODO(ChasonTang): If CGPoint not change, UITouchPhase should be UITouchPhaseStationary instead of UITouchPhaseMoved
                [touchArray[idx] dk_updateWithPointInWindow:[self.window convertPoint:obj.CGPointValue fromView:self]];
                [touchArray[idx] dk_updateWithPhase:UITouchPhaseMoved];
            }];
            [UIApplication.sharedApplication sendEvent:event];

            if (idx == pointEventArray.count - 1) {
                [touchArray enumerateObjectsUsingBlock:^(UITouch *__attribute((unused)) obj, __attribute((unused)) NSUInteger idx, BOOL *__attribute((unused)) stop) {
                    [touchArray[idx] dk_updateWithPhase:UITouchPhaseEnded];
                }];
                [UIApplication.sharedApplication sendEvent:event];
            }
        }
    }];
}

- (void)dk_swipeWithDirection:(UISwipeGestureRecognizerDirection)swipeGestureRecognizerDirection {
    if (__builtin_expect(!self.window, NO)) {
        NSAssert1(NO, @"%@ without window property", self);

        return;
    }
    if ((swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionDown && swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionUp) || (swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionLeft && swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionRight)) {
        NSAssert(NO, @"swipeGestureRecognizerDirection cannot be left and right or up and down in the meantime");

        return;
    }
    CGPoint point = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    CGPoint endPoint = point;
    if (swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionRight) {
        endPoint.x += point.x;
    }
    if (swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionLeft) {
        endPoint.x -= point.x;
    }
    if (swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionUp) {
        endPoint.y -= point.y;
    }
    if (swipeGestureRecognizerDirection & UISwipeGestureRecognizerDirectionDown) {
        endPoint.y += point.y;
    }
    UITouch *touch = [[UITouch alloc] initWithStartPoint:point view:self];
    UIEvent *event = DKEventWithTouches(@[touch]);
    if (__builtin_expect(!event, NO)) {
        NSAssert(NO, @"DKEventWithTouches() return nil");

        return;
    }
    [UIApplication.sharedApplication sendEvent:event];
    [touch dk_updateWithPhase:UITouchPhaseMoved];
    [touch dk_updateWithPointInWindow:[self.window convertPoint:endPoint fromView:self]];
    [UIApplication.sharedApplication sendEvent:event];
    [touch dk_updateWithPhase:UITouchPhaseEnded];
    [UIApplication.sharedApplication sendEvent:event];
}

- (void)dk_rotateWithRadian:(CGFloat)radian {
    CGPoint centerPoint = CGPointMake(self.bounds.size.width / 2, self.bounds.size.height / 2);
    // estimate the first finger to be on the left
    CGPoint finger1Start = CGPointMake(centerPoint.x - kTwoFingerConstantWidth, centerPoint.y);
    CGPoint finger1End = CGPointMake(centerPoint.x - kTwoFingerConstantWidth * cos(radian), centerPoint.y - kTwoFingerConstantWidth * sin(radian));
    
    // estimate the second finger to be on the right
    CGPoint finger2Start = CGPointMake(centerPoint.x + kTwoFingerConstantWidth, centerPoint.y);
    CGPoint finger2End = CGPointMake(centerPoint.x + kTwoFingerConstantWidth * cos(radian), centerPoint.y + kTwoFingerConstantWidth * sin(radian));

    [self dk_dragWithPointEventArray:@[@[[NSValue valueWithCGPoint:finger1Start], [NSValue valueWithCGPoint:finger2Start]],
            @[[NSValue valueWithCGPoint:finger1End], [NSValue valueWithCGPoint:finger2End]]]];
}

@end
