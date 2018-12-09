//
//  DoraemonGPSViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/20.
//

#import "DoraemonGPSViewController.h"
#import <MapKit/MapKit.h>
#import "UIImage+Doraemon.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonToastUtil.h"
#import "DoraemonGPSMocker.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonMockGPSOperateView.h"
#import "DoraemonMockGPSInputView.h"
#import "DoraemonMockGPSCenterView.h"

@interface DoraemonGPSViewController ()<MKMapViewDelegate,DoraemonMockGPSInputViewDelegate>

@property (nonatomic, strong) MKMapView *mapView;
@property (nonatomic, strong) CLLocationManager *locationManager;
@property (nonatomic, strong) DoraemonMockGPSOperateView *operateView;
@property (nonatomic, strong) DoraemonMockGPSInputView *inputView;
@property (nonatomic, strong) DoraemonMockGPSCenterView *mapCenterView;

@end

@implementation DoraemonGPSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Mock GPS";
    
    [self initUI];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)initUI{
    _operateView = [[DoraemonMockGPSOperateView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(6), self.bigTitleView.doraemon_bottom+kDoraemonSizeFrom750(24), self.view.doraemon_width-2*kDoraemonSizeFrom750(6), kDoraemonSizeFrom750(124))];
    _operateView.switchView.on = [[DoraemonCacheManager sharedInstance] mockGPSSwitch];
    [self.view addSubview:_operateView];
    [_operateView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    
    _inputView = [[DoraemonMockGPSInputView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(6), _operateView.doraemon_bottom+kDoraemonSizeFrom750(17), self.view.doraemon_width-2*kDoraemonSizeFrom750(6), kDoraemonSizeFrom750(170))];
    _inputView.delegate = self;
    [self.view addSubview:_inputView];
    
    //获取定位服务授权
    [self requestUserLocationAuthor];
    //初始化地图
    MKMapView *mapView = [[MKMapView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-self.bigTitleView.doraemon_bottom)];
    mapView.mapType = MKMapTypeStandard;
    mapView.delegate = self;
    [self.view addSubview:mapView];
    self.mapView = mapView;
    
    [self.view sendSubviewToBack:self.mapView];
    
    _mapCenterView = [[DoraemonMockGPSCenterView alloc] initWithFrame:CGRectMake(_mapView.doraemon_width/2-kDoraemonSizeFrom750(250)/2, _mapView.doraemon_height/2-kDoraemonSizeFrom750(250)/2, kDoraemonSizeFrom750(250), kDoraemonSizeFrom750(250))];
    [_mapView addSubview:_mapCenterView];

    if (_operateView.switchView.on) {
        CLLocationCoordinate2D coordinate = [[DoraemonCacheManager sharedInstance] mockCoordinate];
        if (coordinate.longitude>0&&coordinate.latitude>0) {
            [_mapCenterView hiddenGPSInfo:NO];
            [_mapCenterView renderUIWithGPS:[NSString stringWithFormat:@"%f , %f",coordinate.longitude,coordinate.latitude]];
            [self.mapView setCenterCoordinate:coordinate animated:NO];
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
            [[DoraemonGPSMocker shareInstance] mockPoint:loc];
        }
    }else{
        [_mapCenterView hiddenGPSInfo:YES];
        [[DoraemonGPSMocker shareInstance] stopMockPoint];
    }
}
    

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveMockGPSSwitch:isButtonOn];
    if (isButtonOn) {
        CLLocationCoordinate2D coordinate = [[DoraemonCacheManager sharedInstance] mockCoordinate];
        if (coordinate.longitude>0 && coordinate.latitude>0) {
            [_mapCenterView hiddenGPSInfo:NO];
            [_mapCenterView renderUIWithGPS:[NSString stringWithFormat:@"%f , %f",coordinate.longitude,coordinate.latitude]];
            [self.mapView setCenterCoordinate:coordinate animated:NO];
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
            [[DoraemonGPSMocker shareInstance] mockPoint:loc];
        }
    }else{
        [_mapCenterView hiddenGPSInfo:YES];
        [[DoraemonGPSMocker shareInstance] stopMockPoint];
    }
}

#pragma mark - DoraemonMockGPSInputViewDelegate
- (void)inputViewOkClick:(NSString *)gps{
    if (![[DoraemonCacheManager sharedInstance] mockGPSSwitch]) {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"mock开关没有打开")];
        return;
    }
    NSArray *array = [gps componentsSeparatedByString:@" "];
    if(array && array.count == 2){
        NSString *longitudeValue = array[0];
        NSString *latitudeValue = array[1];
        if (longitudeValue.length==0 || latitudeValue.length==0) {
            [DoraemonToastUtil showToast:DoraemonLocalizedString(@"经纬度不能为空")];
            return;
        }
        
        CGFloat longitude = [longitudeValue floatValue];
        CGFloat latitude = [latitudeValue floatValue];
        if (longitude < -180 || longitude > 180) {
            [DoraemonToastUtil showToast:DoraemonLocalizedString(@"经度不合法")];
            return;
        }
        if (latitude < -90 || latitude > 90){
            [DoraemonToastUtil showToast:DoraemonLocalizedString(@"纬度不合法")];
            return;
        }
        
        CLLocationCoordinate2D coordinate;
        coordinate.longitude = longitude;
        coordinate.latitude = latitude;

        [_mapCenterView hiddenGPSInfo:NO];
        [_mapCenterView renderUIWithGPS: [NSString stringWithFormat:@"%f , %f",coordinate.longitude,coordinate.latitude]];
        [self.mapView setCenterCoordinate:coordinate animated:NO];
        
        CLLocation *loc = [[CLLocation alloc] initWithLatitude:coordinate.latitude longitude:coordinate.longitude];
        [[DoraemonGPSMocker shareInstance] mockPoint:loc];
    }else{
        [DoraemonToastUtil showToast:@"格式不正确"];
        return;
    }
    
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
    [_mapCenterView hiddenGPSInfo:NO];
    [_mapCenterView renderUIWithGPS:[NSString stringWithFormat:@"%f , %f",centerCoordinate.longitude,centerCoordinate.latitude]];
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:centerCoordinate.latitude longitude:centerCoordinate.longitude];
    [[DoraemonGPSMocker shareInstance] mockPoint:loc];
}


@end
