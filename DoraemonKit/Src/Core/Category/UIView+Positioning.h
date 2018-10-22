//
//  UIView+Positioning.h
//
//  Created by Shai Mishali on 5/22/13.
//  Copyright (c) 2013 Shai Mishali. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 `UIView+Positioning` provides easy shorthand methods to defining the frame properties (width, height, x, y) of any UIView based object in an easy fashion, as well as extra helpful properties and methods.
 */

@interface UIView (Positioning)

/** View's X Position */
@property (nonatomic, assign) CGFloat   doraemon_x;

/** View's Y Position */
@property (nonatomic, assign) CGFloat   doraemon_y;

/** View's width */
@property (nonatomic, assign) CGFloat   doraemon_width;

/** View's height */
@property (nonatomic, assign) CGFloat   doraemon_height;

/** View's origin - Sets X and Y Positions */
@property (nonatomic, assign) CGPoint   doraemon_origin;

/** View's size - Sets Width and Height */
@property (nonatomic, assign) CGSize    doraemon_size;

/** Y value representing the bottom of the view **/
@property (nonatomic, assign) CGFloat   doraemon_bottom;

/** X Value representing the right side of the view **/
@property (nonatomic, assign) CGFloat   doraemon_right;

/** X Value representing the top of the view (alias of x) **/
@property (nonatomic, assign) CGFloat   doraemon_left;

/** Y Value representing the top of the view (alias of y) **/
@property (nonatomic, assign) CGFloat   doraemon_top;

/** X value of the object's center **/
@property (nonatomic, assign) CGFloat   doraemon_centerX;

/** Y value of the object's center **/
@property (nonatomic, assign) CGFloat   doraemon_centerY;


@end
