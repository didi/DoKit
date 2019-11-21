//
//  DoraemonDemoMemoryLeakModel.m
//  DoraemonKitDemo
//
//  Created by didi on 2019/10/6.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import "DoraemonDemoMemoryLeakModel.h"

@interface DoraemonDemoMemoryLeakModel()

@property (nonatomic, copy) DoraemonDemoMemoryLeakModelBlock block;

@end

@implementation DoraemonDemoMemoryLeakModel

- (void)addBlock:(DoraemonDemoMemoryLeakModelBlock)block{
    self.block = block;
}

- (void)install{
    self.block();
}

@end
