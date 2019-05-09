//
//  DoraemonRunTraceHelpManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by S S on 2019/5/9.
//

#import "DoraemonRunTraceHelpManager.h"
#import "RunTrace.h"
#import "DoraemonDefine.h"

@interface DoraemonRunTraceHelpManager()

@property (nonatomic, strong) RunTrace *viewCheckView;

@end

@implementation DoraemonRunTraceHelpManager

+ (DoraemonRunTraceHelpManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonRunTraceHelpManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonRunTraceHelpManager alloc] init];
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
        _viewCheckView = [[RunTrace alloc] init];
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
