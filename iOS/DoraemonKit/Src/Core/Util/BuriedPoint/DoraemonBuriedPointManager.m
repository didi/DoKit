//
//  DoraemonBuriedPointManager.m
//  DoraemonKit
//
//  Created by didi on 2020/2/23.
//

#import "DoraemonBuriedPointManager.h"
#import "DoraemonUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonNetworkUtil.h"
#import "DoraemonManager.h"

@interface DoraemonBuriedPointManager()

@property (nonatomic, strong) NSMutableArray *pointArray;
@property (nonatomic, strong) NSMutableDictionary *basicInfoDic;
@property (nonatomic, assign) NSTimeInterval nowTime;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, assign) NSInteger timeOut;


@end

@implementation DoraemonBuriedPointManager

+ (DoraemonBuriedPointManager *)shareManager{
    static dispatch_once_t once;
    static DoraemonBuriedPointManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonBuriedPointManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
        self.count = 10;//暂定的10
        self.timeOut = 60;//时隔60s
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillEnterBackgroundNotification:) name:UIApplicationDidEnterBackgroundNotification object:nil];
    }
    return self;
}

- (NSMutableArray *)pointArray{
    if(nil == _pointArray){
        _pointArray = [[NSMutableArray alloc] init];
    }
    return _pointArray;
}

- (NSMutableDictionary *)basicInfoDic{
    if(nil == _basicInfoDic){
        _basicInfoDic = [NSMutableDictionary dictionary];
        
        [_basicInfoDic setValue:STRING_NOT_NULL([DoraemonManager shareInstance].pId) forKey:@"pId"];
        [_basicInfoDic setValue:@"iOS" forKey:@"platform"];
        [_basicInfoDic setValue:[DoraemonUtil currentTimeInterval] forKey:@"time"];
        [_basicInfoDic setValue:[DoraemonAppInfoUtil iphoneType] forKey:@"phoneModel"];
        [_basicInfoDic setValue:[DoraemonAppInfoUtil iphoneSystemVersion] forKey:@"systemVersion"];
        [_basicInfoDic setValue:[DoraemonAppInfoUtil appName] forKey:@"appName"];
        [_basicInfoDic setValue:[DoraemonAppInfoUtil bundleIdentifier] forKey:@"appId"];
        [_basicInfoDic setValue:DoKitVersion forKey:@"dokitVersion"];
        
        NSString *currentLanguageRegion = [[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] firstObject];
        [_basicInfoDic setValue:STRING_NOT_NULL(currentLanguageRegion) forKey:@"language"];//语言这一块主要是为了统计国内外用户分布
    }
    return _basicInfoDic;
}

- (void)addPointName:(NSString *)name{
    if (name.length<1) {
        return;
    }
    NSDictionary *dic = @{
        @"eventName":STRING_NOT_NULL(name),
        @"time":[DoraemonUtil currentTimeInterval]
    };
    [self.pointArray addObject:dic];
    
    if([self needUpload]){
        [self uploadData];
    }
}

- (void)uploadData{
    if(self.pointArray.count > 0){
        [self.basicInfoDic setValue:self.pointArray forKey:@"events"];
        NSMutableDictionary *params = [self.basicInfoDic copy];

        [DoraemonNetworkUtil postWithUrlString:@"https://www.dokit.cn/pointData/addPointData" params:params success:^(NSDictionary * _Nonnull result) {
            NSInteger code = [result[@"code"] integerValue];
            if (code == 200) {
                [self removePointArray];
            }
        } error:^(NSError * _Nonnull error){
            
        }];
    }
}


- (void)removePointArray{
    if(self.pointArray.count > 0){
        [self.pointArray removeAllObjects];
    }
}

- (BOOL)needUpload{
    //1、数据列表大于10条(暂定)时上传
    if (self.pointArray.count > self.count) {
        return YES;
    }else if(self.pointArray.count > 2){
        //2、条数据之间的时间大于60s(暂定)时上传
        NSInteger count = self.pointArray.count;
        NSDictionary *lastItem = self.pointArray[count-1];
        NSDictionary *lastSecondItem = self.pointArray[count-2];
        NSInteger lastItemTime = [lastItem[@"time"] integerValue];
        NSInteger lastSecondItemTime = [lastSecondItem[@"time"] integerValue];
        if (lastItemTime - lastSecondItemTime > self.timeOut*1000) {
            return YES;
        }
    }

    return NO;
}

//3、app 切换到后台时 上传
- (void)appWillEnterBackgroundNotification:(UIApplication *)application{
    [self uploadData];
}


@end
