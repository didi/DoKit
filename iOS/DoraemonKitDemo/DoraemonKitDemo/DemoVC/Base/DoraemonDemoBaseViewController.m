//
//  DoraemonDemoBaseViewController.m
//  DoraemonKitDemo
//
//  Created by didi on 2020/3/4.
//  Copyright © 2020 yixiang. All rights reserved.
//

#import "DoraemonDemoBaseViewController.h"
#import <DoraemonKit/DoraemonDefine.h>
#import <DoraemonKit/DoraemonNavBarItemModel.h>

@interface DoraemonDemoBaseViewController ()

@property (nonatomic, strong) DoraemonNavBarItemModel *leftModel;

@end

@implementation DoraemonDemoBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    UIImage *image = [UIImage doraemon_xcassetImageNamed:@"doraemon_back"];
    self.leftModel = [[DoraemonNavBarItemModel alloc] initWithImage:image selector:@selector(leftNavBackClick:)];
    [self setLeftNavBarItems:@[self.leftModel]];
    
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
    
    for (NSInteger i=0; i<items.count; i++) {
        
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
        }else{
            barItem = [[UIBarButtonItem alloc] init];
        }
        [barItems addObject:barItem];
    }
    return barItems;
}

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

- (void)leftNavBackClick:(id)clickView{
    [self.navigationController popViewControllerAnimated:YES];
}

@end
