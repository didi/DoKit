//
//  DoraemonBaseSwitchViewController.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import <DoraemonKit/DoraemonKit.h>
#import "DoraemonBaseViewController.h"

@interface DoraemonBaseSwitchViewController : DoraemonBaseViewController

@property (nonatomic, copy) NSArray *dataSourceArray;

- (BOOL)switchViewOn;

- (void)switchAction:(id)sender;

- (NSArray *)getLocalRecords;

- (void)refreshTableView;

- (void)didSelectedItemWithMessage:(NSString *)message;

@end
