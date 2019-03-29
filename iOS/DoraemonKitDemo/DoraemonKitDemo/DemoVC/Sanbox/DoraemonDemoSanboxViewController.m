//
//  DoraemonDemoSanboxViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoSanboxViewController.h"
#import <DoraemonKit/UIView+Doraemon.h>
#import "DoraemonDefine.h"

@interface DoraemonDemoSanboxViewController ()

@end

@implementation DoraemonDemoSanboxViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"沙盒测试Demo";
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:@"添加一条json到沙盒中" forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(addFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:@"添加一张图片到沙盒中" forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(addImageFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:@"添加一段mp4到沙盒中" forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(addMP4File) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    
    UIButton *btn3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn3.backgroundColor = [UIColor orangeColor];
    [btn3 setTitle:@"添加doc、xlsx、pdf到沙盒中" forState:UIControlStateNormal];
    [btn3 addTarget:self action:@selector(addOtherFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
    
    UIButton *btn4 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn3.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn4.backgroundColor = [UIColor orangeColor];
    [btn4 setTitle:@"添加html到沙盒中" forState:UIControlStateNormal];
    [btn4 addTarget:self action:@selector(addHtmlFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn4];
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
    NSString *filePath = [docDir stringByAppendingPathComponent:@"json.txt"];
    
    [json writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
    
}

- (void)addImageFile{
    UIImage *image2 = [UIImage imageNamed:@"zhaoliyin.jpg"];
    NSString *path_document = NSHomeDirectory();
    NSString *imagePath = [path_document stringByAppendingString:@"/Documents/zhaoliyin.jpg"];
    [UIImagePNGRepresentation(image2) writeToFile:imagePath atomically:YES];
}

- (void)addMP4File{
    [self copyBundleToSanboxWithName:@"huoying" type:@"mp4"];
}

- (void)addOtherFile{
    [self copyBundleToSanboxWithName:@"Doraemon" type:@"docx"];
    [self copyBundleToSanboxWithName:@"Doraemon" type:@"pdf"];
    [self copyBundleToSanboxWithName:@"Doraemon" type:@"xlsx"];
}

- (void)addHtmlFile{
    [self copyBundleToSanboxWithName:@"doraemon" type:@"html"];
}

- (void)copyBundleToSanboxWithName:(NSString *)name type:(NSString *)type{
    NSFileManager *fileManage = [NSFileManager defaultManager];
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *path = [bundle pathForResource:name ofType:type];
    if(![fileManage fileExistsAtPath:path]){
        NSLog(@"文件不存在");
        return;
    }
    NSString *fromPath = path;
    NSString *path_document = NSHomeDirectory();
    NSString *appendingString = [NSString stringWithFormat:@"/Documents/%@.%@",name,type];
    NSString *toPath = [path_document stringByAppendingString:appendingString];
    if (![fileManage fileExistsAtPath:toPath]) {
        BOOL isSuccess = [fileManage copyItemAtPath:fromPath toPath:toPath error:nil];
        NSLog(@"name=%@ %@",name,isSuccess ? @"拷贝成功" : @"拷贝失败");
    }
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
