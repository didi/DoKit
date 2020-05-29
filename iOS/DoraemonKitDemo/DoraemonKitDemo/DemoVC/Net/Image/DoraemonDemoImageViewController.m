//
//  DoraemonDemoImageViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2019/6/18.
//  Copyright © 2019年 yixiang. All rights reserved.
//

#import "DoraemonDemoImageViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonDemoImageShowViewController.h"
#import <SDWebImage/SDWebImageManager.h>
#import "DoraemonUIWebViewViewController.h"
#import "DoraemonWKWebViewViewController.h"

@interface DoraemonDemoImageViewController ()

@property (nonatomic, copy) NSString *picUrl;

@end

@implementation DoraemonDemoImageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"图片测试");
    
    _picUrl = @"http://wx2.sinaimg.cn/large/61e7f4aaly1g0qsmz73juj20iv0iv4h0.jpg";
    
    UIButton *btn0 = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn0.backgroundColor = [UIColor orangeColor];
    [btn0 setTitle:DoraemonDemoLocalizedString(@"dataWithContentsOfURL") forState:UIControlStateNormal];
    [btn0 addTarget:self action:@selector(dataWithContentsOfURL) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn0];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn0.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:DoraemonDemoLocalizedString(@"SDWebImage") forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(sdWebImage) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:DoraemonDemoLocalizedString(@"uiWebView") forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(uiWebView) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    
    UIButton *btn3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn3.backgroundColor = [UIColor orangeColor];
    [btn3 setTitle:DoraemonDemoLocalizedString(@"wkWebView") forState:UIControlStateNormal];
    [btn3 addTarget:self action:@selector(wkWebView) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
}

- (void)dataWithContentsOfURL{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSURL *imageUrl = [NSURL URLWithString:self.picUrl];
        NSData *imageData = [NSData dataWithContentsOfURL:imageUrl];
        UIImage *result = [UIImage imageWithData:imageData];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self showImage:result];
        });
    });
}

- (void)sdWebImage{
//NSURL *imageUrl = [NSURL URLWithString:self.picUrl];
    [[SDWebImageManager sharedManager] loadImageWithURL:_picUrl options:SDWebImageRetryFailed progress:^(NSInteger receivedSize, NSInteger expectedSize, NSURL * _Nullable targetURL) {
        //
    } completed:^(UIImage * _Nullable image, NSData * _Nullable data, NSError * _Nullable error, SDImageCacheType cacheType, BOOL finished, NSURL * _Nullable imageURL) {
        //
        [self showImage:image];
    }];
}

- (void)uiWebView{
    UIViewController *vc = [[DoraemonUIWebViewViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)wkWebView{
    UIViewController *vc = [[DoraemonWKWebViewViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)showImage:(UIImage *)image{
    DoraemonDemoImageShowViewController *vc = [[DoraemonDemoImageShowViewController alloc] init];
    vc.image = image;
    [self.navigationController pushViewController:vc animated:YES];
}

@end
