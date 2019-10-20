//
//  DoraemonDemoMemoryLeakView.m
//  DoraemonKitDemo
//
//  Created by didi on 2019/10/6.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import "DoraemonDemoMemoryLeakView.h"
#import "DoraemonDemoMemoryLeakModel.h"

@interface DoraemonDemoMemoryLeakView()

@property (nonatomic, strong) DoraemonDemoMemoryLeakModel *model;

@end

@implementation DoraemonDemoMemoryLeakView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor redColor];
    
        _model = [[DoraemonDemoMemoryLeakModel alloc] init];
        [_model addBlock:^{
            [self printXX];
        }];
        [_model install];
    }
    return self;
}

- (void)printXX {
    NSLog(@"view XX");
}

- (void)dealloc {
    NSLog(@"DoraemonDemoMemoryLeakView dealloc");
}

@end
