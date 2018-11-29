//
//  DoraemonDemoPerformanceViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoPerformanceViewController.h"
#import "UIView+DoraemonPositioning.h"
#import <AFNetworking/AFNetworking.h>

@interface DoraemonDemoPerformanceViewController (){
    Byte  *_addMemory;
}

@property (nonatomic, assign) BOOL highCPU;
@property (nonatomic, strong) NSThread *cpuThread;
@property (nonatomic, assign) BOOL highMemory;
@property (nonatomic, strong) NSThread *memoryThread;

@property (nonatomic, strong) UIButton *btn0;
@property (nonatomic, strong) UIButton *btn1;
@property (nonatomic, strong) UIButton *btn2;
@property (nonatomic, strong) UIButton *btn3;
@property (nonatomic, strong) UIButton *btn4;

@end

@implementation DoraemonDemoPerformanceViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"性能测试Demo";
    
    _highCPU = NO;
    _highMemory = NO;
    
    UIButton *btn0 = [[UIButton alloc] initWithFrame:CGRectMake(0, 20, self.view.doraemon_width, 60)];
    btn0.backgroundColor = [UIColor orangeColor];
    [btn0 setTitle:@"低FPS操作打开" forState:UIControlStateNormal];
    [btn0 addTarget:self action:@selector(fpsClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn0];
    _btn0 = btn0;
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, 120, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:@"高CPU操作打开" forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(cpuClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    _btn1 = btn1;
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, 220, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:@"高内存操作打开" forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(memoryClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    _btn2 = btn2;
    
    UIButton *btn3 = [[UIButton alloc] initWithFrame:CGRectMake(0, 320, self.view.doraemon_width, 60)];
    btn3.backgroundColor = [UIColor orangeColor];
    [btn3 setTitle:@"高流量操作打开" forState:UIControlStateNormal];
    [btn3 addTarget:self action:@selector(flowClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
    _btn3 = btn3;
    
    UIButton *btn4 = [[UIButton alloc] initWithFrame:CGRectMake(0, 420, self.view.doraemon_width, 60)];
    btn4.backgroundColor = [UIColor orangeColor];
    [btn4 setTitle:@"卡顿操作打开" forState:UIControlStateNormal];
    [btn4 addTarget:self action:@selector(anrClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn4];
    _btn4 = btn4;
    
}

- (void)dealloc{
    if (_addMemory) {
        free(_addMemory);
        _addMemory = nil;
    }
}

- (void)fpsClick{
    [NSThread sleepForTimeInterval:0.5];
}

- (void)cpuClick{
    _highCPU = !_highCPU;
    if (_highCPU) {
        _cpuThread = [[NSThread alloc] initWithTarget:self selector:@selector(highCPUOperate) object:nil];
        _cpuThread.name = @"HighCPUThread";
        [_cpuThread start];
        
        [_btn1 setTitle:@"高CPU操作关闭" forState:UIControlStateNormal];
    }else{
        [_cpuThread cancel];
        _cpuThread = nil;
        
        [_btn1 setTitle:@"高CPU操作打开" forState:UIControlStateNormal];
    }
}

- (void)memoryClick{
    _highMemory = !_highMemory;
    if (_highMemory) {
        _memoryThread = [[NSThread alloc] initWithTarget:self selector:@selector(highMemoryOperate) object:nil];
        _memoryThread.name = @"HighMemoryThread";
        [_memoryThread start];
        
        [_btn2 setTitle:@"高内存操作关闭" forState:UIControlStateNormal];
    }else{
        [_memoryThread cancel];
        _memoryThread = nil;
        
        [_btn2 setTitle:@"高内存操作打开" forState:UIControlStateNormal];
    }
}

- (void)flowClick{
    for(int i=0 ; i<10; i++){
//        AFHTTPSessionManager *session = [AFHTTPSessionManager manager];
//        session.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
//        session.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
//        session.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
//        [session GET:@"https://www.taobao.com/" parameters:nil success:^(NSURLSessionDataTask *task, id responseObject) {
//            NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
//            NSLog(@"请求成功 %@",string);
//        } failure:^(NSURLSessionDataTask *task, NSError *error) {
//            NSLog(@"请求失败");
//        }];
        
        AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
        manager.requestSerializer = [AFHTTPRequestSerializer serializer];// 请求
        manager.responseSerializer = [AFHTTPResponseSerializer serializer];// 响应
        manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/json",@"text/javascript",@"text/html", nil];
        [manager GET:@"https://www.taobao.com/" parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
            NSString *string = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
            NSLog(@"请求成功 %@",string);
        } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
            NSLog(@"请求失败");
        }];
    }

}


- (void)highCPUOperate{
    while (TRUE) {
        if ([[NSThread currentThread] isCancelled]) {
            [NSThread exit];
        }
    }
}


- (void)highMemoryOperate{
    int addedMemSize = 400;
    int interval = 2;
    while (TRUE) {
        
        if ([[NSThread currentThread] isCancelled]) {
            [NSThread exit];
        }
        if (!_addMemory) {
            _addMemory = malloc(1024*1024*addedMemSize);
            if (_addMemory) {
                memset(_addMemory, 0, 1024*1024*addedMemSize);
            }else{
                NSLog(@"add mem failed!");
            }
            
        }
        
        
        [NSThread sleepForTimeInterval:interval];
        if ([[NSThread currentThread] isCancelled]) {
            [NSThread exit];
        }
        
        if (_addMemory) {
            free(_addMemory);
            _addMemory = nil;
            
        }
        
        [NSThread sleepForTimeInterval:interval];
    }
}

- (void)anrClick{
    [NSThread sleepForTimeInterval:1.5];
//    for(int i=0 ; i< 50000; i++){
//        UIView *v = [[UIView alloc] init];
//        v.frame = CGRectMake(0, 100, 100, 100);
//        v.backgroundColor = [UIColor redColor];
//        [self.view addSubview:v];
//    }
}

@end
