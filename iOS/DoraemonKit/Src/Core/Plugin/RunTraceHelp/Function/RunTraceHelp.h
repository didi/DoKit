//
//  RunTraceHelp.h
//  RunTrace
//
//  Created by 孙昕 on 15/9/18.
//  Copyright (c) 2015年 孙昕. All rights reserved.
//

#import <UIKit/UIKit.h>

#define IS_IPhoneX (812.0 == [[UIScreen mainScreen] bounds].size.height ? YES : NO)
#define RUN_IPhoneX_TabBar_FixHeight (IS_IPhoneX ? 34 : 0) //UITabBar高度 iPhone X 偏差34
#define RUN_IPhoneX_NavBar_FixHeight (IS_IPhoneX ? 44 : 0) //NavBar高度 iPhone X 偏差34

#define RUN_TabBar_Height (IS_IPhoneX ? 83 : 49)//UITabBar高度 iPhone X = 83，其他49
#define RUN_NavBar_Height (IS_IPhoneX ? 88 : 64)//导航栏高度 iPhone X = 88，其他64

extern NSString *msgRunTraceSuperView;
extern NSString *msgRunTraceSubView;
extern NSString *msgRunTraceRemoveView;
extern NSString *msgRunTraceRemoveSubView;
extern NSString *msgRunTraceAddSubView;
extern NSString *msgRunTraceContraints;
extern NSString *msgRunTraceInfoPosition;
@interface RunTraceObject:NSObject
+(instancetype)objectWithWeak:(id)o;
@property (weak,nonatomic) id object;
@end
@interface RunTraceHelp : UIView

@property (strong, nonatomic) IBOutlet UILabel *lbAutoLayout;
@property (weak,nonatomic) UIView* viewHit;
- (IBAction)onClose:(id)sender;
@property (strong, nonatomic) IBOutlet UITableView *tableSuper;
@property (strong, nonatomic) IBOutlet UITableView *tableSub;
@property (strong, nonatomic) IBOutlet UIButton *btnClose;
@property (strong, nonatomic) IBOutlet UIButton *btnTrace;
- (IBAction)onTrace:(id)sender;
- (IBAction)onDetail:(id)sender;
@property (strong, nonatomic) IBOutlet UILabel *lbTip;
@property (strong, nonatomic) IBOutlet UIButton *btnAutoLayout;
- (IBAction)onAutoLayout:(id)sender;
@property (strong, nonatomic) IBOutlet UIButton *btnPosition;
- (IBAction)onPosition:(id)sender;
- (IBAction)onDonate:(id)sender;


@end
