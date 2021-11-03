//
//  DoraemonBaseViewController.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonBaseBigTitleView.h"

@interface DoraemonBaseViewController : UIViewController

//是否需要大标题，默认不需要
- (BOOL)needBigTitleView;
@property (nonatomic, strong) DoraemonBaseBigTitleView *bigTitleView;

- (void)setLeftNavBarItems:(NSArray *)items;
- (void)leftNavBackClick:(id)clickView;
- (void)setRightNavTitle:(NSString *)title;
- (void)rightNavTitleClick:(id)clickView;
- (void)setRightNavBarItems:(NSArray *)items;

@end
