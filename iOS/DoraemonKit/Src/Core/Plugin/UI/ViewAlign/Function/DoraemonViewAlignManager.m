//
//  DoraemonViewAlignManager.m
//  DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonViewAlignManager.h"
#import "DoraemonDefine.h"
#import "DoraemonViewAlignView.h"


@interface DoraemonViewAlignManager()

@property (nonatomic, strong) DoraemonViewAlignView *alignView;

@end

@implementation DoraemonViewAlignManager

+ (DoraemonViewAlignManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonViewAlignManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonViewAlignManager alloc] init];
    });
    return instance;
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

- (void)show{
    if (!_alignView) {
        _alignView = [[DoraemonViewAlignView alloc] init];
//        _alignView.hidden = YES;
        [_alignView hide];
        [[DoraemonUtil getKeyWindow] addSubview:_alignView];
    }
//    _alignView.hidden = NO;
    [_alignView show];
}

- (void)hidden{
//    _alignView.hidden = YES;
    [_alignView hide];
}

- (void)closePlugin:(NSNotification *)notification{
    [self hidden];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.alignView];
}

@end
