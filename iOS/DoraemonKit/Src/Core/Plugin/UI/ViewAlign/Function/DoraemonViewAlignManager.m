//
//  DoraemonViewAlignManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonViewAlignManager.h"
#import "DoraemonDefine.h"
#import "DoraemonViewAlignView.h"


@interface DoraemonViewAlignManager()


//持有2个UIVIEW，1个UIIMAGE,4个UILabel,管理其显示和隐藏等
@property (nonatomic, strong) DoraemonViewAlignView *alignView;

@end

@implementation DoraemonViewAlignManager

//维护单例
+ (DoraemonViewAlignManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonViewAlignManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonViewAlignManager alloc] init];
    });
    return instance;
}


//1.监听被关闭的事件，2.监听rootCV被更改的事件，目标是把其持有的alignView提到最前面
- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
        [[DoraemonUtil getKeyWindow] addObserver:self forKeyPath:@"rootViewController" options:NSKeyValueObservingOptionNew context:nil];
    }
    return self;
}


//防止内存泄漏
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


//把alignView放到最前面
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.alignView];
}

@end
