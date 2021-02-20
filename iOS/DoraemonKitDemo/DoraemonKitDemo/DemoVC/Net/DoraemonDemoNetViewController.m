//
//  DoraemonDemoNetViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/18.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoNetViewController.h"
#import "UIView+Doraemon.h"
#import <AFNetworking/AFNetworking.h>
#import <DoraemonKit/DoraemonNSURLProtocol.h>
#import "DoraemonDefine.h"
#import "DoraemonUIWebViewViewController.h"
#import "DoraemonWKWebViewViewController.h"
#import "DoraemonDemoImageViewController.h"
#import <DoraemonKit/DoraemonNetworkUtil.h>
#import "DoraemonDemoNetTableViewCell.h"
#import "DoraemonDemoURLProtocol1.h"
#import "DoraemonDemoURLProtocol2.h"

@interface DoraemonDemoNetViewController ()<NSURLConnectionDataDelegate,NSURLSessionDelegate,UITableViewDataSource,UITableViewDelegate>


@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *cellTitleArray;
@property (nonatomic, strong) NSString *DoraemonDemoNetViewCellID;

/** 可变的二进制数据 */
@property (nonatomic, strong) NSMutableData *fileData;

@end

@implementation DoraemonDemoNetViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"网络测试Demo");
    
    [self initCellTitleArray];
    _DoraemonDemoNetViewCellID = @"DoraemonDemoNetViewCell";
    [self.view addSubview:self.tableView];
    
    //[NSURLProtocol registerClass:[DoraemonDemoURLProtocol1 class]];
    //[NSURLProtocol registerClass:[DoraemonDemoURLProtocol2 class]];
}

- (void)initCellTitleArray{
    _cellTitleArray = @[
        @"发送一条URLConnection请求",
        @"发送一条NSURLSession请求",
        @"发送一条AFNetworking请求",
        @"发送一条AFNetworking请求2",
        @"打开UIWebView",
        @"打开WKWebView",
        @"图片测试",
        @"Mock测试",
        @"Mock测试2"
    ];
}

- (UITableView *)tableView{
    if(!_tableView){
        _tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
        _tableView.backgroundColor = [UIColor whiteColor];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        [_tableView registerClass:[DoraemonDemoNetTableViewCell class] forCellReuseIdentifier:_DoraemonDemoNetViewCellID];
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return _tableView;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return _cellTitleArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return kDoraemonSizeFrom750_Landscape(104);
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return kDoraemonSizeFrom750_Landscape(24);
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    UIView* footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width,kDoraemonSizeFrom750_Landscape(24))];
    footerView.backgroundColor = [UIColor clearColor];
    return footerView;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    DoraemonDemoNetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:_DoraemonDemoNetViewCellID];
    if (!cell) {
        cell = [[DoraemonDemoNetTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:_DoraemonDemoNetViewCellID];
    }
    [cell renderUIWithTitle:DoraemonDemoLocalizedString(_cellTitleArray[indexPath.section])];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [self cellSelected:indexPath.section];
}

- (void)cellSelected:(NSInteger)row{
    switch (row) {
        case 0:
            [self netForURLConnection];
            break;
        case 1:
            [self netForNSURLSession];
            break;
        case 2:
            [self netForAFNetworking];
            break;
        case 3:
            [self netForAFNetworking2];
            break;
        case 4:
            [self openUIWebView];
            break;
        case 5:
            [self openWKWebView];
            break;
        case 6:
            [self imageTest];
            break;
        case 7:
            [self mockTest];
            break;
        case 8:
            [self mockTest2];
            break;
        
        default:
            break;
    }
}


- (void)openUIWebView{
    UIViewController *vc = [[DoraemonUIWebViewViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)openWKWebView{
    UIViewController *vc = [[DoraemonWKWebViewViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)imageTest{
    DoraemonDemoImageViewController *vc = [[DoraemonDemoImageViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)netForURLConnection{
    //发送一条异步请求，block方式
    NSURL *url = [NSURL URLWithString:@"https://www.taobao.com/"];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL:url];
    [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init]completionHandler:^(NSURLResponse * _Nullable response, NSData * _Nullable data, NSError * _Nullable connectionError) {
        if ((data != nil) && (connectionError == nil)) {
            //            NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"response == %@",[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
        }
        
    }];
    
    //发送一条异步请求，delegate方式
    //NSURLConnection *connect = [[NSURLConnection alloc]initWithRequest:request delegate:self startImmediately:NO];
    //[connect cancel]; 取消
    //[connect start];
}

- (void)netForNSURLSession{
    //发送一条异步请求，block方式
    NSURLSession *session = [NSURLSession sharedSession];
    NSURL *url = [NSURL URLWithString:@"https://www.taobao.com/"];
    // 通过URL初始化task,在block内部可以直接对返回的数据进行处理
    NSURLSessionTask *task = [session dataTaskWithURL:url
                                    completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                                        if (error) {
                                            NSLog(@"error == %@",error);
                                            return ;
                                        }
                                        //                                        NSDictionary *result = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
                                        NSLog(@"%@", data);
                                    }];
    
    // 启动任务
    [task resume];
    
    //发送一条异步请求，delegate方式
    // 使用代理方法需要设置代理,但是session的delegate属性是只读的,要想设置代理只能通过这种方式创建session
    //NSURLSession *session1 = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
//                                                           delegate:self
//                                                      delegateQueue:[[NSOperationQueue alloc] init]];
    
    // 创建任务(因为要使用代理方法,就不需要block方式的初始化了)
    //NSURLSessionDataTask *task1 = [session1 dataTaskWithRequest:[NSURLRequest requestWithURL:url]];
    
    // 启动任务
    //[task1 resume];
}

- (void)netForAFNetworking{
    //NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    //configuration.protocolClasses = @[[DoraemonNSURLProtocol class]];
    //AFHTTPSessionManager *session = [[AFHTTPSessionManager alloc] initWithSessionConfiguration:configuration];
    
    AFHTTPSessionManager *session = [AFHTTPSessionManager manager];
    
    //    NSMutableArray *protocolsArray = [NSMutableArray arrayWithArray:session.session.configuration.protocolClasses];
    //    [protocolsArray insertObject:[DoraemonNSURLProtocol class] atIndex:0];
    //    session.session.configuration.protocolClasses = [protocolsArray copy];
    //    session.session.configuration.protocolClasses = @[[DoraemonNSURLProtocol class]];
    
    session.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
    session.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
    session.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
    
    [session GET:@"https://www.taobao.com/" parameters:nil headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"success %@",string);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
    }];
    
    //    [session POST:@"http://172.23.160.242:8080/YxReactServerDemo/message/getAllMessage" parameters:@{@"name":@"yixiang1"} success:^(NSURLSessionDataTask *task, id responseObject) {
    //        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
    //        NSLog(@"请求成功 %@",string);
    //    } failure:^(NSURLSessionDataTask *task, NSError *error) {
    //        NSLog(@"请求失败");
    //    }];
}

- (void)netForAFNetworking2{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
    //    [manager POST:@"http://172.23.160.242:8080/YxReactServerDemo/message/getAllMessage" parameters:@{@"name":@"yixiang2"} success:^(AFHTTPRequestOperation *operation, id responseObject) {
    //        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
    //        NSLog(@"请求成功 %@",string);
    //    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
    //        NSLog(@"请求失败");
    //    }];
    
    [manager POST:@"https://www.taobao.com/" parameters:nil headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"success %@",string);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"failure");
    }];
}

#pragma mark -- NSURLConnectionDataDelegate
//1.当接受到服务器响应的时候会调用:response(响应头)
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{
    NSLog(@"receive response");
}

//2.当接受到服务器返回数据的时候调用(会调用多次)
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    //
    NSLog(@"receive data");
    //拼接数据
    [self.fileData appendData:data];
}

//3.当请求失败的时候调用
-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error{
    NSLog(@"fail with error");
}

//4.当请求结束(成功|失败)的时候调用
-(void)connectionDidFinishLoading:(NSURLConnection *)connection{
    NSLog(@"connection did finish loading");
    //解析数据
    NSString *json = [[NSString alloc]initWithData:self.fileData encoding:NSUTF8StringEncoding];
    NSLog(@"%@",json);
}


#pragma mark -- NSURLSessionDelegate
// 1.接收到服务器的响应
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    // 允许处理服务器的响应，才会继续接收服务器返回的数据
    completionHandler(NSURLSessionResponseAllow);
}

// 2.接收到服务器的数据（可能调用多次）
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    // 处理每次接收的数据
    [self.fileData appendData:data];
}

// 3.请求成功或者失败（如果失败，error有值）
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    // 请求完成,成功或者失败的处理
    NSString *json = [[NSString alloc]initWithData:self.fileData encoding:NSUTF8StringEncoding];
    NSLog(@"%@",json);
}

- (void)mockTest {
    [DoraemonNetworkUtil getWithUrlString:@"http://172.23.162.150:8080/api/topics/hot.json?name=yi" params:nil success:^(NSDictionary * _Nonnull result) {
        NSLog(@"result == %@",result);
    } error:^(NSError * _Nonnull error) {
        NSLog(@"error == %@",error);
    }];
}

- (void)mockTest2 {
    [DoraemonNetworkUtil postWithUrlString:@"http://172.23.162.150:8080/api/topics/hot.json" params:@{@"bodyTitle":@"bodyName"} success:^(NSDictionary * _Nonnull result) {
        NSLog(@"result == %@",result);
    } error:^(NSError * _Nonnull error) {
        NSLog(@"error == %@",error);
    }];
}

@end
