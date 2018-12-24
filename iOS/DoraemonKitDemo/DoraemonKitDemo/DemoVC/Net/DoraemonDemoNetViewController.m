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

@interface DoraemonDemoNetViewController ()<NSURLConnectionDataDelegate,NSURLSessionDelegate>

@property (nonatomic, strong) UIButton *btn0;
@property (nonatomic, strong) UIButton *btn1;
@property (nonatomic, strong) UIButton *btn2;
@property (nonatomic, strong) UIButton *btn3;

/** 可变的二进制数据 */
@property (nonatomic, strong) NSMutableData *fileData;

@end

@implementation DoraemonDemoNetViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"网络测试Demo";
    
    UIButton *btn0 = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn0.backgroundColor = [UIColor orangeColor];
    [btn0 setTitle:@"发送一条URLConnection请求" forState:UIControlStateNormal];
    [btn0 addTarget:self action:@selector(netForURLConnection) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn0];
    _btn0 = btn0;
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn0.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:@"发送一条NSURLSession请求" forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(netForNSURLSession) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    _btn1 = btn1;
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:@"发送一条AFNetworking请求" forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(netForAFNetworking) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    _btn2 = btn2;
    
    UIButton *btn3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn3.backgroundColor = [UIColor orangeColor];
    [btn3 setTitle:@"发送一条AFNetworking请求2" forState:UIControlStateNormal];
    [btn3 addTarget:self action:@selector(netForAFNetworking2) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
    _btn3 = btn3;
    
}

- (void)netForURLConnection{
    //发送一条异步请求，block方式
    NSURL *url = [NSURL URLWithString:@"https://www.taobao.com/"];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL:url];
    [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init]completionHandler:^(NSURLResponse * _Nullable response, NSData * _Nullable data, NSError * _Nullable connectionError) {
        if ((data != nil) && (connectionError == nil)) {
            //            NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"response == %@",data);
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
    NSURLSession *session1 = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
                                                           delegate:self
                                                      delegateQueue:[[NSOperationQueue alloc] init]];
    
    // 创建任务(因为要使用代理方法,就不需要block方式的初始化了)
    NSURLSessionDataTask *task1 = [session1 dataTaskWithRequest:[NSURLRequest requestWithURL:url]];
    
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
    [session GET:@"https://www.taobao.com/" parameters:nil success:^(NSURLSessionDataTask *task, id responseObject) {
        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"请求成功 %@",string);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        NSLog(@"请求失败");
    }];
    
    //    [session POST:@"http://172.23.160.242:8080/YxReactServerDemo/message/getAllMessage" parameters:@{@"name":@"yixiang1"} success:^(NSURLSessionDataTask *task, id responseObject) {
    //        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
    //        NSLog(@"请求成功 %@",string);
    //    } failure:^(NSURLSessionDataTask *task, NSError *error) {
    //        NSLog(@"请求失败");
    //    }];
}

- (void)netForAFNetworking2{
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
    //    [manager POST:@"http://172.23.160.242:8080/YxReactServerDemo/message/getAllMessage" parameters:@{@"name":@"yixiang2"} success:^(AFHTTPRequestOperation *operation, id responseObject) {
    //        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
    //        NSLog(@"请求成功 %@",string);
    //    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
    //        NSLog(@"请求失败");
    //    }];
    
    [manager POST:@"https://www.taobao.com/" parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"请求成功 %@",string);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"请求失败");
    }];
}

#pragma mark -- NSURLConnectionDataDelegate
//1.当接受到服务器响应的时候会调用:response(响应头)
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{
    NSLog(@"接受到相应");
}

//2.当接受到服务器返回数据的时候调用(会调用多次)
-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    //
    NSLog(@"接受到数据");
    //拼接数据
    [self.fileData appendData:data];
}

//3.当请求失败的时候调用
-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error{
    NSLog(@"请求失败");
}

//4.当请求结束(成功|失败)的时候调用
-(void)connectionDidFinishLoading:(NSURLConnection *)connection{
    NSLog(@"请求结束");
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

@end
