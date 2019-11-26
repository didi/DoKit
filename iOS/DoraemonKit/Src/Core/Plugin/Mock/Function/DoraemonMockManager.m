//
//  DoraemonMockManager.m
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import "DoraemonMockManager.h"
#import "DoraemonNetworkUtil.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonCacheManager.h"
#import "DoraemonUrlUtil.h"
#import "DoraemonManager.h"

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

- (void)queryMockData{
    NSString *pId = [DoraemonManager shareInstance].pId;
    if (pId && pId.length>0) {
        NSDictionary *params = @{
            @"projectId":pId,
            @"isfull":@"1",
            @"curPage":@"1",
            @"pageSize":@"20"
        };
        
        __weak typeof(self) weakSelf = self;
        [DoraemonNetworkUtil getWithUrlString:@"http://xyrd.intra.xiaojukeji.com/api/app/interface" params:params success:^(NSDictionary * _Nonnull result) {
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
        } error:^(NSError * _Nonnull error) {
            NSLog(@"error == %@",error);
        }];
    }else{
        NSLog(@"请求接口列表必须保证pId不为空");
    }
    
}

// 处理数据：合并网络数据和本地数据
- (void)handleData {
    _mockGroup = @"所有";
    _mockState = @"所有";
    _uploadGroup = @"所有";
    _uploadState = @"所有";
    for (DoraemonMockAPIModel *api in self.mockArray) {
        if (api.selected) {
            self.mock = YES;
            return;
        }
    }
}

- (BOOL)needMock:(NSURLRequest *)request{
    DoraemonMockBaseModel *api = [self getSelectedData:request dataArray:_mockArray];
    BOOL mock = NO;
    if (api) {
        mock = YES;
    }
    NSLog(@"yixiang mock = %zi",mock);
    return mock;
}

- (NSString *)getSceneId:(NSURLRequest *)request{
    DoraemonMockAPIModel *api = (DoraemonMockAPIModel *)[self getSelectedData:request dataArray:_mockArray];
    NSArray<DoraemonMockScene *> *sceneList = api.sceneList;
    NSString *sceneId;
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
    DoraemonMockBaseModel *selectedApi;
    for (DoraemonMockBaseModel *api in dataArray) {
        if ([api.path isEqualToString:path] && api.selected) {
            if (api.query && api.query.allKeys.count>0) {
                NSDictionary *q = api.query;
                BOOL match = YES;
                for (NSString *key in q.allKeys) {
                    NSString *value = q[@"key"];
                    NSString *item = [NSString stringWithFormat:@"%@=%@",key,value];
                    if (![query containsString:item]) {
                        match = NO;
                        break;
                    }
                }
                if (match) {
                    selectedApi = api;
                    break;
                }
            }else{
                selectedApi = api;
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
    NSLog(@"yixiang save = %zi",save);
    return save;
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    if ([self needSave:request]) {
        NSString *result = [DoraemonUrlUtil convertJsonFromData:data];
        DoraemonMockUpLoadModel *upload = (DoraemonMockUpLoadModel *)[self getSelectedData:request dataArray:_upLoadArray];
        upload.result = result;
    }
}


- (BOOL)shouldIntercept {
    return _isDetecting;
}


@end
