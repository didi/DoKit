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

#import "UITouch+DKEventSynthesize.h"
#import <objc/runtime.h>

NS_ASSUME_NONNULL_BEGIN

@interface UITouch (APLPrivate)

- (void)setTimestamp:(NSTimeInterval)timestamp;

- (void)setPhase:(UITouchPhase)phase;

- (void)setTapCount:(NSUInteger)tapCount;

- (void)setWindow:(nullable UIWindow *)window;

- (void)setView:(nullable UIView *)view;

- (void)_setLocationInWindow:(CGPoint)locationInWindow resetPrevious:(BOOL)resetPrevious;

- (void)_setIsFirstTouchForView:(BOOL)firstTouchForView;

@end

@interface UITouchesEvent : UIEvent

- (void)_clearTouches;

- (void)_addTouch:(UITouch *)touch forDelayedDelivery:(BOOL)forDelayedDelivery;

@end

@interface UIApplication (APLPrivate)

- (nullable UITouchesEvent *)_touchesEvent;

@end

NS_ASSUME_NONNULL_END

UIEvent *_Nullable DKEventWithTouches(NSArray<UITouch *> *touches) {
    NSCAssert(touches.count > 0, @"touches have no element");
    UITouchesEvent *touchesEvent = [UIApplication.sharedApplication _touchesEvent];
    NSCAssert(touchesEvent, @"-[UIApplication _touchesEvent] return nil");
    [touchesEvent _clearTouches];
    [touches enumerateObjectsUsingBlock:^(UITouch *_Nonnull obj, __attribute__((unused)) NSUInteger idx, __attribute__((unused)) BOOL *_Nonnull stop) {
        [touchesEvent _addTouch:obj forDelayedDelivery:NO];
    }];

    return touchesEvent;
}

@implementation UITouch (DKEventSynthesize)

- (instancetype)initWithStartPoint:(CGPoint)startPoint view:(UIView *)view {
    if (!view.window) {
        NSAssert(NO, @"view.window is nil");

        return nil;
    }
    self = [self init];
    self.timestamp = 0;
    self.phase = UITouchPhaseBegan;
    self.tapCount = 1;
    self.window = view.window;
    // - hitTest:withEvent: can pass event with nil
    // and could return nil as hitTestView.
    // Then we use window as hitTestView
    self.view = view;
    [self _setLocationInWindow:[view.window convertPoint:startPoint fromView:view] resetPrevious:YES];
    if ([self respondsToSelector:@selector(_setIsFirstTouchForView:)]) {
        [self _setIsFirstTouchForView:YES];
    } else {
        // We modify the touchFlags ivar struct directly.
        // First entry is _firstTouchForView
        Ivar flagsIvar = class_getInstanceVariable(UITouch.class, "_touchFlags");
        ptrdiff_t touchFlagsOffset = ivar_getOffset(flagsIvar);
        char *flags = (__bridge void *) self + touchFlagsOffset;
        *flags = *flags | (char) 0x01;
    }

    return self;
}

- (void)dk_updateWithPhase:(UITouchPhase)phase {
    self.phase = phase;
}

- (void)dk_updateWithPointInWindow:(CGPoint)pointInWindow {
    [self _setLocationInWindow:pointInWindow resetPrevious:NO];
}

@end
