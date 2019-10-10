//
//  UIImage+Doraemon.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>

@interface UIImage (Doraemon)

+ (UIImage *)doraemon_imageNamed:(NSString *)name;

//压缩图片尺寸 等比缩放 通过计算得到缩放系数
- (UIImage*)doraemon_scaledToSize:(CGSize)newSize;

/**
Create and return a 1x1 point size image with the given color.

@param color  The color.
*/
+ (nullable UIImage *)imageWithColor:(UIColor *)color;

/**
 Create and return a pure color image with the given color and size.
 
 @param color  The color.
 @param size   New image's type.
 */
+ (nullable UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size;

@end
