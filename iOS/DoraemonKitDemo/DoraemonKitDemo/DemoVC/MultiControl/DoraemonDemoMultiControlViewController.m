//
//  DoraemonDemoMultiControlViewController.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/14.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiControlViewController.h"
#import <DoraemonKit/UIView+Doraemon.h>
#import "DoraemonDefine.h"
#import "DoraemonHealthAlertView.h"
#import "DoraemonDemoMultiConLongPressGesture.h"
#import "DoraemonDemoMultiConPinchGesture.h"
#import "DoraemonDemoMultiConRotationGesture.h"
#import "DoraemonDemoMultiConSwipeGesture.h"
#import "DoraemonDemoMultiConTapGesture.h"
#import "DoraemonDemoMultiConScreenEdgePanGesture.h"
#import "DoraemonMCCommandExcutor.h"
#import "DoraemonMCMessagePackager.h"
#import "DoraemonDemoMultiSlideView.h"
@interface DoraemonMCEventHandler1: DoraemonMCEventHandler



@end

@implementation DoraemonMCEventHandler1

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
    
    self.messageInfo = eventInfo;
    self.targetView = [self fetchTargetView];
    NSString *message =  [NSString stringWithFormat:@"%@,%@",self.targetView, [self.messageInfo.eventInfo objectForKey:@"eventInfo"]];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"自定义手势" message:message
       delegate:nil cancelButtonTitle:@"好的" otherButtonTitles: nil];
    [alert show];
    
    return YES;
}

@end



@interface DoraemonMCEventHandler2: DoraemonMCEventHandler



@end

@implementation DoraemonMCEventHandler2

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo {
    
    self.messageInfo = eventInfo;
    self.targetView = [self fetchTargetView];
    NSString *message =  [NSString stringWithFormat:@"%@,%@",self.targetView, [self.messageInfo.eventInfo objectForKey:@"eventInfo"]];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"自定义手势" message:message
       delegate:nil cancelButtonTitle:@"好的" otherButtonTitles: nil];
    [alert show];
    
    return YES;
}

@end


@interface DoraemonDemoMultiControlViewController ()
@property (nonatomic,strong) UIScrollView * superScrollView;
@end

@implementation DoraemonDemoMultiControlViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"一机多控Demo");
    
    self.superScrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
    self.superScrollView.contentSize = CGSizeMake(self.view.doraemon_width, self.view.doraemon_height*1.3);
    [self.view addSubview:self.superScrollView];
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 系统 AlertView") forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(controlEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:btn];
    
    

    
    UIButton *btn_2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn_2.backgroundColor = [UIColor orangeColor];
    [btn_2 setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 系统 UIAlertController") forState:UIControlStateNormal];
    [btn_2 addTarget:self action:@selector(controlEvent2) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:btn_2];
    
    
    UIButton *btn_3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn_2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn_3.backgroundColor = [UIColor orangeColor];
    [btn_3 setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 自定义Alertview") forState:UIControlStateNormal];
    [btn_3 addTarget:self action:@selector(controlEvent3) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:btn_3];

    // 自定义事件
    DoraemonMCEventHandler1 *customHandler1 = [DoraemonMCEventHandler1 new];
    [DoraemonMCCommandExcutor addCustomMessage:@"customType1" eventHandlerName:customHandler1];
    
    
    DoraemonMCEventHandler2 *customHandler2 = [DoraemonMCEventHandler2 new];
    [DoraemonMCCommandExcutor addCustomMessage:@"customType2" eventHandlerName:customHandler2];
    
    
    DoraemonDemoMultiSlideView  *slideView  = [[DoraemonDemoMultiSlideView alloc]initWithFrame:CGRectMake(30, btn_3.doraemon_bottom +20, btn_3.doraemon_width-60, 60)];
    [self.superScrollView addSubview:slideView];
    

    //输入文本
    UITextField *field_1 = [[UITextField alloc] initWithFrame:CGRectMake(60, slideView.doraemon_bottom+20, self.view.doraemon_width-120, 60)];
    field_1.placeholder = @"输入文案测试";
    field_1.clearButtonMode = UITextFieldViewModeAlways;
    field_1.backgroundColor = [UIColor whiteColor];
    [self.superScrollView addSubview:field_1];
    
    
    //输入 UITextView 文本
    UITextView  *textView_1 = [[UITextView alloc]initWithFrame:CGRectMake(60, field_1.doraemon_bottom+20, self.view.doraemon_width-120, 60)];
    textView_1.layer.borderWidth = 0.5;
    textView_1.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    textView_1.backgroundColor = [UIColor whiteColor];
    [self.superScrollView addSubview:textView_1];
    
    //输入 UITextView 文本
    UITextView  *textView_2 = [[UITextView alloc]initWithFrame:CGRectMake(60, textView_1.doraemon_bottom+20, self.view.doraemon_width-120, 60)];
    textView_2.layer.borderWidth = 0.5;
    textView_2.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    textView_2.backgroundColor = [UIColor whiteColor];
    [self.superScrollView addSubview:textView_2];
    
    //长按
    UIButton *longPress = [[UIButton alloc] initWithFrame:CGRectMake(0, textView_2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    longPress.backgroundColor = [UIColor orangeColor];
    [longPress setTitle:DoraemonDemoLocalizedString(@"长安点击事件") forState:UIControlStateNormal];
    [longPress addTarget:self action:@selector(longPressEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:longPress];
    
    //捏合 UIPinchGestureRecognizer
    UIButton *pinch = [[UIButton alloc] initWithFrame:CGRectMake(0, longPress.doraemon_bottom+20, self.view.doraemon_width, 60)];
    pinch.backgroundColor = [UIColor orangeColor];
    [pinch setTitle:DoraemonDemoLocalizedString(@"拟合事件") forState:UIControlStateNormal];
    [pinch addTarget:self action:@selector(pinchEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:pinch];
    
    
    //旋转事件
    UIButton *rotation = [[UIButton alloc] initWithFrame:CGRectMake(0, pinch.doraemon_bottom+20, self.view.doraemon_width, 60)];
    rotation.backgroundColor = [UIColor orangeColor];
    [rotation setTitle:DoraemonDemoLocalizedString(@"旋转事件") forState:UIControlStateNormal];
    [rotation addTarget:self action:@selector(rotationEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:rotation];
    
    
    // 单击  UITapGestureRecognizer
    UIButton *tap = [[UIButton alloc] initWithFrame:CGRectMake(0, rotation.doraemon_bottom+20, self.view.doraemon_width, 60)];
    tap.backgroundColor = [UIColor orangeColor];
    [tap setTitle:DoraemonDemoLocalizedString(@"单击事件") forState:UIControlStateNormal];
    [tap addTarget:self action:@selector(tapEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:tap];
    
    //滑动手势 UISwipeGestureRecognizer
    UIButton *swipe = [[UIButton alloc] initWithFrame:CGRectMake(0, tap.doraemon_bottom+20, self.view.doraemon_width, 60)];
    swipe.backgroundColor = [UIColor orangeColor];
    [swipe setTitle:DoraemonDemoLocalizedString(@"滑动手势事件") forState:UIControlStateNormal];
    [swipe addTarget:self action:@selector(swipeEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:swipe];
    
    //边缘手势  UIScreenEdgePanGestureRecognizer
    UIButton *screenEdgePan = [[UIButton alloc] initWithFrame:CGRectMake(0, swipe.doraemon_bottom+20, self.view.doraemon_width, 60)];
    screenEdgePan.backgroundColor = [UIColor orangeColor];
    [screenEdgePan setTitle:DoraemonDemoLocalizedString(@"边缘手势事件") forState:UIControlStateNormal];
    [screenEdgePan addTarget:self action:@selector(screenEdgePanEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.superScrollView addSubview:screenEdgePan];
    

    
    
    
}

- (void)controlEvent{
     UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"点击事件测试" message:@"我是普通警告框"
        delegate:nil cancelButtonTitle:@"好的" otherButtonTitles: nil];
     [alert show];

}

- (void)controlEvent2{
    [self showAlertMessage];
}

- (void)controlEvent3{
    DoraemonHealthAlertView *alertView = [[DoraemonHealthAlertView alloc] init];
    [alertView renderUI:DoraemonLocalizedString(@"UIControl 点击事件 自定义Alertview") placeholder:@[] inputTip:@[DoraemonLocalizedString(@"测试用例名称"),DoraemonLocalizedString(@"测试人名称")] ok:DoraemonLocalizedString(@"提交") quit:DoraemonLocalizedString(@"丢弃") cancle:DoraemonLocalizedString(@"取消") okBlock:^{


    } quitBlock:^{

    } cancleBlock:^{

    }];

    [self.view addSubview:alertView];
}

- (void)showAlertMessage{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"提示" message:[NSString stringWithFormat:@"当前设备为模拟器，不支持扫一扫功能"] preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    
    }];
    [alertController addAction:sureAction];
    
    [self.navigationController presentViewController:alertController animated:YES completion:nil];
}


- (void)longPressEvent{
    DoraemonDemoMultiConLongPressGesture *longPress = [[DoraemonDemoMultiConLongPressGesture alloc]init];
    [self.navigationController pushViewController:longPress animated:YES];
}

- (void)pinchEvent {
    DoraemonDemoMultiConPinchGesture *pinch = [[DoraemonDemoMultiConPinchGesture alloc]init];
    [self.navigationController pushViewController:pinch animated:YES];
}
- (void)rotationEvent {
    DoraemonDemoMultiConRotationGesture *rotation = [[DoraemonDemoMultiConRotationGesture alloc]init];
    [self.navigationController pushViewController:rotation animated:YES];
}

-(void)tapEvent {
    DoraemonDemoMultiConTapGesture *tap = [[DoraemonDemoMultiConTapGesture alloc]init];
    [self.navigationController pushViewController:tap animated:YES];
    
}

-(void)swipeEvent {
    DoraemonDemoMultiConSwipeGesture *swipe = [[DoraemonDemoMultiConSwipeGesture alloc]init];
    [self.navigationController pushViewController:swipe animated:YES];
}

-(void)screenEdgePanEvent {
    DoraemonDemoMultiConScreenEdgePanGesture *screenEdgePan = [[DoraemonDemoMultiConScreenEdgePanGesture alloc]init];
    [self.navigationController pushViewController:screenEdgePan animated:YES];
}
@end


