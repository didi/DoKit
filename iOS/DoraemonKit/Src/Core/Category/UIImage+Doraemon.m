//
//  UIImage+DoraemonKit.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "UIImage+Doraemon.h"

@class DoraemonManager;
@implementation UIImage (Doraemon)

+ (UIImage *)doraemon_imageNamed:(NSString *)name{
    if(name){
        NSBundle *bundle = [NSBundle bundleForClass:NSClassFromString(@"DoraemonManager")];
        NSURL *url = [bundle URLForResource:@"DoraemonKit" withExtension:@"bundle"];
        if(!url) return nil;
        NSBundle *imageBundle = [NSBundle bundleWithURL:url];
        
        NSString *imageName = nil;
        CGFloat scale = [UIScreen mainScreen].scale;
        if (ABS(scale-3) <= 0.001){
            imageName = [NSString stringWithFormat:@"%@@3x",name];
        }else if(ABS(scale-2) <= 0.001){
            imageName = [NSString stringWithFormat:@"%@@2x",name];
        }else{
            imageName = name;
        }
        UIImage *image = [UIImage imageWithContentsOfFile:[imageBundle pathForResource:imageName ofType:@"png"]];
        if (!image) {
            image = [UIImage imageWithContentsOfFile:[imageBundle pathForResource:name ofType:@"png"]];
            if (!image) {
                image = [UIImage imageNamed:name];
            }
        }
        return image;
    }
    
    return nil;
}

//压缩图片尺寸 等比缩放 通过计算得到缩放系数
- (UIImage*)doraemon_scaledToSize:(CGSize)newSize{
    UIImage *sourceImage = self;
    UIImage *newImage = nil;
    CGSize imageSize = sourceImage.size;
    CGFloat width = imageSize.width;
    CGFloat height = imageSize.height;
    CGFloat targetWidth = newSize.width;
    CGFloat targetHeight = newSize.height;
    CGFloat scaleFactor = 0.0;
    CGFloat scaledWidth = targetWidth;
    CGFloat scaledHeight = targetHeight;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    
    if (CGSizeEqualToSize(imageSize, newSize) == NO)
    {
        CGFloat widthFactor = targetWidth / width;
        CGFloat heightFactor = targetHeight / height;
        if (widthFactor > heightFactor)
            scaleFactor = widthFactor; // scale to fit height
        else
            scaleFactor = heightFactor; // scale to fit width
        
        scaledWidth= width * scaleFactor;
        scaledHeight = height * scaleFactor;
        // center the image
        if (widthFactor > heightFactor)
        {
            thumbnailPoint.y = (targetHeight - scaledHeight) * 0.5;
        }
        else if (widthFactor < heightFactor)
        {
            thumbnailPoint.x = (targetWidth - scaledWidth) * 0.5;
        }
    }
    
    UIGraphicsBeginImageContextWithOptions(newSize, NO, [UIScreen mainScreen].scale);
    CGRect thumbnailRect = CGRectZero;
    thumbnailRect.origin = thumbnailPoint;
    thumbnailRect.size.width= scaledWidth;
    thumbnailRect.size.height = scaledHeight;
    [sourceImage drawInRect:thumbnailRect];
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    if(newImage == nil)
        NSLog(@"could not scale image");
    //pop the context to get back to the default
    UIGraphicsEndImageContext();
    
    return newImage;
}

+ (UIImage *)imageWithColor:(UIColor *)color {
    return [self imageWithColor:color size:CGSizeMake(1, 1)];
}

+ (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size {
    if (!color || size.width <= 0 || size.height <= 0) return nil;
    CGRect rect = CGRectMake(0.0f, 0.0f, size.width, size.height);
    UIGraphicsBeginImageContextWithOptions(rect.size, NO, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

@end
