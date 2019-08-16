//
//  DoraemonOscillogramWindow.h
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/3.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "DoraemonOscillogramViewController.h"

@protocol DoraemonOscillogramWindowDelegate <NSObject>

- (void)doraemonOscillogramWindowClosed;

@end

@interface DoraemonOscillogramWindow : UIWindow

+ (DoraemonOscillogramWindow *)shareInstance;

@property (nonatomic, strong) DoraemonOscillogramViewController *vc;

//需要子类重写
- (void)addRootVc;

- (void)show;

- (void)hide;

- (void)addDelegate:(id<DoraemonOscillogramWindowDelegate>) delegate;

- (void)removeDelegate:(id<DoraemonOscillogramWindowDelegate>)delegate;

@end
