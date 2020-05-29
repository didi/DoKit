//
//  DoraemonDemoCommonViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/12/3.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoCommonViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonManager.h"

@interface DoraemonDemoCommonViewController ()

@end

@implementation DoraemonDemoCommonViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"通用测试Demo");
    
    UIButton *btn0 = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn0.backgroundColor = [UIColor orangeColor];
    [btn0 setTitle:DoraemonDemoLocalizedString(@"子线程UI操作") forState:UIControlStateNormal];
    [btn0 addTarget:self action:@selector(addSubViewAtOtherThread) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn0];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn0.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:DoraemonDemoLocalizedString(@"显示入口") forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(showEntry) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:DoraemonDemoLocalizedString(@"隐藏入口") forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(hiddenEntry) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    
    UIImageView *imgView = [[UIImageView alloc] initWithFrame:CGRectMake(0, btn2.doraemon_bottom+60, 100, 100)];
    imgView.image = [[self class] jy_QRCodeFromString:@"hello" size:100];
    [self.view addSubview:imgView];

    // 设置允许摇一摇功能
    [UIApplication sharedApplication].applicationSupportsShakeToEdit = YES;
    // 并让自己成为第一响应者
    [self becomeFirstResponder];
}

- (void)addSubViewAtOtherThread{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        UIView *v = [UIView new];
        [self.view addSubview:v];
    });
}

- (void)showEntry{
    [[DoraemonManager shareInstance] showDoraemon];
}

- (void)hiddenEntry{
    [[DoraemonManager shareInstance] hiddenDoraemon];
}

/**
*  根据字符串生成二维码图片
*
*  @param code 二维码code
*  @param size 生成图片大小
*
*  @return image
*/
+ (UIImage *)jy_QRCodeFromString:(NSString *)code size:(CGFloat)size{
    //创建CIFilter 指定filter的名称为CIQRCodeGenerator
    CIFilter *filter = [CIFilter filterWithName:@"CIQRCodeGenerator"];
    //指定二维码的inputMessage,即你要生成二维码的字符串
    [filter setValue:[code dataUsingEncoding:NSUTF8StringEncoding] forKey:@"inputMessage"];
    //输出CIImage
    CIImage *ciImage = [filter outputImage];
    //对CIImage进行处理
    return [self createfNonInterpolatedImageFromCIImage:ciImage withSize:size];
}

/**
 *  对CIQRCodeGenerator 生成的CIImage对象进行不插值放大或缩小处理
 *
 *  @param iamge 原CIImage对象
 *  @param size  处理后的图片大小
 *
 *  @return image
 */
+ (UIImage *) createfNonInterpolatedImageFromCIImage:(CIImage *)iamge withSize:(CGFloat)size{
    CGRect extent = iamge.extent;
    CGFloat scale = MIN(size/CGRectGetWidth(extent), size/CGRectGetHeight(extent));
    size_t with = scale * CGRectGetWidth(extent);
    size_t height = scale * CGRectGetHeight(extent);
    
    UIGraphicsBeginImageContext(CGSizeMake(with, height));
    CGContextRef bitmapContextRef = UIGraphicsGetCurrentContext();
    
    CIContext *context = [CIContext contextWithOptions:nil];
    //通过CIContext 将CIImage生成CGImageRef
    CGImageRef bitmapImage = [context createCGImage:iamge fromRect:extent];
    //在对二维码放大或缩小处理时,禁止插值
    CGContextSetInterpolationQuality(bitmapContextRef, kCGInterpolationNone);
    //对二维码进行缩放
    CGContextScaleCTM(bitmapContextRef, scale, scale);
    //将二维码绘制到图片上下文
    CGContextDrawImage(bitmapContextRef, extent, bitmapImage);
    //获得上下文中二维码
    UIImage *retVal =  UIGraphicsGetImageFromCurrentImageContext();
    CGImageRelease(bitmapImage);
    CGContextRelease(bitmapContextRef);
    return retVal;
}

- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    NSLog(@"start shake");
    return;
}

- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    NSLog(@"cancel shake");
    return;
}

- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) { // 判断是否是摇动结束
        NSLog(@"stop shake");
    }
    return;
}



@end
