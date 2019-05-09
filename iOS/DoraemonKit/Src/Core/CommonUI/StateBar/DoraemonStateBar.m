//
//  DoraemonStateBar.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/7.
//

#import "DoraemonStateBar.h"
#import "DoraemonDefine.h"

@interface DoraemonStateBar()

@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, assign) DoraemonStateBarFrom from;

@end

@implementation DoraemonStateBar

+ (DoraemonStateBar *)shareInstance{
    static dispatch_once_t once;
    static DoraemonStateBar *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonStateBar alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.windowLevel = UIWindowLevelStatusBar + 1.f;
        self.backgroundColor = [UIColor doraemon_colorWithString:@"#427dbe"];
        
        _contentLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(20)];
        _contentLabel.textColor = [UIColor whiteColor];
        _contentLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_contentLabel];
        
        _contentLabel.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapView)];
        [_contentLabel addGestureRecognizer:tap];
    }
    return self;
}

- (void)becomeKeyWindow{
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

- (void)show{
    self.hidden = NO;
    self.frame = CGRectMake(0, 0, DoraemonScreenWidth, IPHONE_STATUSBAR_HEIGHT);
    if(IS_IPHONE_X_Series){
        _contentLabel.frame = CGRectMake(0, self.doraemon_height-20, DoraemonScreenWidth, 20);
    }else{
        _contentLabel.frame = CGRectMake(0, 0, DoraemonScreenWidth, IPHONE_STATUSBAR_HEIGHT);
    }
}

- (void)hide{
    self.hidden = YES;
}

- (void)renderUIWithContent:(NSString *)content from:(DoraemonStateBarFrom)from{
    _contentLabel.text = content;
    _from = from;
}

- (void)tapView{
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonQuickOpenLogVCNotification object:nil userInfo:@{@"from":@(_from)}];
}


@end
