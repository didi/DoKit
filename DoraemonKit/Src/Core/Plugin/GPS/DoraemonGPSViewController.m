//
//  DoraemonGPSViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/20.
//

#import "DoraemonGPSViewController.h"
#import <MapKit/MapKit.h>
#import "UIImage+DoraemonKit.h"
#import "UIView+Positioning.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonToastUtil.h"
#import "DoraemonGPSMocker.h"

@interface DoraemonGPSViewController ()<MKMapViewDelegate>

@property (nonatomic, strong) MKMapView *mapView;
@property (nonatomic, strong) CLLocationManager *locationManager;
@property (nonatomic, strong) UIView *operatorView;
@property (nonatomic, strong) UILabel *tipLabel;
@property (nonatomic, strong) UISwitch *switchView;
@property (nonatomic, strong) UILabel *mockLabel;
@property (nonatomic, strong) UIButton *inputBtn;
@property (nonatomic, strong) UILabel *longitudeTipLabel;
@property (nonatomic, strong) UITextField *longitudeField;
@property (nonatomic, strong) UILabel *latitudeTipLabel;
@property (nonatomic, strong) UITextField *latitudeField;
@property (nonatomic, strong) UIButton *okBtn;

@property (nonatomic, strong) UIView *centerView;

@end

@implementation DoraemonGPSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Mock GPS";
    
    [self initUI];
}

- (void)initUI{
    _operatorView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 120)];
    [self.view addSubview:_operatorView];
    
    _tipLabel = [[UILabel alloc] init];
    _tipLabel.textColor = [UIColor blackColor];
    _tipLabel.text = @"是否打开Mock GPS开关⬇️:";
    _tipLabel.font = [UIFont systemFontOfSize:14];
    [_tipLabel sizeToFit];
    _tipLabel.frame = CGRectMake(20, 5, _tipLabel.doraemon_width, _tipLabel.doraemon_height);
    [_operatorView addSubview:_tipLabel];
    
    _mockLabel = [[UILabel alloc] init];
    _mockLabel.textColor = [UIColor blackColor];
    _mockLabel.font = [UIFont boldSystemFontOfSize:18];
    _mockLabel.frame = CGRectMake(0, _tipLabel.doraemon_bottom+15, _operatorView.doraemon_width, 14);
    _mockLabel.textAlignment = NSTextAlignmentRight;
    [_operatorView addSubview:_mockLabel];
    
    UISwitch *switchView = [[UISwitch alloc] init];
    switchView.doraemon_origin = CGPointMake(20, _tipLabel.doraemon_bottom+10);
    [_operatorView addSubview:switchView];
    [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    switchView.on = [[DoraemonCacheManager sharedInstance] mockGPSSwitch];
    self.switchView = switchView;
    
    
    //支持输入经纬度信息
    _inputBtn = [[UIButton alloc] initWithFrame:CGRectMake(20, switchView.doraemon_bottom+20, 160, 30)];
    _inputBtn.layer.cornerRadius = 2;
    [_inputBtn setTitle:@"点击输入经纬度" forState:UIControlStateNormal];
    _inputBtn.backgroundColor = [UIColor orangeColor];
    [_inputBtn addTarget:self action:@selector(inputBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.operatorView addSubview:_inputBtn];
    
    _longitudeTipLabel = [[UILabel alloc] init];
    _longitudeTipLabel.textColor = [UIColor blackColor];
    _longitudeTipLabel.font = [UIFont systemFontOfSize:14];
    _longitudeTipLabel.text = @"经度:";
    [self.operatorView addSubview:_longitudeTipLabel];
    [_longitudeTipLabel sizeToFit];
    _longitudeTipLabel.frame = CGRectMake(20, _inputBtn.doraemon_top, _longitudeTipLabel.doraemon_width, 30);
    _longitudeTipLabel.hidden = YES;
    
    _longitudeField = [[UITextField alloc] initWithFrame:CGRectMake(_longitudeTipLabel.doraemon_right+2, _inputBtn.doraemon_top, 100, 30)];
    _longitudeField.layer.borderWidth = 1;
    _longitudeField.layer.borderColor = [UIColor blackColor].CGColor;
    _longitudeField.hidden = YES;
    [self.operatorView addSubview:_longitudeField];
    
    _latitudeTipLabel = [[UILabel alloc] init];
    _latitudeTipLabel.textColor = [UIColor blackColor];
    _latitudeTipLabel.font = [UIFont systemFontOfSize:14];
    _latitudeTipLabel.text = @"纬度:";
    [self.operatorView addSubview:_latitudeTipLabel];
    [_latitudeTipLabel sizeToFit];
    _latitudeTipLabel.frame = CGRectMake(_longitudeField.doraemon_right+4, _inputBtn.doraemon_top, _latitudeTipLabel.doraemon_width, 30);
    _latitudeTipLabel.hidden = YES;
    
    _latitudeField = [[UITextField alloc] initWithFrame:CGRectMake(_latitudeTipLabel.doraemon_right+2, _inputBtn.doraemon_top, 100, 30)];
    _latitudeField.layer.borderWidth = 1;
    _latitudeField.layer.borderColor = [UIColor blackColor].CGColor;
    _latitudeField.hidden = YES;
    [self.operatorView addSubview:_latitudeField];
    
    _okBtn = [[UIButton alloc] initWithFrame:CGRectMake(_latitudeField.doraemon_right+5, _inputBtn.doraemon_top, 100, 30)];
    _okBtn.layer.cornerRadius = 2;
    [_okBtn setTitle:@"确定" forState:UIControlStateNormal];
    _okBtn.backgroundColor = [UIColor orangeColor];
    _okBtn.hidden = YES;
    [_okBtn addTarget:self action:@selector(okBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.operatorView addSubview:_okBtn];
    
    //获取定位服务授权
    [self requestUserLocationAuthor];
    //初始化地图
    MKMapView *mapView = [[MKMapView alloc] initWithFrame:CGRectMake(0, _operatorView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-_operatorView.doraemon_bottom-self.navigationController.navigationBar.doraemon_height)];
    mapView.mapType = MKMapTypeStandard;
    //mapView.userTrackingMode = MKUserTrackingModeFollow;
    mapView.delegate = self;
    [self.view addSubview:mapView];
    self.mapView = mapView;

    if (switchView.on) {
        _mockLabel.hidden = NO;
        CLLocationCoordinate2D coordinate = [[DoraemonCacheManager sharedInstance] mockCoordinate];
        if (coordinate.longitude>0&&coordinate.latitude>0) {
            _mockLabel.text = [NSString stringWithFormat:@"(%f , %f)",coordinate.longitude,coordinate.latitude];
            [self.mapView setCenterCoordinate:coordinate animated:NO];
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
            [[DoraemonGPSMocker shareInstance] mockPoint:loc];
        }
    }else{
        _mockLabel.hidden = YES;
        [[DoraemonGPSMocker shareInstance] stopMockPoint];
    }
    
    
    _centerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
    _centerView.center = mapView.center;
    [self.view addSubview:_centerView];
    
    UIImageView *iconView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 30, 30)];
    iconView.image = [UIImage doraemon_imageNamed:@"doraemon_gps"];
    [_centerView addSubview:iconView];
}
    

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveMockGPSSwitch:isButtonOn];
    if (isButtonOn) {
        _mockLabel.hidden = NO;
        CLLocationCoordinate2D coordinate = [[DoraemonCacheManager sharedInstance] mockCoordinate];
        if (coordinate.longitude>0 && coordinate.latitude>0) {
            _mockLabel.text = [NSString stringWithFormat:@"(%f , %f)",coordinate.longitude,coordinate.latitude];
            [self.mapView setCenterCoordinate:coordinate animated:NO];
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
            [[DoraemonGPSMocker shareInstance] mockPoint:loc];
        }
    }else{
        _mockLabel.hidden = YES;
        [[DoraemonGPSMocker shareInstance] stopMockPoint];
        [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonMockCoordinateNotification object:nil userInfo:@{@"mockSwitch":@NO}];
    }
}

- (void)inputBtnClick{
    if (![[DoraemonCacheManager sharedInstance] mockGPSSwitch]) {
        [DoraemonToastUtil showToast:@"mock开关没有打开"];
        return;
    }
    _inputBtn.hidden = YES;
    _longitudeTipLabel.hidden = NO;
    _longitudeField.hidden = NO;
    _latitudeTipLabel.hidden = NO;
    _latitudeField.hidden = NO;
    _okBtn.hidden = NO;
}

- (void)okBtnClick{
    if (![[DoraemonCacheManager sharedInstance] mockGPSSwitch]) {
        [DoraemonToastUtil showToast:@"mock开关没有打开"];
        return;
    }
    NSString *longitudeValue = _longitudeField.text;
    NSString *latitudeValue = _latitudeField.text;
    if (longitudeValue.length==0 || latitudeValue.length==0) {
        [DoraemonToastUtil showToast:@"经纬度不能为空"];
        return;
    }
    
    CGFloat longitude = [longitudeValue floatValue];
    CGFloat latitude = [latitudeValue floatValue];
    if (longitude < -180 || longitude > 180) {
        [DoraemonToastUtil showToast:@"经度不合法"];
        return;
    }
    if (latitude < -90 || latitude > 90){
        [DoraemonToastUtil showToast:@"纬度不合法"];
        return;
    }
    
    CLLocationCoordinate2D coordinate;
    coordinate.longitude = longitude;
    coordinate.latitude = latitude;
    
    _mockLabel.text = [NSString stringWithFormat:@"(%f , %f)",coordinate.longitude,coordinate.latitude];
    [self.mapView setCenterCoordinate:coordinate animated:NO];
    
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
    [[DoraemonGPSMocker shareInstance] mockPoint:loc];
    
}

//如果没有获得定位授权，获取定位授权请求
- (void)requestUserLocationAuthor{
    self.locationManager = [[CLLocationManager alloc] init];
    if ([CLLocationManager locationServicesEnabled]) {
        if ([CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorizedWhenInUse) {
            [self.locationManager requestWhenInUseAuthorization];
        }
    }
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated {
    CLLocationCoordinate2D centerCoordinate = mapView.region.center;
    
    if (![[DoraemonCacheManager sharedInstance] mockGPSSwitch]) {
        return;
    }
    [[DoraemonCacheManager sharedInstance] saveMockCoordinate:centerCoordinate];
    _mockLabel.text = [NSString stringWithFormat:@"(%f , %f)",centerCoordinate.longitude,centerCoordinate.latitude];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonMockCoordinateNotification object:nil userInfo:@{@"mockSwitch":@YES}];
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:centerCoordinate.latitude longitude:centerCoordinate.longitude];
    [[DoraemonGPSMocker shareInstance] mockPoint:loc];
}


@end
