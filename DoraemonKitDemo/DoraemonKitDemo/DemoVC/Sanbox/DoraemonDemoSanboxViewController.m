//
//  DoraemonDemoSanboxViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoSanboxViewController.h"
#import "UIView+DoraemonPositioning.h"

@interface DoraemonDemoSanboxViewController ()

@end

@implementation DoraemonDemoSanboxViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"沙盒测试Demo";
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 20, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:@"添加一条json到沙盒中" forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(addFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, 120, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:@"添加一张图片到沙盒中" forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(addImageFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
}

- (void)addFile{
    // 获取沙盒主目录路径
    //NSString *homeDir = NSHomeDirectory();
    // 获取Documents目录路径
    //NSString *docDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    // 获取Library的目录路径
    //NSString *libDir = [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES) lastObject];
    // 获取Caches目录路径
    //NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    // 获取tmp目录路径
    //NSString *tmpDir =  NSTemporaryDirectory();
    
    //向Document目录中添加一条json数据
    NSDictionary *dic = @{
                          @"name":@"yixiang",
                          @"age":@16
                          };
    NSString *json = [self dictToJsonStr:dic];
    
    NSString *docDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    NSString *dirPath = [docDir stringByAppendingPathComponent:@"test"];
    NSFileManager *fm = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL existed = [fm fileExistsAtPath:dirPath isDirectory:&isDir];
    if (!(isDir && existed)) {
        [fm createDirectoryAtPath:dirPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *filePath = [dirPath stringByAppendingPathComponent:@"json.txt"];
    NSString *filePath2 = [dirPath stringByAppendingPathComponent:@"json2.txt"];
    
    [json writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
    [json writeToFile:filePath2 atomically:YES encoding:NSUTF8StringEncoding error:nil];
}

- (void)addImageFile{
    UIImage *image2 = [UIImage imageNamed:@"zhaoliyin.jpg"];
    NSString *path_document = NSHomeDirectory();
    NSString *imagePath = [path_document stringByAppendingString:@"/Documents/zhaoliyin.jpg"];
    [UIImagePNGRepresentation(image2) writeToFile:imagePath atomically:YES];
}

- (NSString *)dictToJsonStr:(NSDictionary *)dict{
    NSString *jsonString = nil;
    if ([NSJSONSerialization isValidJSONObject:dict])
    {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
        jsonString =[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        //NSLog(@"json data:%@",jsonString);
        if (error) {
            NSLog(@"Error:%@" , error);
        }
    }
    return jsonString;
}

@end
