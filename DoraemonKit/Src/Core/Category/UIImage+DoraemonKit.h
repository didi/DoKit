//
//  UIImage+DoraemonKit.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>

@interface UIImage (DoraemonKit)

+ (UIImage *)doraemon_imageNamed:(NSString *)name;

//压缩图片尺寸 等比缩放 通过计算得到缩放系数
- (UIImage*)doraemon_scaledToSize:(CGSize)newSize;

@end
