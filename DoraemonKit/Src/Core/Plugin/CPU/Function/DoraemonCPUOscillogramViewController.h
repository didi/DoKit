//
//  DoraemonCPUOscillogramViewController.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import <DoraemonKit/DoraemonKit.h>
#import "DoraemonOscillogramViewController.h"

@interface DoraemonCPUOscillogramViewController : DoraemonOscillogramViewController

- (void)addRecortArray:(NSArray<DoraemonPerformanceInfoModel *> *)recordArray;

@end
