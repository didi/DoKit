//
//  TestPlugin.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "TestPlugin.h"


@implementation TestPlugin

- (void)pluginDidLoad{
    NSLog(@"TestPlugin pluginDidLoad");
}

- (void)pluginDidLoad:(NSDictionary *)itemData{
    NSLog(@"TestPlugin pluginDidLoad:itemData = %@",itemData);
}

@end
