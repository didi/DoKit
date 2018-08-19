//
//  ViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "ViewController.h"
//#import <CocoaLumberjack/CocoaLumberjack.h>
#import "NSObject+Runtime.h"
#import <DoraemonKit/UIColor+DoreamonKit.h>

@interface ViewController ()<UITableViewDelegate,UITableViewDataSource,NSURLConnectionDataDelegate,NSURLSessionDelegate>

/** 可变的二进制数据 */
@property (nonatomic, strong) NSMutableData *fileData;

@end

@implementation ViewController

/*
 懒加载
 */
-(NSMutableData *)fileData{
    if (!_fileData) {
        _fileData = [[NSMutableData alloc]init];
    }
    return _fileData;
}



- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"DoraemonKitDemo";
    

    //NSArray *dataArray = @[@"1"];
    //NSString *he = dataArray[1];
    
    //DDLogInfo(@"n好");
    
    UITableView *tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
    tableView.delegate = self;
    tableView.dataSource = self;
    [self.view addSubview:tableView];
    
    UIView *redView = [[UIView alloc] initWithFrame:CGRectMake(100, 300, 60, 60)];
    redView.backgroundColor = [UIColor redColor];
    [self.view addSubview:redView];
    
    UIView *alphaView = [[UIView alloc] initWithFrame:CGRectMake(100, 400, 60, 60)];
    alphaView.backgroundColor = [UIColor doraemon_colorWithHexString:@"#89FFFF00"];
    [self.view addSubview:alphaView];
    
    
    //判断手机中是否安装 美团司机端
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL  URLWithString:@"meituanqcsr://"]]){
        NSLog(@"install--");
        [[UIApplication sharedApplication] openURL:[NSURL  URLWithString:@"meituanqcsr://"]];
    }else{
        NSLog(@"no---");
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return 7;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *cellId = @"cellId";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
    }
    NSString *txt = nil;
    NSInteger row = indexPath.row;
    if (row==0) {
        txt = @"添加一条日志";
    }else if(row==1){
        txt = @"往沙盒中添加数据";
    }else if(row==2){
        txt = @"显示曲线图";
    }else if(row==3){
        txt = @"发送一条URLConnection请求";
    }else if(row==4){
        txt = @"发送一条NSURLSession请求";
    }else if(row==5){
        txt = @"hook person";
    }else if(row==6){
        txt = @"hook person test";
    }
    cell.textLabel.text = txt;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSInteger row = indexPath.row;
    if (row==0) {
        //DDLogInfo(@"点击添加埋点");
    }else if(row==1){
        //DDLogError(@"点击添加文件 点击添加文件 点击添加文件 点击添加文件 点击添加文件 点击添加文件 点击添加文件");
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults setObject:@"yixiangContent" forKey:@"yixiangTest"];
        [defaults synchronize];
        
        //Document目录中添加一条json文件
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *dPath = [paths objectAtIndex:0];
        NSString *filePath = [dPath stringByAppendingPathComponent:@"yixiang.txt"];
        NSDictionary *dic = @{
                                     @"name":@"yixiang",
                                     @"object":@{
                                             @"age":@15,
                                             @"sex":@"boy"
                                             }
                                     };
        
        
        [dic writeToFile:filePath atomically:NO];
    }else if(row==2){
//        UIViewController *vc = [[OscillogramViewController alloc] init];
//        [self.navigationController pushViewController:vc animated:NO];
    }else if(row==3){
        [self sendURLConnection];
    }else if(row==4){
        [self sendNSURLSession];
    }
}

- (void)sendURLConnection{
    
    //发送一条异步请求，block方式
    NSURL *url = [NSURL URLWithString:@"http://172.23.160.149:8080/YxReactServerDemo/message/getAllMessage"];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL:url];
    [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init]completionHandler:^(NSURLResponse * _Nullable response, NSData * _Nullable data, NSError * _Nullable connectionError) {
        if ((data != nil) && (connectionError == nil)) {
            NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            NSLog(@"response == %@",jsonDict);
        }
        
    }];
    
    //发送一条异步请求，delegate方式
    //NSURLConnection *connect = [[NSURLConnection alloc]initWithRequest:request delegate:self startImmediately:NO];
    //[connect cancel]; 取消
    //[connect start];
}

- (void)sendNSURLSession{
    //发送一条异步请求，block方式
    NSURLSession *session = [NSURLSession sharedSession];
    NSURL *url = [NSURL URLWithString:@"http://172.23.160.149:8080/YxReactServerDemo/message/getAllMessage"];
    // 通过URL初始化task,在block内部可以直接对返回的数据进行处理
    NSURLSessionTask *task = [session dataTaskWithURL:url
                                    completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                                        if (error) {
                                            NSLog(@"error == %@",error);
                                            return ;
                                        }
                                        NSDictionary *result = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
                                        NSLog(@"%@", result);
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
