//
//  DoraemonBaseViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "DoraemonBaseViewController.h"
#import "DoraemonNavBarItemModel.h"
#import "UIImage+DoraemonKit.h"
#import "DoraemonHomeWindow.h"

@interface DoraemonBaseViewController ()

@end

@implementation DoraemonBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    if ([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
    [self.navigationController.navigationBar setTranslucent:NO];
    
    
    DoraemonNavBarItemModel *leftModel = [[DoraemonNavBarItemModel alloc] initWithImage:[UIImage doraemon_imageNamed:@"doraemon_back"] selector:@selector(leftNavBackClick:)];
    
    [self setLeftNavBarItems:@[leftModel]];
    
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [[DoraemonHomeWindow shareInstance] makeKeyWindow];
}


- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

- (void)leftNavBackClick:(id)clickView{
    if (self.navigationController.viewControllers.count==1) {
        [[DoraemonHomeWindow shareInstance] hide];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
    
}

- (void)setLeftNavBarItems:(NSArray *)items{
    NSArray *barItems = [self navigationItems:items];
    if (barItems) {
        self.navigationItem.leftBarButtonItems = barItems;
    }
}

- (NSArray *)navigationItems:(NSArray *)items{
    NSMutableArray *barItems = [NSMutableArray array];
    //距离左右的间距
    UIBarButtonItem *spacer = [self getSpacerByWidth:-10];
    [barItems addObject:spacer];
    
    for (int i=0; i<items.count; i++) {
        
        DoraemonNavBarItemModel *model = items[i];
        UIBarButtonItem *barItem;
        if (model.type == DoraemonNavBarItemTypeText) {//文字按钮
            barItem = [[UIBarButtonItem alloc] initWithTitle:model.text style:UIBarButtonItemStylePlain target:self action:model.selector];
            barItem.tintColor = model.textColor;
        }else if(model.type == DoraemonNavBarItemTypeImage){//图片按钮
            UIImage *image = [model.image imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];//设置图片没有默认蓝色效果
            //默认的间距太大
            //            barItem = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:model.selector];
            
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            [btn setImage:image forState:UIControlStateNormal];
            [btn addTarget:self action:model.selector forControlEvents:UIControlEventTouchUpInside];
            btn.frame = CGRectMake(0, 0, 30, 30);
            btn.clipsToBounds = YES;
            barItem = [[UIBarButtonItem alloc] initWithCustomView:btn];
        }
        [barItems addObject:barItem];
    }
    return barItems;
}

/**
 * 获取间距
 */
- (UIBarButtonItem *)getSpacerByWidth : (CGFloat)width{
    UIBarButtonItem *spacer = [[UIBarButtonItem alloc]
                               initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace
                               target:nil action:nil];
    /**
     *  width为负数时，相当于btn向右移动width数值个像素，由于按钮本身和边界间距为5pix，所以width设为-5时，间距正好调整
     *  为0；width为正数时，正好相反，相当于往左移动width数值个像素
     */
    spacer.width = width;
    return spacer;
}

@end
