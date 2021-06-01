//
//  RelativePositionManager.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/26.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "RelativePositionManager.h"
#import "RelativePositionView.h"
#import "DoraemonDefine.h"
#import "DoraemonVisualInfoWindow.h"


@interface RelativePositionManager()

@property (nonatomic, strong) RelativePositionView *viewCheckViewA;
@property (nonatomic, strong) RelativePositionView *viewCheckViewB;

@property (nonatomic, strong) DoraemonVisualInfoWindow *infoWindow;//顶部被探测到的view的信息显示的UIwindow

@end

@implementation RelativePositionManager

+ (RelativePositionManager *)shareInstance{
    static dispatch_once_t once;
    static RelativePositionManager *instance;
    //只执行一次block
    dispatch_once(&once, ^{
        instance = [[RelativePositionManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
        [[DoraemonUtil getKeyWindow] addObserver:self forKeyPath:@"rootViewController" options:NSKeyValueObservingOptionNew context:nil];
        
        CGRect infoWindowFrame = CGRectZero;
        if (kInterfaceOrientationPortrait) {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30), DoraemonScreenWidth - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        } else {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        }
        _infoWindow = [[DoraemonVisualInfoWindow alloc] initWithFrame:infoWindowFrame];
    }
    return self;
}

- (void)dealloc {
    [[DoraemonUtil getKeyWindow] removeObserver:self forKeyPath:@"rootViewController"];
}

- (void)show{
    if (!_viewCheckViewA) {
        _viewCheckViewA = [[RelativePositionView alloc] initWithLocation:CGPointMake(-30, 0)];
        _viewCheckViewA.hidden = YES;
        [[DoraemonUtil getKeyWindow] addSubview:_viewCheckViewA];
    }
    if (!_viewCheckViewB) {
        _viewCheckViewB = [[RelativePositionView alloc] initWithLocation:CGPointMake(30, 0)];
        _viewCheckViewB.hidden = YES;
        [[DoraemonUtil getKeyWindow] addSubview:_viewCheckViewB];
    }
    [_viewCheckViewA show];
    [_viewCheckViewB show];
    _infoWindow.hidden = NO;
}

- (void)hidden{
    [_viewCheckViewA hide];
    [_viewCheckViewB hide];
    _infoWindow.hidden = YES;
}

- (void)closePlugin:(NSNotification *)notification{
    [self hidden];
}


//监听对象属性变更，变更时则处理时间，把viewCheckView放在最前面
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.viewCheckViewA];
    [[DoraemonUtil getKeyWindow] bringSubviewToFront:self.viewCheckViewB];
}

- (void)refresh{
    NSMutableAttributedString* hexstring1=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"左边距:%0.1lf ",
                              self.viewCheckViewA.viewFrame.origin.x
                              -self.viewCheckViewB.viewFrame.origin.x
                              ]];
    NSMutableAttributedString*
    hexstring2=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"右边距:%0.1lf ",
                              self.viewCheckViewA.viewFrame.origin.x
                              +self.viewCheckViewA.viewFrame.size.width
                              -self.viewCheckViewB.viewFrame.origin.x
                              -self.viewCheckViewB.viewFrame.size.width
                              ]];
    NSMutableAttributedString*
    hexstring3=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"上边距:%0.1lf ",
                              self.viewCheckViewA.viewFrame.origin.y
                              -self.viewCheckViewB.viewFrame.origin.y
                              ]];
    NSMutableAttributedString*
    hexstring4=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"下边距:%0.1lf\n",
                              self.viewCheckViewA.viewFrame.origin.y
                              +self.viewCheckViewA.viewFrame.size.height
                              -self.viewCheckViewB.viewFrame.origin.y
                              -self.viewCheckViewB.viewFrame.size.height
                              ]];
    
    NSMutableAttributedString*
    hexstring5=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"相对面积:%0.1lf\n",
                              self.viewCheckViewA.viewFrame.size.width
                              *self.viewCheckViewA.viewFrame.size.height
                              /self.viewCheckViewB.viewFrame.size.width
                              /self.viewCheckViewB.viewFrame.size.height
                              ]];
    
    
    
    NSMutableAttributedString*
    hexstring6=[[NSMutableAttributedString alloc] initWithString:
                             [NSString stringWithFormat:@"A控件位置：左%0.1lf  上%0.1lf  宽%0.1lf  高%0.1lf\nB控件位置：左%0.1lf  上%0.1lf  宽%0.1lf  高%0.1lf",
                              self.viewCheckViewA.viewFrame.origin.x,
                              self.viewCheckViewA.viewFrame.origin.y,
                              self.viewCheckViewA.viewFrame.size.width,
                              self.viewCheckViewA.viewFrame.size.height,
                              self.viewCheckViewB.viewFrame.origin.x,
                              self.viewCheckViewB.viewFrame.origin.y,
                              self.viewCheckViewB.viewFrame.size.width,
                              self.viewCheckViewB.viewFrame.size.height]];
    
    [hexstring1 appendAttributedString:hexstring2];
    [hexstring1 appendAttributedString:hexstring3];
    [hexstring1 appendAttributedString:hexstring4];
    [hexstring1 appendAttributedString:hexstring5];
    [hexstring1 appendAttributedString:hexstring6];
//    NSAttributedString* viewInfoText =
    self.infoWindow.infoAttributedText = hexstring1;
}

@end
