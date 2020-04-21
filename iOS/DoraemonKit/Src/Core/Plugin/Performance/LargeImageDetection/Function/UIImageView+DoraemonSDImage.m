//
//  UIImageView+DoraemonSDImage.m
//  AFNetworking
//
//  Created by yixiang on 2019/6/19.
//

#if __has_include(<SDWebImage/UIImageView+WebCache.h>)
#import "UIImageView+DoraemonSDImage.h"
//#import <SDWebImage/UIImageView+WebCache.h>
#import "NSObject+Doraemon.h"
#import "DoraemonLargeImageDetectionManager.h"
#import <objc/runtime.h>

@implementation UIImageView (DoraemonSDImage)

+ (void)load{
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wundeclared-selector"
    
    Method originAddObserverMethod = class_getInstanceMethod(self, @selector(sd_setImageWithURL:placeholderImage:options:context:progress:completed:));
    if (originAddObserverMethod) {
        // 兼容SDWebImage 最新版本 5.0.6
        [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(sd_setImageWithURL:placeholderImage:options:context:progress:completed:)
                                              swizzledSel:@selector(doraemon_sd_setImageWithURL:placeholderImage:options:context:progress:completed:)];
    }else{
        // 兼容SDWebImage 我们使用的版本 3.7.6
        [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(sd_setImageWithURL:placeholderImage:options:progress:completed:) swizzledSel:@selector(doraemon_sd_setImageWithURL:placeholderImage:options:progress:completed:)];
    }
    //估计以后还会兼容其他版本啊。哭。
    #pragma clang diagnostic pop
}

- (void)doraemon_sd_setImageWithURL:(nullable NSURL *)url
          placeholderImage:(nullable UIImage *)placeholder
                   options:(NSUInteger)options
                   context:(id)context
                  progress:(id)progressBlock
                          completed:(void(^)(UIImage * _Nullable image, NSError * _Nullable error, NSInteger cacheType, NSURL * _Nullable imageURL))completedBlock{
    
    __weak typeof(self) weafSelf = self;
    if ([DoraemonLargeImageDetectionManager shareInstance].detecting) {
        id replaceCompletedBlock = ^(UIImage * _Nullable image, NSError * _Nullable error, NSInteger cacheType, NSURL * _Nullable imageURL){
            NSData *data = UIImageJPEGRepresentation(image, 1.0);
            if (!data && data.length <= 0) {
                data = UIImagePNGRepresentation(image);
            }
            if (data.length > [DoraemonLargeImageDetectionManager shareInstance].minimumDetectionSize) {
                NSString *drawText = [NSString stringWithFormat:@"url : %@ \n size : %fKB",[url absoluteString],data.length/1024.];
                weafSelf.image = [self drawText:drawText inImage:weafSelf.image];
            }
            if (completedBlock) {
                completedBlock(weafSelf.image, error, cacheType,imageURL);
            }
            
        };
        [self doraemon_sd_setImageWithURL:url placeholderImage:placeholder options:options context:context progress:progressBlock completed:replaceCompletedBlock];
    }else{
        [self doraemon_sd_setImageWithURL:url placeholderImage:placeholder options:options context:context progress:progressBlock completed:completedBlock];
    }
}

- (void)doraemon_sd_setImageWithURL:(NSURL *)url placeholderImage:(UIImage *)placeholder options:(NSUInteger)options progress:(id)progressBlock completed:(void(^)(UIImage *image, NSError *error, NSInteger cacheType, NSURL *imageURL))completedBlock{
    __weak typeof(self) weafSelf = self;
    if ([DoraemonLargeImageDetectionManager shareInstance].detecting){
        id replaceCompletedBlock = ^(UIImage *image, NSError *error, NSInteger cacheType, NSURL *imageURL) {
            NSData *data = UIImageJPEGRepresentation(image, 1.0);
            if (!data && data.length <= 0) {
                data = UIImagePNGRepresentation(image);
            }
            if (data.length > [DoraemonLargeImageDetectionManager shareInstance].minimumDetectionSize) {
                NSString *drawText = [NSString stringWithFormat:@"url : %@ \n size : %fKB",[url absoluteString],data.length/1024.];
                weafSelf.image = [self drawText:drawText inImage:weafSelf.image];
            }
            if (completedBlock) {
                completedBlock(weafSelf.image, error, cacheType,imageURL);
            }
        };
        [self doraemon_sd_setImageWithURL:url placeholderImage:placeholder options:options progress:progressBlock completed:replaceCompletedBlock];
    }else{
        [self doraemon_sd_setImageWithURL:url placeholderImage:placeholder options:options progress:progressBlock completed:completedBlock];
    }
}

- (UIImage *)drawText:(NSString *)text inImage:(UIImage *)image{
    UIFont *font = [UIFont boldSystemFontOfSize:12 * (image.size.height / 230)];
    UIGraphicsBeginImageContextWithOptions(image.size, NO, [UIScreen mainScreen].scale);
    [image drawInRect:CGRectMake(0, 0, image.size.width, image.size.height)];
    CGRect rect = CGRectMake(0, 0, image.size.width, image.size.height);
    [text drawInRect:CGRectIntegral(rect) withAttributes:@{
                                                           NSFontAttributeName : font,
                                                           NSForegroundColorAttributeName : [UIColor redColor]
                                                           }];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return newImage;
}


@end

#endif
