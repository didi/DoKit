//
//  DoraemonViewAPluginManager.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/2.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonViewAPluginManager.h"
#import "DoraemonViewAPlugin.h"
#import "DoraemonViewBPlugin.h"
#import "DoraemonDefine.h"


@interface DoraemonViewAPluginManager()

@property (nonatomic, strong) DoraemonViewAPlugin *viewCheckViewA;
@property (nonatomic, strong) DoraemonViewBPlugin *viewCheckViewB;


@end

@implementation DoraemonViewAPluginManager

+ (instancetype)shareInstance {
    static id _shareInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _shareInstance = [[self alloc] init];
        
    });
    return _shareInstance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
        [[DoraemonUtil getKeyWindow] addObserver:self forKeyPath:@"rootViewController" options:NSKeyValueObservingOptionNew context:nil];
    }
    return self;
}

- (void)dealloc {
    [[DoraemonUtil getKeyWindow] removeObserver:self forKeyPath:@"rootViewController"];
}

- (void)showA{
    if (!_viewCheckViewA) {
        _viewCheckViewA = [DoraemonViewAPlugin shareInstance];
        _viewCheckViewA.hidden = YES;
        [[DoraemonUtil getKeyWindow] addSubview:_viewCheckViewA];
    }
    [_viewCheckViewA show];
}

- (void)showB{
    if (!_viewCheckViewB) {
        _viewCheckViewB = [[DoraemonViewBPlugin alloc] init];
        _viewCheckViewB.hidden = YES;
        [[DoraemonUtil getKeyWindow] addSubview:_viewCheckViewB];
    }
    [_viewCheckViewB show];
}

- (void)hiddenA{
    [_viewCheckViewA hide];
}
- (void)hiddenB{
    [_viewCheckViewB hide];
}

- (void)closePlugin:(NSNotification *)notification{
    [self hiddenA];
    [self hiddenB];
}


//监听对象属性变更，变更时则处理事件，把viewCheckView放在最前面
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.viewCheckViewA];
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.viewCheckViewB];
}

@end
