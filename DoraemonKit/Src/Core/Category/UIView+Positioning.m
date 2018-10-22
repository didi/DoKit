//
//  UIView+Positioning.m
//
//  Created by Shai Mishali on 5/22/13.
//  Copyright (c) 2013 Shai Mishali. All rights reserved.
//

#import "UIView+Positioning.h"

#define SCREEN_SCALE                    ([[UIScreen mainScreen] scale])
#define PIXEL_INTEGRAL(pointValue)      (round(pointValue * SCREEN_SCALE) / SCREEN_SCALE)

@implementation UIView (Positioning)
@dynamic doraemon_x, doraemon_y, doraemon_width, doraemon_height, doraemon_origin, doraemon_size;

// Setters
-(void)setDoraemon_x:(CGFloat)x{
    self.frame      = CGRectMake(PIXEL_INTEGRAL(x), self.doraemon_y, self.doraemon_width, self.doraemon_height);
}

-(void)setDoraemon_y:(CGFloat)y{
    self.frame      = CGRectMake(self.doraemon_x, PIXEL_INTEGRAL(y), self.doraemon_width, self.doraemon_height);
}

-(void)setDoraemon_width:(CGFloat)width{
    self.frame      = CGRectMake(self.doraemon_x, self.doraemon_y, PIXEL_INTEGRAL(width), self.doraemon_height);
}

-(void)setDoraemon_height:(CGFloat)height{
    self.frame      = CGRectMake(self.doraemon_x, self.doraemon_y, self.doraemon_width, PIXEL_INTEGRAL(height));
}

-(void)setDoraemon_origin:(CGPoint)origin{
    self.doraemon_x          = origin.x;
    self.doraemon_y          = origin.y;
}

-(void)setDoraemon_size:(CGSize)size{
    self.doraemon_width      = size.width;
    self.doraemon_height     = size.height;
}

-(void)setDoraemon_right:(CGFloat)right {
    self.doraemon_x          = right - self.doraemon_width;
}

-(void)setDoraemon_bottom:(CGFloat)bottom {
    self.doraemon_y          = bottom - self.doraemon_height;
}

-(void)setDoraemon_left:(CGFloat)left{
    self.doraemon_x          = left;
}

-(void)setDoraemon_top:(CGFloat)top{
    self.doraemon_y          = top;
}

-(void)setDoraemon_centerX:(CGFloat)centerX {
    self.center     = CGPointMake(PIXEL_INTEGRAL(centerX), self.center.y);
}

-(void)setDoraemon_centerY:(CGFloat)centerY {
    self.center     = CGPointMake(self.center.x, PIXEL_INTEGRAL(centerY));
}

// Getters
-(CGFloat)doraemon_x{
    return self.frame.origin.x;
}

-(CGFloat)doraemon_y{
    return self.frame.origin.y;
}

-(CGFloat)doraemon_width{
    return self.frame.size.width;
}

-(CGFloat)doraemon_height{
    return self.frame.size.height;
}

-(CGPoint)doraemon_origin{
    return CGPointMake(self.doraemon_x, self.doraemon_y);
}

-(CGSize)doraemon_size{
    return CGSizeMake(self.doraemon_width, self.doraemon_height);
}

-(CGFloat)doraemon_right {
    return self.frame.origin.x + self.frame.size.width;
}

-(CGFloat)doraemon_bottom {
    return self.frame.origin.y + self.frame.size.height;
}

-(CGFloat)doraemon_left{
    return self.doraemon_x;
}

-(CGFloat)doraemon_top{
    return self.doraemon_y;
}

-(CGFloat)doraemon_centerX {
    return self.center.x;
}

-(CGFloat)doraemon_centerY {
    return self.center.y;
}

@end
