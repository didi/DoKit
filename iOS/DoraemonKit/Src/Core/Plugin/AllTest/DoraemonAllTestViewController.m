//
//  DoraemonAllTestViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/24.
//

#import "DoraemonAllTestViewController.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonAllTestManager.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonToastUtil.h"


@interface DoraemonAllTestViewController ()


@property (nonatomic, strong) DoraemonCellSwitch *fpsSwitchView;//fps
@property (nonatomic, strong) DoraemonCellSwitch *cpuSwitchView;//cpu
@property (nonatomic, strong) DoraemonCellSwitch *memorySwitchView;//memory
@property (nonatomic, strong) DoraemonCellSwitch *flowSwitchView;//flow

@property (nonatomic, strong) UIButton *okBtn;
@property (nonatomic, assign) BOOL okBtnStatus;//YES 打开  NO 关闭

@end

@implementation DoraemonAllTestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"自定义测试");
    
    _fpsSwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_fpsSwitchView renderUIWithTitle:DoraemonLocalizedString(@"帧率") switchOn:[DoraemonAllTestManager shareInstance].fpsSwitchOn];
    [_fpsSwitchView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:_fpsSwitchView];
    
    _cpuSwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.fpsSwitchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_cpuSwitchView renderUIWithTitle:@"CPU" switchOn:[DoraemonAllTestManager shareInstance].cpuSwitchOn];
    [_cpuSwitchView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:_cpuSwitchView];
    
    _memorySwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.cpuSwitchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_memorySwitchView renderUIWithTitle:DoraemonLocalizedString(@"内存") switchOn:[DoraemonAllTestManager shareInstance].memorySwitchOn];
    [_memorySwitchView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:_memorySwitchView];
    
    _flowSwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.memorySwitchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_flowSwitchView renderUIWithTitle:DoraemonLocalizedString(@"流量") switchOn:[DoraemonAllTestManager shareInstance].flowSwitchOn];
    [_flowSwitchView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [self.view addSubview:_flowSwitchView];
    
    
    _okBtn = [[UIButton alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(30), self.view.doraemon_bottom-kDoraemonSizeFrom750_Landscape(30)-kDoraemonSizeFrom750_Landscape(100), self.view.doraemon_width-2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(100))];
    _okBtn.backgroundColor = [UIColor doraemon_colorWithString:@"#337CC4"];
    _okBtn.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
    [_okBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    _okBtnStatus = [DoraemonAllTestManager shareInstance].startTestOn;
    if(_okBtnStatus){
        [_okBtn setTitle:DoraemonLocalizedString(@"结束测试") forState:UIControlStateNormal];
    }else{
        [_okBtn setTitle:DoraemonLocalizedString(@"开始测试") forState:UIControlStateNormal];
    }
    [_okBtn addTarget:self action:@selector(okBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_okBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    if (sender == _fpsSwitchView.switchView) {
        [DoraemonAllTestManager shareInstance].fpsSwitchOn = isButtonOn;
    }else if(sender == _cpuSwitchView.switchView){
        [DoraemonAllTestManager shareInstance].cpuSwitchOn = isButtonOn;
    }else if(sender == _memorySwitchView.switchView){
        [DoraemonAllTestManager shareInstance].memorySwitchOn = isButtonOn;
    }else {
        [DoraemonAllTestManager shareInstance].flowSwitchOn = isButtonOn;
    }
}

- (void)okBtnClick{
    __weak typeof(self) weakSelf = self;
    [DoraemonAlertUtil handleAlertActionWithVC:self text:@"是否确认" okBlock:^{
        weakSelf.okBtnStatus = !weakSelf.okBtnStatus;
        [DoraemonAllTestManager shareInstance].startTestOn = weakSelf.okBtnStatus;
        if (weakSelf.okBtnStatus) {
            [[DoraemonAllTestManager shareInstance] startRecord];
            [weakSelf.okBtn setTitle:DoraemonLocalizedString(@"结束测试") forState:UIControlStateNormal];
        }else{
            [DoraemonAllTestManager shareInstance].fpsSwitchOn = NO;
            [DoraemonAllTestManager shareInstance].cpuSwitchOn = NO;
            [DoraemonAllTestManager shareInstance].memorySwitchOn = NO;
            [DoraemonAllTestManager shareInstance].flowSwitchOn = NO;
            
            weakSelf.fpsSwitchView.switchView.on = NO;
            weakSelf.cpuSwitchView.switchView.on = NO;
            weakSelf.memorySwitchView.switchView.on = NO;
            weakSelf.flowSwitchView.switchView.on = NO;
            [[DoraemonAllTestManager shareInstance] endRecord];
            [weakSelf.okBtn setTitle:DoraemonLocalizedString(@"开始测试") forState:UIControlStateNormal];
            
            [DoraemonToastUtil showToast:DoraemonLocalizedString(@"数据保存到Library/Caches/DoraemonPerformance中") inView:self.view];
        }
    } cancleBlock:^{
        
    }];
}



@end
