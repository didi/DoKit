//
//  DoraemonWeexInfoAnalyzer.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/4.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoAnalyzer.h"
#import "DoraemonWeexInfoDataManager.h"

@implementation DoraemonWeexInfoAnalyzer

- (void)transfer:(NSDictionary *) value{
    [[DoraemonWeexInfoDataManager shareInstance] formatInfo:value];
}

@end
