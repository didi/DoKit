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
#import <sqlite3.h>

@interface DoraemonDemoSanboxViewController ()

@end

@implementation DoraemonDemoSanboxViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"沙盒测试Demo");
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:DoraemonDemoLocalizedString(@"添加一条json到沙盒中") forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(addFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:DoraemonDemoLocalizedString(@"添加一张图片到沙盒中") forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(addImageFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:DoraemonDemoLocalizedString(@"添加一段mp4到沙盒中") forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(addMP4File) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];
    
    UIButton *btn3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn3.backgroundColor = [UIColor orangeColor];
    [btn3 setTitle:DoraemonDemoLocalizedString(@"添加doc、xlsx、pdf到沙盒中") forState:UIControlStateNormal];
    [btn3 addTarget:self action:@selector(addOtherFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn3];
    
    UIButton *btn4 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn3.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn4.backgroundColor = [UIColor orangeColor];
    [btn4 setTitle:DoraemonDemoLocalizedString(@"添加html到沙盒中") forState:UIControlStateNormal];
    [btn4 addTarget:self action:@selector(addHtmlFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn4];
    
    UIButton *btn5 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn4.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn5.backgroundColor = [UIColor orangeColor];
    [btn5 setTitle:DoraemonDemoLocalizedString(@"添加DB到沙盒中") forState:UIControlStateNormal];
    [btn5 addTarget:self action:@selector(addDBFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn5];
    
    UIButton *btn6 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn5.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn6.backgroundColor = [UIColor orangeColor];
    [btn6 setTitle:DoraemonDemoLocalizedString(@"添加Webp到沙盒中") forState:UIControlStateNormal];
    [btn6 addTarget:self action:@selector(addWebPFile) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn6];
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

- (void)addWebPFile{
    [self copyBundleToSanboxWithName:@"WebpDemo" type:@"webp"];
}

- (void)addDBFile{
    NSString *path_document = NSHomeDirectory();
    NSString *appendingString = [NSString stringWithFormat:@"/Documents/doraemon.db"];
    NSString *toPath = [path_document stringByAppendingString:appendingString];
    const char *filename = [toPath UTF8String];
    sqlite3 *db = nil;
    int result = sqlite3_open(filename, &db);
    if(result == SQLITE_OK){
        NSLog(@"open sqlite success");
    }else{
        NSLog(@"open sqlite failure");
    }
    
    //2.创建表
    const char  *sql = "CREATE TABLE IF NOT EXISTS t_students (id integer PRIMARY KEY AUTOINCREMENT,name text NOT NULL,age integer NOT NULL);";
    char *errmsg = NULL;
    result = sqlite3_exec(db, sql, NULL, NULL, &errmsg);
    if (result == SQLITE_OK) {
        NSLog(@"create table success");
    }else{
        NSLog(@"create table failure----%s",errmsg);
    }
    
    //插入数据
    for (int i=0; i<20; i++) {
        //1.拼接SQL语句
        NSString *name=[NSString stringWithFormat:@"TestText--%d",arc4random_uniform(100)];
        int age=arc4random_uniform(20)+10;
        NSString *sql=[NSString stringWithFormat:@"INSERT INTO t_students (name,age) VALUES ('%@',%d);",name,age];
        
        //2.执行SQL语句
        char *errmsg=NULL;
        sqlite3_exec(db, sql.UTF8String, NULL, NULL, &errmsg);
        if (errmsg) {//如果有错误信息
            NSLog(@"insert failure--%s",errmsg);
        }else
        {
            NSLog(@"insert success");
        }
    }
}

- (void)copyBundleToSanboxWithName:(NSString *)name type:(NSString *)type{
    NSFileManager *fileManage = [NSFileManager defaultManager];
    
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *path = [bundle pathForResource:name ofType:type];
    if(![fileManage fileExistsAtPath:path]){
        NSLog(@"file not exist");
        return;
    }
    NSString *fromPath = path;
    NSString *path_document = NSHomeDirectory();
    NSString *appendingString = [NSString stringWithFormat:@"/Documents/%@.%@",name,type];
    NSString *toPath = [path_document stringByAppendingString:appendingString];
    if (![fileManage fileExistsAtPath:toPath]) {
        BOOL isSuccess = [fileManage copyItemAtPath:fromPath toPath:toPath error:nil];
        NSLog(@"name=%@ %@",name,isSuccess ? @"copy success" : @"copy failure");
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
