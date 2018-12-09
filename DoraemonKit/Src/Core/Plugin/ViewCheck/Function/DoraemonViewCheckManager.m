//
//  DoraemonViewCheckManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import "DoraemonViewCheckManager.h"
#import "DoraemonViewCheckView.h"
#import "DoraemonDefine.h"

@interface DoraemonViewCheckManager()

@property (nonatomic, strong) DoraemonViewCheckView *viewCheckView;

@end

@implementation DoraemonViewCheckManager

+ (DoraemonViewCheckManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonViewCheckManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonViewCheckManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
    }
    return self;
}


- (void)show{
    if (!_viewCheckView) {
        _viewCheckView = [[DoraemonViewCheckView alloc] init];
        _viewCheckView.hidden = YES;
        UIWindow *delegateWindow = [[UIApplication sharedApplication].delegate window];
        [delegateWindow addSubview:_viewCheckView];
    }
    [_viewCheckView show];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonShowPluginNotification object:nil userInfo:nil];
}

- (void)hidden{
    [_viewCheckView hide];
}

- (void)closePlugin:(NSNotification *)notification{
    [self hidden];
}

@end
