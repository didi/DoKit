//
//  UIImage+Doraemon.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (Doraemon)

+ (nullable UIImage *)doraemon_imageNamed:(NSString *)name __attribute((deprecated("已废弃，请使用doraemon_xcassetImageNamed")));

+ (nullable UIImage *)doraemon_xcassetImageNamed:(NSString *)name;

//压缩图片尺寸 等比缩放 通过计算得到缩放系数
- (nullable UIImage*)doraemon_scaledToSize:(CGSize)newSize;

/**
Create and return a 1x1 point size image with the given color.

@param color  The color.
*/
+ (UIImage *)doraemon_imageWithColor:(UIColor *)color;

/**
 Create and return a pure color image with the given color and size.
 
 @param color  The color.
 @param size   New image's type.
 */
+ (UIImage *)doraemon_imageWithColor:(UIColor *)color size:(CGSize)size;

@end

NS_ASSUME_NONNULL_END
