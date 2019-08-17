//
//  DoraemonViewAlignManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonViewAlignManager.h"
#import "DoraemonDefine.h"
#import "DoraemonViewAlignView.h"

#define kDelegateWindow [[UIApplication sharedApplication].delegate window]

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
        [kDelegateWindow addObserver:self forKeyPath:@"rootViewController" options:NSKeyValueObservingOptionNew context:nil];
    }
    return self;
}

- (void)dealloc {
     [kDelegateWindow removeObserver:self forKeyPath:@"rootViewController"];
}

- (void)show{
    if (!_alignView) {
        _alignView = [[DoraemonViewAlignView alloc] init];
//        _alignView.hidden = YES;
        [_alignView hide];
        [kDelegateWindow addSubview:_alignView];
    }
//    _alignView.hidden = NO;
    [_alignView show];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonShowPluginNotification object:nil userInfo:nil];
}

- (void)hidden{
//    _alignView.hidden = YES;
    [_alignView hide];
}

- (void)closePlugin:(NSNotification *)notification{
    [self hidden];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    [kDelegateWindow bringSubviewToFront:self.alignView];
}

@end
