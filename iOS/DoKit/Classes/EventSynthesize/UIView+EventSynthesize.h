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

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIView (EventSynthesize)

- (void)dk_tap;

- (void)dk_longPress;

- (void)dk_dragWithStartPoint:(CGPoint)startPoint endPoint:(CGPoint)endPoint;

- (void)dk_dragWithStartPoint:(CGPoint)startPoint endPoint:(CGPoint)endPoint stepCount:(NSUInteger)stepCount;

- (void)dk_dragWithStartPoint:(CGPoint)startPoint displacement:(CGPoint)displacement stepCount:(NSUInteger)stepCount;

- (void)dk_pinchInWithDistance:(CGFloat)distance;

- (void)dk_pinchOutWithDistance:(CGFloat)distance;

- (void)dk_dragWithPointEventArray:(nullable NSArray<NSArray<NSValue *> *> *)pointEventArray;

- (void)dk_swipeWithDirection:(UISwipeGestureRecognizerDirection)swipeGestureRecognizerDirection;

- (void)dk_rotateWithRadian:(CGFloat)radian;

@end

NS_ASSUME_NONNULL_END
