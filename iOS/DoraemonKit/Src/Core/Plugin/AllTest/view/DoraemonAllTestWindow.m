//
//  DoraemonAllTestWindow.m
//  AFNetworking
//
//  Created by didi on 2019/10/9.
//

#import "DoraemonAllTestWindow.h"
#import "DoraemonDefine.h"
#import "UIView+Doraemon.h"
#import "DoraemonAllTestManager.h"

#import "DoraemonStatusBarViewController.h"

@interface DoraemonAllTestWindow()

@property (nonatomic, assign) CGFloat showViewSize;
@property (nonatomic, strong) UILabel *memoryValue;
@property (nonatomic, strong) UILabel *cpuValue;
@property (nonatomic, strong) UILabel *fpsValue;
@property (nonatomic, strong) UILabel *upFlowValue;
@property (nonatomic, strong) UILabel *downFlowValue;
@property (nonatomic, strong) UIButton *closeBtn;
@property (nonatomic, strong) NSHashTable *delegates;

@property(nonatomic, assign) int height;
@property(nonatomic, assign) int width;
@property(nonatomic, assign) int fontSize;
@property(nonatomic, assign) int nextX;
@property(nonatomic, strong) UIColor *fontColor;

@end

@implementation DoraemonAllTestWindow

+ (DoraemonAllTestWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonAllTestWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonAllTestWindow alloc] init];
        [instance _initialization];
    });
    return instance;
}

- (instancetype)init{
    _showViewSize = kDoraemonSizeFrom750_Landscape(200);
    CGFloat x = self.startingPosition.x;
    CGFloat y = self.startingPosition.y;
    CGPoint defaultPosition = DoraemonStartingPosition;
    if (x < 0 || x > (DoraemonScreenWidth - _showViewSize*2.5)) {
        x = defaultPosition.x;
    }
    if (y <= 0 || y > (DoraemonScreenHeight - _showViewSize)) {
        y = _showViewSize/4;
    }
    
    self = [super initWithFrame:CGRectMake(x, y, _showViewSize*2.5, _showViewSize)];
    if (self) {
        self.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.33];
        self.windowLevel = UIWindowLevelStatusBar + 50;
        self.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
        self.layer.masksToBounds = YES;//保证显示
        NSString *version= [UIDevice currentDevice].systemVersion;
        if(version.doubleValue >=10.0) {
            if (!self.rootViewController) {
                self.rootViewController = [[UIViewController alloc] init];
            }
        }else{
            //iOS9.0的系统中，新建的window设置的rootViewController默认没有显示状态栏
            if (!self.rootViewController) {
                self.rootViewController = [[DoraemonStatusBarViewController alloc] init];
            }
        }
        UIPanGestureRecognizer *move = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(_move:)];
        [self addGestureRecognizer:move];
    }
    return self;
}

- (void)_initialization{
    _nextX = kDoraemonSizeFrom750_Landscape(30);
    _height = kDoraemonSizeFrom750_Landscape(50);
    _width = self.doraemon_width/5*2;
    _fontSize = kDoraemonSizeFrom750_Landscape(24);
    _fontColor = [UIColor whiteColor];
    [self.rootViewController.view addSubview:self.memoryValue];
    [self.rootViewController.view addSubview:self.cpuValue];
    [self.rootViewController.view addSubview:self.fpsValue];
    _nextX += _memoryValue.doraemon_width;
    _width = self.doraemon_width - _width;
    [self.rootViewController.view addSubview:self.upFlowValue];
    [self.rootViewController.view addSubview:self.downFlowValue];
    [self.rootViewController.view addSubview:self.closeBtn];
}

- (void)closeBtnClick{
    [self hide];
    [DoraemonAllTestManager shareInstance].realTimeSwitchOn = false;
}

- (NSHashTable *)delegates {
    if (_delegates == nil) {
        self.delegates = [NSHashTable weakObjectsHashTable];
    }
    return _delegates;
}

- (void)addDelegate:(id<DoraemonAllTestWindowDelegate>) delegate {
    [self.delegates addObject:delegate];
}

- (void)hide{
    self.hidden = YES;
    for (id<DoraemonAllTestWindowDelegate> delegate in self.delegates) {
        [delegate doraemonAllTestWindowClosed];
    }
}


- (UIButton *)closeBtn{
    _closeBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.doraemon_width-_height, 0, _height, _height)];
    [_closeBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close_white"] forState:UIControlStateNormal];
    [_closeBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _closeBtn.backgroundColor = [UIColor clearColor];
    return _closeBtn;
}

- (UILabel *)memoryValue {
    if (!_memoryValue) {
        _memoryValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, _height/2, _width, _height)];
        _memoryValue.textAlignment = NSTextAlignmentLeft;
        _memoryValue.font = [UIFont systemFontOfSize:_fontSize];
        _memoryValue.textColor = _fontColor;
        _memoryValue.adjustsFontSizeToFitWidth = YES;
    }
    return _memoryValue;
}

- (UILabel *)cpuValue {
    if (!_cpuValue) {
        _cpuValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, _memoryValue.doraemon_bottom, _width, _height)];
        _cpuValue.textAlignment = NSTextAlignmentLeft;
        _cpuValue.font = [UIFont systemFontOfSize:_fontSize];
        _cpuValue.textColor = _fontColor;
        _cpuValue.adjustsFontSizeToFitWidth = YES;
    }
    return _cpuValue;
}

- (UILabel *)fpsValue {
    if (!_fpsValue) {
        _fpsValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, _cpuValue.doraemon_bottom, _width, _height)];
        _fpsValue.textAlignment = NSTextAlignmentLeft;
        _fpsValue.font = [UIFont systemFontOfSize:_fontSize];
        _fpsValue.textColor = _fontColor;
        _fpsValue.adjustsFontSizeToFitWidth = YES;
    }
    return _fpsValue;
}

- (UILabel *)upFlowValue {
    if (!_upFlowValue) {
        _upFlowValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, _height/2, _width*2, _height)];
        _upFlowValue.textAlignment = NSTextAlignmentLeft;
        _upFlowValue.font = [UIFont systemFontOfSize:_fontSize];
        _upFlowValue.textColor = _fontColor;
        _upFlowValue.adjustsFontSizeToFitWidth = YES;
    }
    return _upFlowValue;
}

- (UILabel *)downFlowValue {
    if (!_downFlowValue) {
        _downFlowValue = [[UILabel alloc] initWithFrame:CGRectMake(_nextX, _upFlowValue.doraemon_bottom, _width*2, _height)];
        _downFlowValue.textAlignment = NSTextAlignmentLeft;
        _downFlowValue.font = [UIFont systemFontOfSize:_fontSize];
        _downFlowValue.textColor = _fontColor;
        _downFlowValue.adjustsFontSizeToFitWidth = YES;
    }
    return _downFlowValue;
}

- (void)_move:(UIPanGestureRecognizer *)sender{
    //1、获得拖动位移
    CGPoint offsetPoint = [sender translationInView:sender.view];
    //2、清空拖动位移
    [sender setTranslation:CGPointZero inView:sender.view];
    //3、重新设置控件位置
    UIView *panView = sender.view;
    CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
    CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
    if (newX < _showViewSize) {
        newX = _showViewSize;
    }
    if (newX > DoraemonScreenWidth - _showViewSize) {
        newX = DoraemonScreenWidth - _showViewSize;
    }
    if (newY < _showViewSize/2) {
        newY = _showViewSize/2;
    }
    if (newY > DoraemonScreenHeight - _showViewSize/2) {
        newY = DoraemonScreenHeight - _showViewSize/2;
    }
    panView.center = CGPointMake(newX, newY);
}

-(void)updateCommonValue:(NSString *)memory cpu:(NSString *)cpu fps:(NSString *)fps{
    if(self.hidden)
        return;
    _memoryValue.text = memory;
    _cpuValue.text = cpu;
    _fpsValue.text = fps;
}
-(void)updateFlowValue:(NSString *)upFlow downFlow:(NSString *)downFlow{
    if(self.hidden)
        return;
    if([upFlow floatValue]>1000){
        upFlow = [NSString stringWithFormat:@"%.1fK",[upFlow floatValue]/1000];
        NSLog(@"the end === %@",upFlow);
    }
    
    if([downFlow floatValue]>1000){
        downFlow = [NSString stringWithFormat:@"%.1fK",[downFlow floatValue]/1000];
    }
    [DoraemonAllTestWindow shareInstance].flowChanged = false;

    _upFlowValue.text = [NSString stringWithFormat:@"%@ : %@B",DoraemonLocalizedString(@"上行流量"),upFlow];
    _downFlowValue.text = [NSString stringWithFormat:@"%@ : %@B",DoraemonLocalizedString(@"下行流量"),downFlow];
}

-(void)hideFlowValue{
    _upFlowValue.text = nil;
    _downFlowValue.text = nil;
}

@end
