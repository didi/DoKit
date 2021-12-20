//
//  DoraemonMockManager.m
//  DoraemonKit
//
//  Created by didi on 2019/10/31.
//

#import "DoraemonMockManager.h"
#import "DoraemonNetworkUtil.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonCacheManager.h"
#import "DoraemonUrlUtil.h"
#import "DoraemonManager.h"
#import "DoraemonMockUtil.h"
#import "DoraemonDefine.h"
#import "UIViewController+Doraemon.h"

@interface DoraemonMockManager()<DoraemonNetworkInterceptorDelegate>

@end

@implementation DoraemonMockManager {
    BOOL _isDetecting;
}

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _states = @[@"所有",@"打开",@"关闭"];
    }
    return self;
}

- (void)setMock:(BOOL)mock{
    if (_isDetecting == mock) {
        return;
    }
    _isDetecting = mock;
    [self updateInterceptStatus];
}

- (void)updateInterceptStatus {
    if (_isDetecting) {
        [[DoraemonNetworkInterceptor shareInstance] addDelegate: self];
    } else {
        [[DoraemonNetworkInterceptor shareInstance] removeDelegate: self];
    }
}

- (void)queryMockData:(void(^)(int flag))block{
    NSString *pId = [DoraemonManager shareInstance].pId;
    NSString *mockDomain = [DoraemonManager shareInstance].mockDomain ? [DoraemonManager shareInstance].mockDomain : @"https://mock.dokit.cn/";
    if (pId && pId.length>0) {
        NSDictionary *params = @{
            @"projectId":pId,
            @"isfull":@"1",
            @"curPage":@"1",
            @"pageSize":@"500"
        };
        
        __weak typeof(self) weakSelf = self;
        NSString *mockInterfaceUrl = [mockDomain stringByAppendingString:@"api/app/interface"];
        [DoraemonNetworkUtil getWithUrlString:mockInterfaceUrl params:params success:^(NSDictionary * _Nonnull result) {
            NSArray *apis = result[@"data"][@"datalist"];
            NSMutableArray<DoraemonMockAPIModel *> *mockArray = [[NSMutableArray alloc] init];
            NSMutableArray<DoraemonMockUpLoadModel *> *uploadArray = [[NSMutableArray alloc] init];
            NSMutableArray *groups = [[NSMutableArray alloc] init];
            [groups addObject:@"所有"];
            for (NSDictionary *item in apis) {
                DoraemonMockAPIModel *mock = [[DoraemonMockAPIModel alloc] init];
                mock.apiId = item[@"_id"];
                mock.name = item[@"name"];
                mock.path = item[@"path"];
                mock.query = item[@"query"];
                mock.body = item[@"body"];
                //mock.body = @{@"bodyTitle":@"bodyName"};
                mock.category = item[@"categoryName"];
                mock.owner = item[@"owner"][@"name"];
                mock.editor = item[@"curStatus"][@"operator"][@"name"];
                NSArray *sceneList = item[@"sceneList"];
                NSMutableArray *sList = [[NSMutableArray alloc] init];
                for (NSDictionary *scene in sceneList) {
                    DoraemonMockScene *s = [[DoraemonMockScene alloc] init];
                    s.sceneId = scene[@"_id"];
                    s.name = scene[@"name"];
                    [sList addObject:s];
                }
                mock.sceneList = sList;
                
                [mockArray addObject:mock];
                
                
                DoraemonMockUpLoadModel *upload = [[DoraemonMockUpLoadModel alloc] init];
                upload.apiId = item[@"_id"];
                upload.name = item[@"name"];
                upload.path = item[@"path"];
                upload.query = item[@"query"];
                upload.body = item[@"body"];
                //upload.body = @{@"bodyTitle":@"bodyName"};
                upload.category = item[@"categoryName"];
                upload.owner = item[@"owner"][@"name"];
                upload.editor = item[@"curStatus"][@"operator"][@"name"];
                [uploadArray addObject:upload];
                
                NSString *category = item[@"categoryName"];
                if (groups && ![groups containsObject:category]) {
                    [groups addObject:category];
                }
            }
            weakSelf.mockArray = mockArray;
            weakSelf.upLoadArray = uploadArray;
            weakSelf.groups = groups;
            [self handleData];
            block(1);
        } error:^(NSError * _Nonnull error) {
            DoKitLog(@"error == %@",error);
            block(2);
        }];
    }else{
        DoKitLog(@"Request interface list must ensure that pId is not empty");
        block(3);
    }
    
}

// 处理数据：合并网络数据和本地数据
- (void)handleData {
    _mockGroup = @"所有";
    _mockState = @"所有";
    _uploadGroup = @"所有";
    _uploadState = @"所有";
    
    [[DoraemonMockUtil sharedInstance] readMockArrayCache];
    [[DoraemonMockUtil sharedInstance] readUploadArrayCache];
    for (DoraemonMockAPIModel *api in self.mockArray) {
        if (api.selected) {
            self.mock = YES;
            break;
        }
    }
    for (DoraemonMockUpLoadModel *upload in self.upLoadArray) {
        if (upload.selected) {
            self.mock = YES;
            break;
        }
    }
}

- (BOOL)needMock:(NSURLRequest *)request{
    DoraemonMockBaseModel *api = [self getSelectedData:request dataArray:_mockArray];
    BOOL mock = NO;
    if (api) {
        mock = YES;
    }
    DoKitLog(@"yixiang mock = %d",mock);
    return mock;
}

- (NSString *)getSceneId:(NSURLRequest *)request{
    DoraemonMockAPIModel *api = (DoraemonMockAPIModel *)[self getSelectedData:request dataArray:_mockArray];
    NSArray<DoraemonMockScene *> *sceneList = api.sceneList;
    NSString *sceneId = @"";
    for (DoraemonMockScene *scene in sceneList) {
        if (scene.selected) {
            sceneId = scene.sceneId;
            break;
        }
    }
    
    return sceneId;
}

- (DoraemonMockBaseModel *)getSelectedData:(NSURLRequest *)request dataArray:(NSArray *)dataArray{
    NSString *path = request.URL.path;
    NSString *query = request.URL.query;
    // 这里暂时使用不严谨body match
    NSData *httpBody = request.HTTPBody;
    NSDictionary *requestBody = [DoraemonUrlUtil convertDicFromData:httpBody];
    DoraemonMockBaseModel *selectedApi;
    for (DoraemonMockBaseModel *api in dataArray) {
        //匹配path
        if (([path hasSuffix:api.path]) && api.selected) {
            //匹配query
            if (api.query && api.query.allKeys.count>0 && query && query.length>0) {
                NSDictionary *q = api.query;
                BOOL match = YES;
                for (NSString *key in q.allKeys) {
                    NSString *value = q[key];
                    NSString *item = [NSString stringWithFormat:@"%@=%@",key,value];
                    if (![query containsString:item]) {
                        match = NO;
                        break;
                    }
                }
                if (match) {
                    selectedApi = api;
                    DoKitLog(@"yixiang mock query match");
                    break;
                }
            }
            
            //匹配body
            if (api.body && api.body.allKeys.count>0 && requestBody && requestBody.allKeys.count>0) {
                NSDictionary *q = api.body;
                BOOL match = YES;
                for (NSString *key in q.allKeys) {
                    NSString *value1 = q[key];
                    NSString *value2 = requestBody[key];
                    if (!(value1 && value2 && [value1 isEqualToString:value2])) {
                        match = NO;
                        break;
                    }
                }
                if (match) {
                    selectedApi = api;
                    DoKitLog(@"yixiang mock body match");
                    break;
                }
            }
            
            if ((!api.query || api.query.allKeys.count==0) && (!api.body || api.body.allKeys.count==0)) {
                //都没有匹配到的话，只匹配path
                selectedApi = api;
                DoKitLog(@"yixiang mock path match");
                break;
            }
        }
    }
    return selectedApi;
}

- (BOOL)needSave:(NSURLRequest *)request{
    DoraemonMockBaseModel *api = [self getSelectedData:request dataArray:_upLoadArray];
    BOOL save = NO;
    if (api) {
        save = YES;
    }
    DoKitLog(@"yixiang save = %d api = %@ query = %@",save,api.path,api.query);
    return save;
}

- (NSMutableArray<DoraemonMockAPIModel *> *)filterMockArray{
    NSMutableArray<DoraemonMockAPIModel *> *filter_1_Array = [[NSMutableArray alloc] init];
    if ([_mockGroup isEqualToString:@"所有"]) {
        filter_1_Array = _mockArray;
    }else{
        for (DoraemonMockAPIModel *mockModel in _mockArray) {
            if ([_mockGroup isEqualToString:mockModel.category]) {
                [filter_1_Array addObject:mockModel];
            }
        }
    }

    NSMutableArray<DoraemonMockAPIModel *> *filter_2_Array = [[NSMutableArray alloc] init];
    if ([_mockState isEqualToString:@"所有"]) {
        filter_2_Array = filter_1_Array;
    }else{
        for (DoraemonMockAPIModel *mockModel in filter_1_Array) {
            if([_mockState isEqualToString:@"打开"] && mockModel.selected){
                [filter_2_Array addObject:mockModel];
            }else if([_mockState isEqualToString:@"关闭"] && !mockModel.selected){
                [filter_2_Array addObject:mockModel];
            }
        }
    }
    
    NSMutableArray<DoraemonMockAPIModel *> *filter_3_Array = [[NSMutableArray alloc] init];
    if(!_mockSearchText || _mockSearchText.length==0){
        filter_3_Array = filter_2_Array;
    }else {
        for (DoraemonMockAPIModel *mockModel in filter_2_Array) {
            if([mockModel.name containsString:_mockSearchText]){
                [filter_3_Array addObject:mockModel];
            }
        }
    }

    return  filter_3_Array;
}

- (NSMutableArray<DoraemonMockUpLoadModel *> *)filterUpLoadArray{
    NSMutableArray<DoraemonMockUpLoadModel *> *filter_1_Array = [[NSMutableArray alloc] init];
    if ([_uploadGroup isEqualToString:@"所有"]) {
        filter_1_Array = _upLoadArray;
    }else{
        for (DoraemonMockUpLoadModel *uploadModel in _upLoadArray) {
            if ([_uploadGroup isEqualToString:uploadModel.category]) {
                [filter_1_Array addObject:uploadModel];
            }
        }
    }

    NSMutableArray<DoraemonMockUpLoadModel *> *filter_2_Array = [[NSMutableArray alloc] init];
    if ([_uploadState isEqualToString:@"所有"]) {
        filter_2_Array = filter_1_Array;
    }else{
        for (DoraemonMockUpLoadModel *uploadModel in filter_1_Array) {
            if([_uploadState isEqualToString:@"打开"] && uploadModel.selected){
                [filter_2_Array addObject:uploadModel];
            }else if([_uploadState isEqualToString:@"关闭"] && !uploadModel.selected){
                [filter_2_Array addObject:uploadModel];
            }
        }
    }
    
    NSMutableArray<DoraemonMockUpLoadModel *> *filter_3_Array = [[NSMutableArray alloc] init];
    if(!_uploadSearchText || _uploadSearchText.length==0){
        filter_3_Array = filter_2_Array;
    }else {
        for (DoraemonMockUpLoadModel *uploadModel in filter_2_Array) {
            if([uploadModel.name containsString:_uploadSearchText]){
                [filter_3_Array addObject:uploadModel];
            }
        }
    }

    return  filter_3_Array;

}

- (void)uploadSaveData:(DoraemonMockUpLoadModel *)upload atView:(UIView *)view{
    NSString *apiId = upload.apiId;
    NSString *result = upload.result;
    NSString *projectId = [DoraemonManager shareInstance].pId;
    NSString *mockDomain = [DoraemonManager shareInstance].mockDomain ? [DoraemonManager shareInstance].mockDomain : @"https://mock.dokit.cn/";
    if (projectId && projectId.length > 0) {
        if (!result) {
            return;
        }
        
        NSDictionary *params = @{
            @"projectId":projectId,
            @"id":apiId,
            @"tempData":result
        };
        NSString *mockInterfaceUrl = [mockDomain stringByAppendingString:@"api/app/interface"];
        [DoraemonNetworkUtil patchWithUrlString:mockInterfaceUrl params:params success:^(NSDictionary * _Nonnull result) {
            [self showToast:DoraemonLocalizedString(@"上传成功") atView:view];
        } error:^(NSError * _Nonnull error) {
            DoKitLog(@"error == %@",error);
            [self showToast:DoraemonLocalizedString(@"上传失败") atView:view];
        }];
    }else{
        DoKitLog(@"Upload template must has pid");
    }
}

- (void)showToast:(NSString *)toast atView:view{
    if ([NSThread isMainThread]) {
        [DoraemonToastUtil showToastBlack:toast inView:view];
    }else{
        dispatch_async(dispatch_get_main_queue(), ^{
            [DoraemonToastUtil showToastBlack:toast inView:view];
        });
    }
    
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    if ([self needSave:request]) {
        NSString *result = [DoraemonUrlUtil convertJsonFromData:data];
        DoraemonMockUpLoadModel *upload = (DoraemonMockUpLoadModel *)[self getSelectedData:request dataArray:_upLoadArray];
        upload.result = result;
        [[DoraemonMockUtil sharedInstance] saveUploadArrayCache];
        dispatch_async(dispatch_get_main_queue(), ^{
            [DoraemonToastUtil showToastBlack:[NSString stringWithFormat:@"save url = %@",request.URL.absoluteURL] inView:[UIViewController rootViewControllerForKeyWindow].view];
        });
    }
}


- (BOOL)shouldIntercept {
    return _isDetecting;
}


@end
