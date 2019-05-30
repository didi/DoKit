//
//  DoraemonOscillogramViewController.h
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/4.
//

#import <UIKit/UIKit.h>
#import "DoraemonOscillogramView.h"

@interface DoraemonOscillogramViewController : UIViewController

@property (nonatomic, strong) DoraemonOscillogramView *oscillogramView;
@property (nonatomic, strong) UIButton *closeBtn;

- (NSString *)title;
- (NSString *)lowValue;
- (NSString *)highValue;
- (void)closeBtnClick;
- (void)startRecord;
- (void)endRecord;
- (void)doSecondFunction;

@end
