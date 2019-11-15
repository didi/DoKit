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
    NSDictionary *params = @{
        @"projectId":@"5c8e04056144a626ff2542e344",
        @"isfull":@"1",
        @"curPage":@"1",
        @"pageSize":@"20"
    };
    
    __weak typeof(self) weakSelf = self;
    [DoraemonNetworkUtil getWithUrlString:@"http://xyrd.intra.xiaojukeji.com/api/app/interface" params:params success:^(NSDictionary * _Nonnull result) {
        NSArray *apis = result[@"data"][@"datalist"];
        NSMutableArray<DoraemonMockAPIModel *> *mockArray = [[NSMutableArray alloc] init];
        NSMutableArray<DoraemonMockUpLoadModel *> *uploadArray = [[NSMutableArray alloc] init];
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
        }
        weakSelf.mockArray = mockArray;
        weakSelf.upLoadArray = uploadArray;
        [self handleData];
    } error:^(NSError * _Nonnull error) {
        NSLog(@"error == %@",error);
    }];
}

// 处理数据：合并网络数据和本地数据
- (void)handleData {
    for (DoraemonMockAPIModel *api in self.mockArray) {
        if (api.selected) {
            self.mock = YES;
            return;
        }
    }
}

- (BOOL)needMock:(NSURLRequest *)request{
    DoraemonMockAPIModel *api = [self getMockApi:request];
    BOOL mock = NO;
    if (api) {
        mock = YES;
    }
    return mock;
}

- (NSString *)getSceneId:(NSURLRequest *)request{
    DoraemonMockAPIModel *api = [self getMockApi:request];
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

- (DoraemonMockAPIModel *)getMockApi:(NSURLRequest *)request{
    NSString *path = request.URL.path;
    NSString *query = request.URL.query;
    DoraemonMockAPIModel *selectedApi;
    for (DoraemonMockAPIModel *api in _mockArray) {
        if ([api.path isEqualToString:path]) {
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
#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    NSLog(@"yixiang 收到回调");
}


- (BOOL)shouldIntercept {
    return _isDetecting;
}


@end
