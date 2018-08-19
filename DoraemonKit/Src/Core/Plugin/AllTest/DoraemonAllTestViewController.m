//
//  DoraemonAllTestViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/24.
//

#import "DoraemonAllTestViewController.h"
#import <UIView+Positioning/UIView+Positioning.h>
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonAllTestManager.h"


@interface DoraemonAllTestViewController ()

@property (nonatomic, strong) UISwitch *fpsSwitch;
@property (nonatomic, strong) UISwitch *cpuSwitch;
@property (nonatomic, strong) UISwitch *memorySwitch;
@property (nonatomic, strong) UISwitch *flowSwitch;
@property (nonatomic, strong) UIButton *okBtn;

@property (nonatomic, strong) UILabel *ipLabel;

@property (nonatomic, assign) BOOL okBtnStatus;//YES 打开  NO 关闭

@end

@implementation DoraemonAllTestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"综合测试";
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = [UIColor orangeColor];
    titleLabel.text = @"请选择你要监控的项目";
    [titleLabel sizeToFit];
    [self.view addSubview:titleLabel];
    titleLabel.frame = CGRectMake(self.view.width/2-titleLabel.width/2, 20, titleLabel.width, 20);
    
    _fpsSwitch = [[UISwitch alloc] init];
    _fpsSwitch.origin = CGPointMake(self.view.width/2-_fpsSwitch.width/2, titleLabel.bottom+20);
    [self.view addSubview:_fpsSwitch];
    [_fpsSwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    _fpsSwitch.on = [DoraemonAllTestManager shareInstance].fpsSwitchOn;
    
    UILabel *fpsLabel = [[UILabel alloc] init];
    fpsLabel.font = [UIFont systemFontOfSize:16];
    fpsLabel.textColor = [UIColor orangeColor];
    fpsLabel.text = @"FPS: ";
    [fpsLabel sizeToFit];
    [self.view addSubview:fpsLabel];
    fpsLabel.frame = CGRectMake(_fpsSwitch.left-10-fpsLabel.width, _fpsSwitch.top+(_fpsSwitch.height-fpsLabel.height)/2, fpsLabel.width, fpsLabel.height);
    
    _cpuSwitch = [[UISwitch alloc] init];
    _cpuSwitch.origin = CGPointMake(self.view.width/2-_cpuSwitch.width/2, _fpsSwitch.bottom+20);
    [self.view addSubview:_cpuSwitch];
    [_cpuSwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    _cpuSwitch.on = [DoraemonAllTestManager shareInstance].cpuSwitchOn;
    
    UILabel *cpuLabel = [[UILabel alloc] init];
    cpuLabel.font = [UIFont systemFontOfSize:16];
    cpuLabel.textColor = [UIColor orangeColor];
    cpuLabel.text = @"CPU: ";
    [cpuLabel sizeToFit];
    [self.view addSubview:cpuLabel];
    cpuLabel.frame = CGRectMake(_cpuSwitch.left-10-cpuLabel.width, _cpuSwitch.top+(_cpuSwitch.height-cpuLabel.height)/2, cpuLabel.width, cpuLabel.height);
    
    _memorySwitch = [[UISwitch alloc] init];
    _memorySwitch.origin = CGPointMake(self.view.width/2-_memorySwitch.width/2, _cpuSwitch.bottom+20);
    [self.view addSubview:_memorySwitch];
    [_memorySwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    _memorySwitch.on = [DoraemonAllTestManager shareInstance].memorySwitchOn;
    
    UILabel *memoryLabel = [[UILabel alloc] init];
    memoryLabel.font = [UIFont systemFontOfSize:16];
    memoryLabel.textColor = [UIColor orangeColor];
    memoryLabel.text = @"MEMORY: ";
    [memoryLabel sizeToFit];
    [self.view addSubview:memoryLabel];
    memoryLabel.frame = CGRectMake(_memorySwitch.left-10-memoryLabel.width, _memorySwitch.top+(_memorySwitch.height-memoryLabel.height)/2, memoryLabel.width, memoryLabel.height);
    
    _flowSwitch = [[UISwitch alloc] init];
    _flowSwitch.origin = CGPointMake(self.view.width/2-_flowSwitch.width/2, _memorySwitch.bottom+20);
    [self.view addSubview:_flowSwitch];
    [_flowSwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    _flowSwitch.on = [DoraemonAllTestManager shareInstance].flowSwitchOn;
    
    UILabel *flowLabel = [[UILabel alloc] init];
    flowLabel.font = [UIFont systemFontOfSize:16];
    flowLabel.textColor = [UIColor orangeColor];
    flowLabel.text = @"FLOW: ";
    [flowLabel sizeToFit];
    [self.view addSubview:flowLabel];
    flowLabel.frame = CGRectMake(_flowSwitch.left-10-flowLabel.width, _flowSwitch.top+(_flowSwitch.height-flowLabel.height)/2, flowLabel.width, flowLabel.height);
    
    _okBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.width/2-60, _flowSwitch.bottom+60, 120, 60)];
    _okBtn.backgroundColor = [UIColor orangeColor];
    _okBtnStatus = [DoraemonAllTestManager shareInstance].startTestOn;
    if(_okBtnStatus){
        [_okBtn setTitle:@"结束测试" forState:UIControlStateNormal];
    }else{
        [_okBtn setTitle:@"开始测试" forState:UIControlStateNormal];
    }
    [_okBtn addTarget:self action:@selector(okBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_okBtn];
    
    
    
    UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(20, _okBtn.bottom+60, self.view.width-40, 60)];
    textField.placeholder = @"请输入服务端ip地址";
    textField.layer.cornerRadius = 2.;
    textField.layer.borderWidth = 1.;
    textField.layer.borderColor = [UIColor orangeColor].CGColor;
    [textField addTarget:self
                   action:@selector(textFieldDidChange:)
         forControlEvents:UIControlEventEditingChanged];
    textField.hidden = YES;
    [self.view addSubview:textField];
    
    UILabel *ipLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, textField.bottom+60, self.view.width, 60)];
    ipLabel.text = [DoraemonAllTestManager shareInstance].ip;
    ipLabel.textColor = [UIColor orangeColor];
    ipLabel.font = [UIFont systemFontOfSize:16];
    ipLabel.textAlignment = NSTextAlignmentCenter;
    ipLabel.hidden = YES;
    [self.view addSubview:ipLabel];
    _ipLabel = ipLabel;
}

- (void)textFieldDidChange:(UITextField *)textField{
    NSLog(@"%@",textField.text);
    _ipLabel.text = textField.text;
    [DoraemonAllTestManager shareInstance].ip = textField.text;
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    if (sender == _fpsSwitch) {
        [DoraemonAllTestManager shareInstance].fpsSwitchOn = isButtonOn;
    }else if(sender == _cpuSwitch){
        [DoraemonAllTestManager shareInstance].cpuSwitchOn = isButtonOn;
    }else if(sender == _memorySwitch){
        [DoraemonAllTestManager shareInstance].memorySwitchOn = isButtonOn;
    }else {
        [DoraemonAllTestManager shareInstance].flowSwitchOn = isButtonOn;
    }
}

- (void)okBtnClick{
    _okBtnStatus = !_okBtnStatus;
    [DoraemonAllTestManager shareInstance].startTestOn = _okBtnStatus;
    if (_okBtnStatus) {
        [[DoraemonAllTestManager shareInstance] startRecord];
        [_okBtn setTitle:@"结束测试" forState:UIControlStateNormal];
    }else{
        [[DoraemonAllTestManager shareInstance] endRecord];
        [_okBtn setTitle:@"开始测试" forState:UIControlStateNormal];
    }
    
}



@end
